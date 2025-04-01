package com.example.springboot_iot;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

// Based on https://docs.spring.io/spring-kafka/reference/streams.html#kafka-streams-example

@Configuration
@EnableKafka
@EnableKafkaStreams
@RegisterReflectionForBinding(TemperatureData.class) // this must be registered in a bean
public class TemperatureProcessor {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.application.name}")
    private String applicationId;

    private static final String INPUT_TOPIC = "temperature-celsius";
    private static final String OUTPUT_TOPIC = "temperature-fahrenheit-springboot";

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, TemperatureData> kStream(StreamsBuilder builder) {
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

        return stream;
    }
}
