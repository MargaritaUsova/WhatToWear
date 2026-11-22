package com.margaritausova.whattowear.domain

data class Weather (
    val temperature: Double,
    val apparentTemperature: Double,
    val weatherCode: Int,
    val windSpeed: Double,
    val temperatureSign: String = "°C",
    val windSpeedSign: String = "m/s"
)