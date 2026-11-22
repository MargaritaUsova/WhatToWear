package com.margaritausova.whattowear.ui
import com.margaritausova.whattowear.domain.Weather

sealed interface WeatherUiState {
    data object Loading: WeatherUiState
    data class Content(val weather: Weather): WeatherUiState
    data class Error(val error: Exception): WeatherUiState
}