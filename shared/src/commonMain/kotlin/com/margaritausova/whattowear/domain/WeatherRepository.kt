package com.margaritausova.whattowear.domain

interface WeatherRepository {

    suspend fun getWeather(): Weather
}