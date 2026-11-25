package com.margaritausova.whattowear.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Coordinates(val latitude: Double, val longitude: Double)

data class LocationStatus(
    val coordinates: Coordinates? = null,
    val loading: Boolean = false,
    val message: String? = null,
    val revision: Int = 0
)

class LocationState {
    private val _state = MutableStateFlow(LocationStatus())
    val state = _state.asStateFlow()

    fun start() {
        _state.value = LocationStatus(loading = true, revision = _state.value.revision + 1)
    }

    fun update(latitude: Double, longitude: Double) {
        _state.value = LocationStatus(Coordinates(latitude, longitude), revision = _state.value.revision + 1)
    }

    fun fail(message: String) {
        _state.value = LocationStatus(message = message, revision = _state.value.revision + 1)
    }
}
