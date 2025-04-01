#!/bin/bash

# Usage: ./temperature-load.sh <num_messages> <throughput>
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <num_messages> <throughput>"
    exit 1
fi

NUM_MESSAGES=$1   # Total messages to produce
THROUGHPUT=$2     # Messages per second
TOPIC="temperature-celsius"
BROKER="kafka-kraft:29092"
TIMESTAMP=$(date +"%Y%m%d%H%M%S")  # Current timestamp (YYYYMMDDHHMMSS)
PAYLOAD_FILE="/tmp/temperature_payload_$TIMESTAMP.json"

# Generate the payload file
echo "Generating $NUM_MESSAGES messages in $PAYLOAD_FILE..."
> "$PAYLOAD_FILE"  # Clear the file before writing

for ((i=1; i<=NUM_MESSAGES; i++)); do
    DEVICE_ID=$((RANDOM % 50 + 1))
    TEMPERATURE=$((RANDOM % 19 + 16))
    GEO_HASH="7h7k6jhpp4e2wfu9"
    echo "{\"deviceId\": $DEVICE_ID, \"geoHashCoordinate\": \"$GEO_HASH\", \"temperature\": $TEMPERATURE}" >> "$PAYLOAD_FILE"
done

echo "Payload file $PAYLOAD_FILE generated successfully."

# Produce messages using kafka-producer-perf-test.sh
echo "Producing $NUM_MESSAGES messages at a rate of $THROUGHPUT messages/sec to topic '$TOPIC'..."
/opt/kafka/bin/kafka-producer-perf-test.sh --topic "$TOPIC" \
    --num-records "$NUM_MESSAGES" \
    --throughput "$THROUGHPUT" \
    --payload-file "$PAYLOAD_FILE" \
    --producer-props bootstrap.servers="$BROKER" \
    --print-metrics

echo "Message production completed."
