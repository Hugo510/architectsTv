package com.example.feature_ui_tv.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onComplete: () -> Unit) {
    var scale by remember { mutableStateOf(0.2f) }
    val animatedScale by animateFloatAsState(
        targetValue    = scale,
        animationSpec  = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        delay(400)       // pequeña pausa
        scale = 1f       // inicia expansión
        delay(800)       // espera fin de anim
        onComplete()     // notifica fin
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(80.dp))
    }
}
