from __future__ import annotations

import asyncio
import logging
import socket
from abc import ABC, abstractmethod
from asyncio import Queue
from dataclasses import dataclass
from enum import Enum
from typing import Optional, Tuple

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
class Beacon:
    id: int
    position: Position


@dataclass
class Sample:
    beacon: Beacon
    distance: float


@dataclass
class SensorMessage:
    device: Device
    sample_a: Sample
    sample_b: Sample
    sample_c: Sample


class IDatabase(ABC):

    @abstractmethod
    async def initialize(self) -> IDatabase:
        pass

    @abstractmethod
    async def store_position(self, position: Position, device: Device) -> None:
        pass

    @abstractmethod
    async def get_beacon(self, beacon_id: int) -> Beacon:
        pass

    @abstractmethod
    async def close(self):
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

    logger = logging.getLogger("zeroconf")

    def __init__(self, database: IDatabase):
        self._zeroconf: Optional[Zeroconf] = None
        self._info = ServiceInfo(
            "_movementheatmap._tcp.local.",
            "Indoor Movement Heatmap._movementheatmap._tcp.local.",
            address=socket.inet_aton(database.address),
            port=database.port,
            properties={}
        )

    async def register(self):
        self._zeroconf = Zeroconf(asyncio.get_running_loop())
        await self._zeroconf.register_service(self._info)
        Service.logger.info(f"Registered on port {self._info.port}")

    async def unregister(self):
        await self._zeroconf.unregister_service(self._info)
        await self._zeroconf.close()
        Service.logger.info("Service unregistered")


class Mqtt:

    logger = logging.getLogger("mqtt")

    def __init__(self):
        self._client = None
        self._connected = asyncio.Event()
        self._subscribed = asyncio.Event()
        self._queue: Queue[Tuple[str, str]] = Queue()

    async def connect(self):
        self._client = aiomqtt.Client(asyncio.get_running_loop())

        self._client.on_connect = self._on_connect
        self._client.on_subscribe = self._on_subscribe
        self._client.on_message = self._on_message
        self._client.on_disconnect = self._on_disconnect

        self._client.loop_start()

        await self._connected.wait()
        await self._subscribed.wait()

    async def get_next_message(self, timeout: float = 1) -> Optional[Tuple[str, str]]:
        try:
            return await asyncio.wait_for(self._queue.get(), timeout)
        except asyncio.TimeoutError:
            pass

    async def disconnect(self):
        self._client.disconnect()

    def _on_connect(self, client, userdata, flags, rc):
        self._connected.set()

    def _on_subscribe(self, client, userdata, mid, granted_qos):
        self._subscribed.set()

    def _on_message(self, client, userdata, message: aiomqtt.MQTTMessage):
        Mqtt.logger.info(f"Received message {message.topic} {message.payload}")
        self._queue.put_nowait((message.topic, message.payload))

    def _on_disconnect(self, client, userdata, rc):
        pass


class IndoorMovementHeatmap:

    def __init__(self, database: IDatabase):
        self._database = database
        self._mqtt = Mqtt()
        self._service = Service(database)
        self._closed = asyncio.Event()

    async def run(self):
        await self._database.initialize()
        await self._service.register()
        try:
            while not self._closed.is_set():
                message = await self._mqtt.get_next_message()
                if message:  # is not null
                    parsed = self._parse_message(*message)
                    position = self._trilaterate(parsed)
                    device = parsed.device
                    asyncio.create_task(self._database.store_position(position, device))
        finally:
            await self._service.unregister()
            await self._mqtt.disconnect()
            await self._database.close()

    @staticmethod
    def _parse_message(topic: str, payload: str) -> SensorMessage:
        pass

    @staticmethod
    def _trilaterate(message: SensorMessage) -> Position:
        a = message.sample_a.beacon.position
        b = message.sample_b.beacon.position
        c = message.sample_c.beacon.position

        da = message.sample_a.distance
        db = message.sample_b.distance
        dc = message.sample_c.distance

        w = da * da - db * db - a.x * a.x - a.y * a.y + b.x * b.x + b.y * b.y
        z = db * db - dc * dc - b.x * b.x - b.y * b.y + c.x * c.x + c.y * c.y

        x = (w * (c.y - b.y) - z * (b.y - a.y)) / (2 * ((b.x - a.x) * (c.y - b.y) - (c.x - b.x) * (b.y - a.y)))
        y1 = (w - 2 * x * (b.x - a.x)) / (2 * (b.y - a.y))
        y2 = (z - 2 * x * (c.x - b.x)) / (2 * (c.y - b.y))
        y = (y1 + y2) / 2

        return Position(x, y)
