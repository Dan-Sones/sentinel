#!/bin/bash

create_topic() {
  local topic=$1
  local bootstrap=${KAFKA_BOOTSTRAP_SERVER:-localhost:9092}
  /usr/bin/kafka-topics --bootstrap-server "$bootstrap" \
    --create --if-not-exists \
    --topic "$topic" \
    --replication-factor 1 \
    --partitions 1
}

KAFKA_BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVER:-localhost:9092}

until /usr/bin/kafka-topics --bootstrap-server "$KAFKA_BOOTSTRAP_SERVER" --list; do
  echo "Waiting for Kafka to be ready at $KAFKA_BOOTSTRAP_SERVER..."
  sleep 5
done

create_topic clicks
create_topic conversions