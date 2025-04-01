package com.example.springboot_iot;

public record AverageTemperature(int deviceId, long hits, Double sumOfTemperatures, Double averageTemperature)
{
}
