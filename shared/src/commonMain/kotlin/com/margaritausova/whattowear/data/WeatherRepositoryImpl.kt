package com.margaritausova.whattowear.data

import com.margaritausova.whattowear.domain.Coordinates
import com.margaritausova.whattowear.domain.Weather
import com.margaritausova.whattowear.domain.WeatherRepository

class WeatherRepositoryImpl(private val api: WeatherApi) : WeatherRepository {
    override suspend fun getWeather(coordinates: Coordinates): Weather {
        val current = api.getWeather(coordinates).current
        return Weather(
            temperature = current.temperature,
            apparentTemperature = current.apparentTemperature,
            weatherCode = current.weatherCode,
            windSpeed = current.windSpeed
        )
    }
}
