package com.margaritausova.whattowear.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.margaritausova.whattowear.domain.Weather

@Composable
fun WeatherScreen(state: WeatherUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            is WeatherUiState.Content -> WeatherStateScreen(state.weather)
            is WeatherUiState.Error -> ErrorStateScreen(state.error)
            WeatherUiState.Loading -> LoadingStateScreen()
        }

    }
}

@Composable
fun WeatherStateScreen(weather: Weather) {
    Text("Москва")
    Text("Температура: " + weather.temperature + weather.temperatureSign)
    Text("Ощущается как: " + weather.apparentTemperature + weather.temperatureSign)
    Text("Скорость ветра: " + weather.windSpeed + weather.windSpeedSign)
}

@Composable
fun LoadingStateScreen() {
    Text("Загружаем погоду!")
}

@Composable
fun ErrorStateScreen(e: Exception) {
    Text("Что-то пошло не так")
    Text("Ошибка: " + e.message)
}