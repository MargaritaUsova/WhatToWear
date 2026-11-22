package com.margaritausova.whattowear.data

import com.margaritausova.whattowear.domain.Weather
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WeatherApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getWeather(): WeatherDto {
        val response = client.get("https://api.open-meteo.com/v1/forecast?latitude=55.75&longitude=37.62&current=temperature_2m,apparent_temperature,weather_code,wind_speed_10m&wind_speed_unit=ms&timezone=auto")
        val weatherDto: WeatherDto = response.body<WeatherDto>()
        return weatherDto
    }
}