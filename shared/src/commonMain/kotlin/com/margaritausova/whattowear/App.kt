package com.margaritausova.whattowear

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.margaritausova.whattowear.data.WeatherApi
import com.margaritausova.whattowear.data.WeatherRepositoryImpl
import com.margaritausova.whattowear.domain.Weather
import com.margaritausova.whattowear.ui.WeatherScreen
import com.margaritausova.whattowear.ui.WeatherViewModel

@Composable
@Preview
fun App() {
    val viewModel = viewModel {
        WeatherViewModel(repository = WeatherRepositoryImpl(
            api = WeatherApi()
        ))
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    MaterialTheme {
        WeatherScreen(state)
    }
}