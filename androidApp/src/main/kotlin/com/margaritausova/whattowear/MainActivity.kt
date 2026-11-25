package com.margaritausova.whattowear

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.margaritausova.whattowear.domain.LocationState

class MainActivity : ComponentActivity() {
    private val position = LocationState()
    private val handler = Handler(Looper.getMainLooper())
    private val manager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }
    private val timeout = Runnable {
        stopLocation()
        position.fail("Не удалось определить местоположение. Проверь, включена ли геолокация, и попробуй ещё раз.")
    }
    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            stopLocation()
            position.update(location.latitude, location.longitude)
        }
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }
    private val permission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) locate()
        else position.fail("Разреши доступ к геопозиции в настройках приложения, чтобы узнать погоду рядом.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App(position, ::requestLocation) }
    }

    private fun requestLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locate()
        } else {
            permission.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun locate() {
        stopLocation()
        position.start()
        try {
            val precise = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val providers = manager.getProviders(true).filter {
                it == LocationManager.NETWORK_PROVIDER || (precise && it == LocationManager.GPS_PROVIDER)
            }
            if (providers.isEmpty()) {
                position.fail("Включи геолокацию в настройках устройства и попробуй ещё раз.")
                return
            }
            providers.forEach { provider ->
                manager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
            }
            handler.postDelayed(timeout, 25_000)
        } catch (e: SecurityException) {
            stopLocation()
            position.fail("Нет доступа к геопозиции. Разреши его в настройках приложения.")
        } catch (e: IllegalArgumentException) {
            stopLocation()
            position.fail("Местоположение сейчас недоступно. Попробуй ещё раз.")
        }
    }

    private fun stopLocation() {
        handler.removeCallbacks(timeout)
        manager.removeUpdates(listener)
    }

    override fun onStop() {
        super.onStop()
        stopLocation()
        if (position.state.value.loading) position.fail("Определение местоположения прервано. Попробуй ещё раз.")
    }
}
