#!/bin/bash

/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-kraft:29092 --topic temperature-celsius --delete
/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-kraft:29092 --topic temperature-fahrenheit-quarkus --delete
/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-kraft:29092 --topic temperature-fahrenheit-springboot --delete
/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-kraft:29092 --topic temperature-fahrenheit-average-quarkus --delete
/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-kraft:29092 --topic temperature-fahrenheit-average-springboot --delete
