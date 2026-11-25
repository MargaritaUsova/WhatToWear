package com.margaritausova.whattowear.domain

interface WeatherRepository {
    suspend fun getWeather(coordinates: Coordinates): Weather
}
