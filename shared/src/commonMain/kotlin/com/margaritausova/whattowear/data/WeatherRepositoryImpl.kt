package com.margaritausova.whattowear.data

import com.margaritausova.whattowear.domain.Weather

class WeatherRepositoryImpl(private val api: WeatherApi) {

    suspend fun getWeather(): Weather {
        val weatherDTO = api.getWeather().current
        return Weather(
            temperature = weatherDTO.temperature,
            apparentTemperature = weatherDTO.apparentTemperature,
            weatherCode = weatherDTO.weatherCode,
            windSpeed = weatherDTO.windSpeed
        )
    }
}