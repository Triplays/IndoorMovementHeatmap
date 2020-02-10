# import time
#
# import paho.mqtt.client as mqtt
#
#
# def on_connect(a, b, c, d):
#     print("CONNECTED")
#
#
# def on_message(client, userdata, message: mqtt.MQTTMessage):
#     print("AAAAA")
#     print(message)
#
#
# broker_address = "127.0.0.1"
# client = mqtt.Client("P1")
# client.on_connect = on_connect
# client.on_message = on_message
# print("A")
# client.connect(broker_address)
# client.subscribe("indoormovementheatmap/")
#
#
# time.sleep(2000000000)
import asyncio

import password
from indoormovementheatmap import IndoorMovementHeatmap
from mariadbdatabase import MariaDbDatabase
import logging


logging.basicConfig(level=logging.INFO)

async def run():
    database = MariaDbDatabase(
        "127.0.0.1", 3306,
        "job",
        password.password,
        "indoormovementheatmap"
    )
    app = IndoorMovementHeatmap(database)
    await app.run()

asyncio.run(run())
