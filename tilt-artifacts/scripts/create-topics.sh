#!/bin/bash

/opt/kafka/bin/kafka-topics.sh --create --topic temperature-celsius --bootstrap-server kafka-kraft:29092 --partitions 1 --replication-factor 1
/opt/kafka/bin/kafka-topics.sh --create --topic temperature-fahrenheit-quarkus --bootstrap-server kafka-kraft:29092 --partitions 1 --replication-factor 1
/opt/kafka/bin/kafka-topics.sh --create --topic temperature-fahrenheit-springboot --bootstrap-server kafka-kraft:29092 --partitions 1 --replication-factor 1
/opt/kafka/bin/kafka-topics.sh --create --topic temperature-fahrenheit-average-quarkus --bootstrap-server kafka-kraft:29092 --partitions 1 --replication-factor 1
/opt/kafka/bin/kafka-topics.sh --create --topic temperature-fahrenheit-average-springboot --bootstrap-server kafka-kraft:29092 --partitions 1 --replication-factor 1
