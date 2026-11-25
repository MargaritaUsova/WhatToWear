package com.margaritausova.whattowear.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.margaritausova.whattowear.domain.LocationStatus
import com.margaritausova.whattowear.domain.Weather
import com.margaritausova.whattowear.domain.condition
import com.margaritausova.whattowear.domain.outfit
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val Ink = Color(0xFF17191C)

@Composable
fun WeatherScreen(
    state: WeatherUiState,
    location: LocationStatus,
    onLocate: () -> Unit,
    onRetry: () -> Unit
) {
    val weather = (state as? WeatherUiState.Content)?.weather.takeIf { location.coordinates != null }
    val accent = when {
        weather == null -> Color(0xFFBCCCE8)
        weather.apparentTemperature < 0 -> Color(0xFFB0A0DA)
        weather.weatherCode >= 51 -> Color(0xFF79A8D5)
        weather.weatherCode <= 2 -> Color(0xFFF3B193)
        else -> Color(0xFFA8BCCE)
    }
    Box(Modifier.fillMaxSize().background(Color(0xFFF0F3F4))) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Brush.radialGradient(
                listOf(accent, accent.copy(alpha = 0.4f), Color.Transparent),
                center = Offset(size.width * 0.85f, size.height * 0.42f),
                radius = size.height * 0.65f
            ))
            drawRect(Brush.radialGradient(
                listOf(Color(0xFFFFE8B5).copy(alpha = 0.65f), Color.Transparent),
                center = Offset(0f, size.height * 0.3f),
                radius = size.width
            ))
        }
        Column(
            Modifier.safeContentPadding().fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("WHAT TO WEAR", fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("СЕЙЧАС", fontSize = 11.sp, letterSpacing = 1.sp, color = Ink.copy(alpha = 0.55f))
            }
            when {
                location.loading -> MessageContent("Где ты сейчас?", "Определяем местоположение…", loading = true)
                location.message != null -> MessageContent("Нужна геопозиция.", location.message, action = "Попробовать снова", onClick = onLocate)
                location.coordinates == null -> MessageContent("Одевайся по погоде.", "Покажем погоду рядом и подскажем, что надеть. Разреши доступ к местоположению при использовании приложения.", action = "Определить местоположение", onClick = onLocate)
                state is WeatherUiState.Loading -> MessageContent("Узнаём погоду.", "Подбираем одежду для твоего дня.", loading = true)
                state is WeatherUiState.Error -> MessageContent("Не вышло. Попробуем?", state.message, action = "Повторить", onClick = onRetry)
                weather != null -> WeatherContent(weather, onLocate)
            }
            Text("Данные: Open-Meteo", fontSize = 11.sp, color = Ink.copy(alpha = 0.45f))
        }
    }
}

@Composable
private fun WeatherContent(weather: Weather, onLocate: () -> Unit) {
    val outfit = weather.outfit()
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        WeatherIcon(weather.weatherCode)
        Column(horizontalAlignment = Alignment.End) {
            Text("ТВОЁ МЕСТОПОЛОЖЕНИЕ", fontSize = 10.sp, letterSpacing = 1.sp, color = Ink.copy(alpha = 0.6f))
            TextButton(onClick = onLocate, contentPadding = PaddingValues(0.dp)) { Text("Обновить ↗", color = Ink) }
        }
    }
    Spacer(Modifier.height(16.dp))
    Text("${weather.temperature.roundToInt()}°", fontSize = 112.sp, lineHeight = 112.sp, fontWeight = FontWeight.Bold, letterSpacing = (-7).sp, color = Ink)
    Text(weather.condition() + ".", fontSize = 46.sp, lineHeight = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = (-2).sp, color = Ink)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Metric("ОЩУЩАЕТСЯ", "${weather.apparentTemperature.roundToInt()}°")
        Metric("ВЕТЕР", "${weather.windSpeed.roundToInt()} м/с")
    }
    HorizontalDivider(color = Ink.copy(alpha = 0.3f))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(outfit.title.uppercase(), fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = Ink.copy(alpha = 0.65f))
        Text(outfit.items, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Medium, color = Ink)
        Text(outfit.extra, fontSize = 15.sp, lineHeight = 22.sp, color = Ink.copy(alpha = 0.65f))
    }
    HorizontalDivider(color = Ink.copy(alpha = 0.3f))
    Text("Твой день. Твоя погода.", fontSize = 28.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = Ink)
}

@Composable
private fun Metric(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 10.sp, letterSpacing = 1.sp, color = Ink.copy(alpha = 0.55f))
        Text(value, fontSize = 24.sp, color = Ink)
    }
}

@Composable
private fun MessageContent(title: String, message: String, loading: Boolean = false, action: String = "", onClick: () -> Unit = {}) {
    Spacer(Modifier.height(40.dp))
    WeatherIcon(2)
    Spacer(Modifier.height(24.dp))
    Text(title, fontSize = 48.sp, lineHeight = 50.sp, fontWeight = FontWeight.Bold, letterSpacing = (-2).sp, color = Ink)
    Text(message, fontSize = 17.sp, lineHeight = 25.sp, color = Ink.copy(alpha = 0.65f))
    if (loading) CircularProgressIndicator(Modifier.size(28.dp), color = Ink, strokeWidth = 2.dp)
    if (action.isNotEmpty()) Button(onClick = onClick, shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(18.dp)) {
        Text(action)
    }
}

@Composable
private fun WeatherIcon(code: Int) {
    Canvas(Modifier.size(80.dp)) {
        val unit = size.width / 100f
        val stroke = Stroke(4f * unit)
        if (code <= 2) {
            drawCircle(Ink, 18f * unit, Offset(50f * unit, 50f * unit), style = stroke)
            repeat(8) { index ->
                val angle = index * kotlin.math.PI / 4
                drawLine(Ink,
                    Offset((50 + cos(angle) * 29).toFloat() * unit, (50 + sin(angle) * 29).toFloat() * unit),
                    Offset((50 + cos(angle) * 40).toFloat() * unit, (50 + sin(angle) * 40).toFloat() * unit), 4f * unit)
            }
        } else {
            val cloud = Path().apply {
                moveTo(22f * unit, 64f * unit)
                cubicTo(0f, 64f * unit, 0f, 34f * unit, 25f * unit, 34f * unit)
                cubicTo(25f * unit, 4f * unit, 65f * unit, 4f * unit, 69f * unit, 32f * unit)
                cubicTo(98f * unit, 28f * unit, 104f * unit, 64f * unit, 78f * unit, 64f * unit)
                close()
            }
            drawPath(cloud, Ink, style = stroke)
            if (code >= 51) repeat(3) { index ->
                val x = (30 + index * 23) * unit
                drawLine(Ink, Offset(x, 76f * unit), Offset(x - 7f * unit, 91f * unit), 4f * unit)
            }
        }
    }
}
