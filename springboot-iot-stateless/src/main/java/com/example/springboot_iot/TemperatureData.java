package com.example.springboot_iot;

public record TemperatureData(int deviceId, String geoHashCoordinate, double temperature) {
}
