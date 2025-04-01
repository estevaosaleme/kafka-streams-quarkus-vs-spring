package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TemperatureData(int deviceId, String geoHashCoordinate, double temperature) {
}
