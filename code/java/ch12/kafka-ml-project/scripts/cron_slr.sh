#!/bin/bash

KAFKA_HOME=/path/to/kafka
MESSAGE_HOME=/path/to/messages/dir

$KAFKA_HOME/bin/kafka-console-producer.sh --topic event_topic --broker-list localhost:9092 < $MESSAGE_HOME/build_slr.json
