#!/bin/bash


check_port() {
    nc -z localhost $1 </dev/null >/dev/null 2>&1
    return $?
}
check_topic() {
    ./bin/kafka-topics.sh --describe --zookeeper localhost:2181 | grep -q "\<$1\>"
    return $?
}

if check_port 2181; then
    echo "Port 2181 is available. Proceeding with Kafka setup."
else
    echo "Port 2181 is not available. Exiting."
    exit 1
fi


wget https://archive.apache.org/dist/kafka/0.10.0.0/kafka_2.11-0.10.0.0.tgz


tar -xf kafka_2.11-0.10.0.0.tgz
mv kafka_* kafka
cd kafka


./bin/zookeeper-server-start.sh config/zookeeper.properties &


sleep 5


echo "broker.id=0" >> config/server.properties
echo "zookeeper.connect=localhost:2181" >> config/server.properties
echo "log.dir=/tmp/kafka" >> config/server.properties


./bin/kafka-server-start.sh config/server.properties &


sleep 10


if check_topic knodb; then
    echo "Topic 'knodb' already exists. Skipping topic creation."
else

    ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic knodb
    echo "Topic 'knodb' created successfully."
fi

java -jar knodb.jar