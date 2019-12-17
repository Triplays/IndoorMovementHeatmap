import asyncio
import logging

import aiomysql
from datetime import datetime

from indoormovementheatmap import IDatabase, Position, Device, Beacon


logger = logging.getLogger("database")


class MariaDbDatabase(IDatabase):

    @property
    def address(self) -> str:
        return self._address

    @property
    def port(self) -> int:
        return self._port

    def __init__(self, address: str, port: int, username: str, password: str, database: str):
        self._address = address
        self._port = port
        self._username = username
        self._password = password
        self._database_name = database
        self._connection = None

    async def __aenter__(self):
        self._connection = await aiomysql.connect(
            host=self._address,
            port=self._port,
            user=self._username,
            password=self._password,
            db=self._database_name,
            loop=asyncio.get_running_loop()
        )

        async with self._connection.cursor() as cursor:
            await cursor.execute(
                """ CREATE TABLE IF NOT EXISTS devicetypes(
                        type_id INT NOT NULL,
                        description VARCHAR(32) NOT NULL,
                        PRIMARY KEY (type_id))""")

            await cursor.execute(
                """ CREATE TABLE IF NOT EXISTS devices(
                        device_id INT NOT NULL,
                        type_id INT NOT NULL,
                        PRIMARY KEY (device_id),
                        FOREIGN KEY (type_id) REFERENCES devicetypes(type_id))""")

            await cursor.execute(
                """ CREATE TABLE IF NOT EXISTS beacons(
                        beacon_id INT NOT NULL,
                        x FLOAT NOT NULL,
                        y FLOAT NOT NULL,
                        PRIMARY KEY (beacon_id))""")

            await cursor.execute(
                """ CREATE TABLE IF NOT EXISTS positions(
                        position_id INT NOT NULL AUTO_INCREMENT,
                        device_id INT NOT NULL,
                        x FLOAT NOT NULL,
                        y FLOAT NOT NULL,
                        datetime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (position_id),
                        FOREIGN KEY (device_id) REFERENCES devices(device_id))""")

        logger.info("Initialized successfully")

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        self._connection.disconnect()

    async def store_position(self, device: Device, position: Position):
        async with self._connection.cursor() as cursor:
            await cursor.execute(
                f"""INSERT INTO positions(device_id, x, y, datetime)
                    VALUES({device.id}, {position.x}, {position.y}, {datetime.now()})"""
            )

    async def get_beacon(self, beacon_id: int) -> Beacon:
        async with self._connection.cursor() as cursor:
            await cursor.execute(
                f"""SELECT x, y
                    FROM beacons 
                    WHERE beacon_id = {beacon_id}"""
            )
            result = await cursor.fetchone()
            if not result:
                raise KeyError(f"No beacon with id={beacon_id} found.")
            return Beacon(beacon_id, Position(*result))
