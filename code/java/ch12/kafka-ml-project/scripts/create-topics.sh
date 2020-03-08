# create-topics.sh
#
# Shell script for creating the required topics for Kafka.
# Amend the partitions and replication factor to settings that reflect your cluster.
# I'm keeping things standalone...
#

KAFKA_HOME=/usr/local/kafka_2.11-1.0.0/
ZK_CONNECT=localhost:2181
REPLICATION_FACTOR=1
PARTITIONS=1

# training_data_topic - Where the raw data will be transported for training. Using Kafka Connect to push the data to a filestore.
echo "Creating topic: training_data_topic"
$KAFKA_HOME/bin/kafka-topics.sh --create --topic training_data_topic --zookeeper $ZK_CONNECT --partitions $PARTITIONS --replication-factor $REPLICATION_FACTOR

# event_stream_topic - Events are sent to the Events Kafka Streaming API application.
echo "Creating topic: event_topic"
$KAFKA_HOME/bin/kafka-topics.sh --create --topic event_topic --zookeeper $ZK_CONNECT --partitions $PARTITIONS --replication-factor $REPLICATION_FACTOR

# prediction_request_topic - Sends data to the Prediction Streaming application.
echo "Creating topic: prediction_request_topic"
$KAFKA_HOME/bin/kafka-topics.sh --create --topic prediction_request_topic --zookeeper $ZK_CONNECT --partitions $PARTITIONS --replication-factor $REPLICATION_FACTOR

# prediction_response_topic - The prediction response from the original prediction_request_topic
echo "Creating topic: prediction_response_topic"
$KAFKA_HOME/bin/kafka-topics.sh --create --topic prediction_response_topic --zookeeper $ZK_CONNECT --partitions $PARTITIONS --replication-factor $REPLICATION_FACTOR
