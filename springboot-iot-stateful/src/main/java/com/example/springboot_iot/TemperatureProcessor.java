package com.example.springboot_iot;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.state.WindowStore;
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
@RegisterReflectionForBinding(classes = {TemperatureData.class, AverageTemperature.class})  // this must be registered in a bean
public class TemperatureProcessor {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.application.name}")
    private String applicationId;

    private static final String INPUT_TOPIC = "temperature-celsius";
    private static final String OUTPUT_TOPIC = "temperature-fahrenheit-average-springboot";

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, TemperatureData> kStream(StreamsBuilder builder) {
        Duration windowDuration = Duration.ofDays(1);

        // Create JSON Serde for TemperatureData
        Serde<TemperatureData> temperatureDataJsonSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(TemperatureData.class));

        Serde<AverageTemperature> averageTemperatureDataJsonSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(AverageTemperature.class));

        // Read from the input topic using the temperatureDataJsonSerde
        KStream<String, TemperatureData> stream = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), temperatureDataJsonSerde));

        // Perform the transformation (Celsius to Fahrenheit)
        KStream<String, TemperatureData> transformedStream = stream.mapValues(value -> {
            try {
                double celsius = value.temperature();
                double fahrenheit = (celsius * 9 / 5) + 32;
                return new TemperatureData(value.deviceId(), value.geoHashCoordinate(), fahrenheit);
            } catch (Exception e) {
                return null; // Handle error case appropriately
            }
        });

        KGroupedStream<String, TemperatureData> groupedStream = transformedStream.groupBy(
            (key, value) -> String.valueOf(value.deviceId()),
            Grouped.with(Serdes.String(), temperatureDataJsonSerde)
        );

        KStream<Windowed<String>, AverageTemperature> averageStream = groupedStream
            .windowedBy(TimeWindows.ofSizeAndGrace(windowDuration, Duration.ofHours(1)))
            .aggregate(
                () -> new AverageTemperature(0, 0, 0.0, 0.0),
                (aggKey, newValue, aggValue) -> {
                    AverageTemperature newAggValue = new AverageTemperature(
                        newValue.deviceId(),
                        aggValue.hits() + 1,
                        aggValue.sumOfTemperatures() + newValue.temperature(),
                        (aggValue.sumOfTemperatures() + newValue.temperature())/(aggValue.hits() + 1)
                    );
                    return newAggValue;
                },
                Materialized.<String, AverageTemperature, WindowStore<Bytes, byte[]>>as("average-temperature-store")
                    .withRetention(windowDuration.plus(Duration.ofHours(1)))
                    .withKeySerde(Serdes.String())
                    .withValueSerde(averageTemperatureDataJsonSerde)
            )
            .toStream();

        // Write to the output topic using the temperatureDataJsonSerde
        Serde<Windowed<String>> windowedKeySerde = new WindowedSerdes.TimeWindowedSerde<>(Serdes.String(), windowDuration.toDays());
        averageStream.to(OUTPUT_TOPIC, Produced.with(windowedKeySerde, averageTemperatureDataJsonSerde));

        return stream;
    }
}
