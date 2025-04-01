package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

@ApplicationScoped
public class TemperatureProcessor {
    
    private static final String INPUT_TOPIC = "temperature-celsius";
    private static final String OUTPUT_TOPIC = "temperature-fahrenheit-quarkus";

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Create JSON Serde for TemperatureData
        Serde<TemperatureData> jsonSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(TemperatureData.class));

        // Read from the input topic using the jsonSerde
        KStream<String, TemperatureData> stream = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), jsonSerde));

        // Perform the transformation (Celsius to Fahrenheit)
        stream.mapValues(value -> {
                try {
                    double celsius = value.temperature();
                    double fahrenheit = (celsius * 9 / 5) + 32;
                    return new TemperatureData(value.deviceId(), value.geoHashCoordinate(), fahrenheit);
                } catch (Exception e) {
                    return null; // Handle error case appropriately
                }
            })
            // Write to the output topic using the jsonSerde
            .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), jsonSerde));

        return builder.build();
    }
}
