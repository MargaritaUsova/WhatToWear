package com.margaritausova.whattowear

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.margaritausova.whattowear.data.WeatherApi
import com.margaritausova.whattowear.data.WeatherRepositoryImpl
import com.margaritausova.whattowear.domain.LocationState
import com.margaritausova.whattowear.ui.WeatherScreen
import com.margaritausova.whattowear.ui.WeatherViewModel

@Composable
fun App(location: LocationState, onLocate: () -> Unit) {
    val weatherViewModel = viewModel {
        val api = WeatherApi()
        WeatherViewModel(WeatherRepositoryImpl(api), api::close)
    }
    val position by location.state.collectAsStateWithLifecycle()
    val state by weatherViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(position) {
        position.coordinates?.let(weatherViewModel::load)
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF17191C))) {
        WeatherScreen(state, position, onLocate) {
            position.coordinates?.let(weatherViewModel::load)
        }
    }
}
