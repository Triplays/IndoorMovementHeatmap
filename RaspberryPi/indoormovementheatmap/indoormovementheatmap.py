from __future__ import annotations

import asyncio
import logging
import socket
from abc import ABC, abstractmethod
from asyncio import Queue
from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from numbers import Number
from typing import Optional, Tuple, Set, List, Generator, AsyncGenerator

import aiomqtt
from aiozeroconf import Zeroconf, ServiceInfo


@dataclass
class Device:
    id: int
    type: Type

    class Type(Enum):
        BASKET = 1
        CART = 2


@dataclass
class Position:
    x: float
    y: float

    def __repr__(self):
        return f"{self.x}, {self.y}"


@dataclass
class DevicePosition:
    device: Device
    position: Position
    datetime: datetime


@dataclass
class Beacon:
    id: int
    position: Position


@dataclass
class Sample:
    device: Device
    beacon: Beacon
    rssi: float
    datetime: datetime


@dataclass
class SensorMessage:
    device: Device
    sample_a: Sample
    sample_b: Sample
    sample_c: Sample


class IDatabase(ABC):

    @abstractmethod
    async def __aenter__(self):
        pass

    @abstractmethod
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        pass

    @abstractmethod
    async def store_sample(self, sample: Sample):
        pass

    @abstractmethod
    async def store_position(self, device_position: DevicePosition) -> None:
        pass

    @abstractmethod
    async def get_beacon(self, beacon_id: int) -> Beacon:
        pass

    @abstractmethod
    async def get_device(self, device_id: int) -> Device:
        pass

    @property
    @abstractmethod
    def address(self) -> str:
        pass

    @property
    @abstractmethod
    def port(self) -> int:
        pass


class Service:

    LOGGER = logging.getLogger("zeroconf")

    def __init__(self, database: IDatabase):
        self._zeroconf: Optional[Zeroconf] = None
        self._info = ServiceInfo(
            "_movementheatmap._tcp.local.",
            "Indoor Movement Heatmap._movementheatmap._tcp.local.",
            address=socket.inet_aton(database.address),
            port=database.port,
            properties={}
        )

    async def __aenter__(self):
        self._zeroconf = Zeroconf(asyncio.get_running_loop())
        await self._zeroconf.register_service(self._info)
        Service.LOGGER.info(f"Registered on port {self._info.port}")

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        await self._zeroconf.unregister_service(self._info)
        await self._zeroconf.close()
        Service.LOGGER.info("Service unregistered")


class Mqtt:

    LOGGER = logging.getLogger("mqtt")

    def __init__(self, loop):
        self._client = None
        self._loop = loop
        self._connected = asyncio.Event(loop=self._loop)
        self._subscribed = asyncio.Event(loop=self._loop)
        self._disconnected = asyncio.Event(loop=self._loop)
        self._queue: Queue[Tuple[str, str]] = Queue()

    async def __aenter__(self):
        self._client = aiomqtt.Client(loop=self._loop)
        self._client.loop_start()

        self._client.on_connect = self._on_connect
        self._client.on_subscribe = self._on_subscribe
        self._client.on_message = self._on_message
        self._client.on_disconnect = self._on_disconnect

        await self._client.connect("localhost")
        await self._connected.wait()

        # await self._subscribed.wait()

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        self._client.disconnect()
        await self._disconnected.wait()

    async def get_next_message(self, timeout: float = 1) -> Optional[Tuple[str, str]]:
        return await self._queue.get()

    def _on_connect(self, client, userdata, flags, rc):
        Mqtt.LOGGER.info("Connected to broker")
        self._connected.set()

    def _on_subscribe(self, client, userdata, mid, granted_qos):
        self._subscribed.set()

    def _on_message(self, client, userdata, message: aiomqtt.MQTTMessage):
        Mqtt.LOGGER.info(f"Received message {message.topic} {message.payload}")
        self._queue.put_nowait((message.topic, message.payload))

    def _on_disconnect(self, client, userdata, rc):
        self._disconnected.set()


class ParseException(Exception):
    pass


class IndoorMovementHeatmap:

    def __init__(self, database: IDatabase):
        self._database = database
        self._mqtt = Mqtt(asyncio.get_running_loop())
        self._service = Service(database)
        self._closed = asyncio.Event()

    async def run(self):
        async with self._database, self._mqtt, self._service:
            while not self._closed.is_set():
                message = await self._mqtt.get_next_message()
                try:
                    samples = await self._parse_samples(*message)
                except ParseException:
                    continue

                for sample in samples:
                    await self._database.store_sample(sample)

                async for position in self._calculate_positions(samples):
                    await self._database.store_position(position)

    # topic = indoormovementheatmap/device-n
    # payload = BEACONID:RSSI:datetime

    async def _parse_samples(self, topic: str, payload: str) -> List[Sample]:  # sorted by time
        samples = []

        try:
            device = await self._database.get_device(int(topic.split("-")[1]))

            for entry in payload.split(","):

                sample = Sample(
                    device=device,
                    beacon=await self._database.get_beacon(int(entry[0])),
                    rssi=int(entry[1]),
                    datetime=None
                )
                samples.append(sample)

            return samples

        except (StopIteration, ValueError, IndexError, KeyError):
            raise ParseException()

    @staticmethod
    def _calculate_positions(samples: List[Sample]) -> AsyncGenerator[DevicePosition]:
        for i in range(len(samples)):
            yield None

    @staticmethod
    def _trilaterate(message: SensorMessage) -> Position:
        a = message.sample_a.beacon.position
        b = message.sample_b.beacon.position
        c = message.sample_c.beacon.position

        da = message.sample_a.rssi
        db = message.sample_b.rssi
        dc = message.sample_c.rssi

        w = da * da - db * db - a.x * a.x - a.y * a.y + b.x * b.x + b.y * b.y
        z = db * db - dc * dc - b.x * b.x - b.y * b.y + c.x * c.x + c.y * c.y

        x = (w * (c.y - b.y) - z * (b.y - a.y)) / (2 * ((b.x - a.x) * (c.y - b.y) - (c.x - b.x) * (b.y - a.y)))
        y1 = (w - 2 * x * (b.x - a.x)) / (2 * (b.y - a.y))
        y2 = (z - 2 * x * (c.x - b.x)) / (2 * (c.y - b.y))
        y = (y1 + y2) / 2

        return Position(x, y)
