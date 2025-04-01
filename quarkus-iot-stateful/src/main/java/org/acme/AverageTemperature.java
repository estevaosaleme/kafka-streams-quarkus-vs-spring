package org.acme;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record AverageTemperature(int deviceId, long hits, Double sumOfTemperatures, Double averageTemperature)
{
}
