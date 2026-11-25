package com.margaritausova.whattowear.data

import com.margaritausova.whattowear.domain.Coordinates
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WeatherApi {
    private val client = HttpClient {
        expectSuccess = true
        install(HttpTimeout) {
            requestTimeoutMillis = 20_000
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getWeather(coordinates: Coordinates): WeatherDto =
        client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", coordinates.latitude)
            parameter("longitude", coordinates.longitude)
            parameter("current", "temperature_2m,apparent_temperature,weather_code,wind_speed_10m")
            parameter("wind_speed_unit", "ms")
            parameter("timezone", "auto")
        }.body()

    fun close() = client.close()
}
