package com.margaritausova.whattowear.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margaritausova.whattowear.domain.Coordinates
import com.margaritausova.whattowear.domain.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val closeClient: () -> Unit = {}
) : ViewModel() {
    private val _state = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val state = _state.asStateFlow()
    private var request: Job? = null

    fun load(coordinates: Coordinates) {
        request?.cancel()
        _state.update { WeatherUiState.Loading }
        request = viewModelScope.launch {
            try {
                val weather = repository.getWeather(coordinates)
                _state.update { WeatherUiState.Content(weather) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { WeatherUiState.Error("Не удалось загрузить погоду. Проверь интернет и попробуй ещё раз.") }
            }
        }
    }

    override fun onCleared() {
        request?.cancel()
        closeClient()
    }
}
