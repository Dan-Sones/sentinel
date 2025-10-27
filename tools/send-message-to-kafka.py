import json
import time
import uuid
from datetime import datetime
import random

from confluent_kafka import Producer


class Click:
    def __init__(self, event_uuid: str, timestamp_event_time: str, ip_address: str, ad_uuid: str):
        self.event_uuid = event_uuid
        self.timestamp_event_time = timestamp_event_time
        self.ip_address = ip_address
        self.ad_uuid = ad_uuid

    def to_dict(self):
        return {
            "event_uuid": self.event_uuid,
            "timestamp_event_time": self.timestamp_event_time,
            "ip_address": self.ip_address,
            "ad_uuid": self.ad_uuid,
        }


class Conversion:
    def __init__(self, event_uuid: str, timestamp_event_time: str, ip_address: str, ad_uuid: str, conversion_type: str):
        self.event_uuid = event_uuid
        self.timestamp_event_time = timestamp_event_time
        self.ip_address = ip_address
        self.ad_uuid = ad_uuid
        self.conversion_type = conversion_type

    def to_dict(self):
        return {
            "event_uuid": self.event_uuid,
            "timestamp_event_time": self.timestamp_event_time,
            "ip_address": self.ip_address,
            "ad_uuid": self.ad_uuid,
            "conversion_type": self.conversion_type,
        }

class KafkaProducerManager:
    def __init__(self, bootstrap_servers: str):
        self.bootstrap_servers = bootstrap_servers
        self.producer = Producer({
            "bootstrap.servers": self.bootstrap_servers,
            "client.id": "jeff",
        })

    def write_json_event(self, topic: str, event: dict, key: str):
        try:
            message_value = json.dumps(event).encode("utf-8")
            self.producer.produce(topic, key=key.encode("utf-8"), value=message_value)
        except Exception as e:
            print(f"Error producing message: {e}")

    def flush(self, timeout=None):
        remaining = self.producer.flush(timeout=timeout)
        if remaining > 0:
            print(f"WARNING: {remaining} messages still in queue "
                  "after flush.")

if __name__ == "__main__":
    kafka_bootstrap_servers = "localhost:9092"
    producer = KafkaProducerManager(bootstrap_servers=kafka_bootstrap_servers)


    ad_ids = [
        "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "f0e9d8c7-b6a5-4321-9876-543210fedcba",
        "1a2b3c4d-5e6f-7080-9102-34567890abcd",
        "fedcba98-7654-3210-abcdef-01234567890a",
    ]

    ip_addresses = ["10.0.0.1", "10.0.0.2", "10.0.0.3", "10.0.0.4"]


    
    clickEvents = []

    for x in range(0, 5000):
        clickEvents.append(Click(
            event_uuid=str(uuid.uuid4()),
            timestamp_event_time=str(datetime.now().isoformat()),
            ip_address=random.choice(ip_addresses),
            ad_uuid=str(random.choice(ad_ids)),
        ))

    for event in clickEvents:
        producer.write_json_event("clicks", event.to_dict(), event.event_uuid)


    conversionEvents = []

    for x in range(0, 5000):
        conversionEvents.append(Conversion(
            event_uuid=str(uuid.uuid4()),
            timestamp_event_time=str(datetime.now().isoformat()),
            ip_address=random.choice(ip_addresses),
            ad_uuid=str(random.choice(ad_ids)),
            conversion_type=random.choice(["purchase", "signup", "download"]),
        ))

    for event in conversionEvents:
        producer.write_json_event("conversions", event.to_dict(), event.event_uuid)


        
    

    producer.flush(10) # Wait up to 10 seconds for messages to be sent



