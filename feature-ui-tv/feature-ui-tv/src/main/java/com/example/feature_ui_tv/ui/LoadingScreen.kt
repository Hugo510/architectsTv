package com.example.feature_ui_tv.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.rememberScreenConfig

@Composable
fun LoadingScreen(onComplete: () -> Unit) {
    val screenConfig = rememberScreenConfig()
    
    // Estados de animación
    var scale by remember { mutableStateOf(0.2f) }
    var alpha by remember { mutableStateOf(0f) }
    var showContent by remember { mutableStateOf(false) }
    
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "scale"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 400),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        // Fase 1: Aparece el contenido
        showContent = true
        alpha = 1f
        delay(200)
        
        // Fase 2: Escala el indicador de progreso
        scale = 1f
        delay(800)
        
        // Fase 3: Desaparece gradualmente
        alpha = 0f
        delay(400)
        
        // Fase 4: Completa y navega
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { 
                scaleX = animatedScale
                scaleY = animatedScale
                this.alpha = animatedAlpha
            },
        contentAlignment = Alignment.Center
    ) {
        // Header components que se mantienen
        LogoHeader(
            logoUrl = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier = Modifier
                .align(Alignment.TopStart)
                .graphicsLayer { this.alpha = 1f } // Mantiene opacidad fija
        )

        ClockDate(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .graphicsLayer { this.alpha = 1f } // Mantiene opacidad fija
        )

        // Contenido central animado
        if (showContent) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(if (screenConfig.isSmallScreen) 60.dp else 80.dp),
                    strokeWidth = if (screenConfig.isSmallScreen) 4.dp else 6.dp
                )
                
                Spacer(Modifier.height(screenConfig.cardSpacing * 2))
                
                Text(
                    text = "Cargando...",
                    fontSize = (20 * screenConfig.textScale).sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(Modifier.height(screenConfig.cardSpacing))
                
                Text(
                    text = "Preparando visualización",
                    fontSize = (14 * screenConfig.textScale).sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}
