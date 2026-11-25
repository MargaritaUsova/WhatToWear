package com.margaritausova.whattowear

import androidx.compose.ui.window.ComposeUIViewController
import com.margaritausova.whattowear.domain.LocationState

fun MainViewController(location: LocationState, onLocate: () -> Unit) =
    ComposeUIViewController { App(location, onLocate) }
