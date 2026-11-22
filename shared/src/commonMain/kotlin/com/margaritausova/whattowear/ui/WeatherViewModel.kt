package com.margaritausova.whattowear.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.margaritausova.whattowear.data.WeatherRepositoryImpl
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.cancellation.CancellationException

class WeatherViewModel(private val repository: WeatherRepositoryImpl): ViewModel() {

    private val _state =
        MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)

    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    init {
        getWeather()
    }

    fun getWeather() {
        _state.update {
            WeatherUiState.Loading
        }
        viewModelScope.launch {
            try {
                val weather = repository.getWeather()
                _state.update {
                    WeatherUiState.Content(weather)
                }
            }catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    WeatherUiState.Error(e)
                }
            }
        }
    }
}