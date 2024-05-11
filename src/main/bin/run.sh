#!/bin/bash

cleanup() {
    echo "Cleaning up..."
    kill $(jobs -p)
}
trap cleanup SIGINT SIGTERM

check_port() {
    # Check if lsof command is available
    command -v lsof >/dev/null 2>&1 || { echo >&2 "lsof command not found. Aborting."; exit 1; }

    # Check if port is in use
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null; then
        echo "Port $1 is already in use."
        return 1
    else
        echo "Port $1 is available."
        return 0
    fi
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
if [ -d "logs" ];then
  echo "logs directory exist"
else
  mkdir "logs"
fi

kafka_setup(){
  if [ -d "kafka" ]; then
      echo "Kafka directory already exists."
      cd kafka/kafka_*
  else
wget https://archive.apache.org/dist/kafka/0.10.0.0/kafka_2.11-0.10.0.0.tgz


tar -xf kafka_2.11-0.10.0.0.tgz
mkdir "kafka"
mv kafka_* kafka
cd kafka/kafka_*
fi

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
}
kafka_setup > logs/kafka.txt 2>&1
cd ../..
echo "server started"
java -jar knodb.jar > logs/startup.txt 2>&1
echo "server killed"