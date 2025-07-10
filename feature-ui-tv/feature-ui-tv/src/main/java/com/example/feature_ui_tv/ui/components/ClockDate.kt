package com.example.feature_ui_tv.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ClockDate(
    modifier: Modifier = Modifier,
    timePattern: String = "hh:mm a",
    datePattern: String = "dd MMM, yyyy"
) {
    // Formatter singleton
    val timeFormatter = remember { DateTimeFormatter.ofPattern(timePattern) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern(datePattern) }

    // State that ticks every minute
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            // recalcula cada 30 segundos para mantenerlo en hora
            delay(30_000L)
        }
    }

    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.TopEnd) {
        // Texto de hora y fecha, con dos líneas
        Text(
            text = "${now.format(timeFormatter)}\n${now.format(dateFormatter)}",
            fontSize = 14.sp
        )
    }
}
