package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Class<T> targetType;

  public JsonDeserializer(Class<T> targetType) {
    this.targetType = targetType;
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @Override
  public T deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(data, targetType);
    } catch (Exception e) {
      throw new RuntimeException("Error deserializing JSON message", e);
    }
  }

  @Override
  public void close() {}
}

