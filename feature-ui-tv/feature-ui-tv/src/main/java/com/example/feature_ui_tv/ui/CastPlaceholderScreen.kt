package com.example.feature_ui_tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.rememberScreenConfig

@Composable
fun CastPlaceholderScreen(onCastClick: () -> Unit = {}) {
    val screenConfig = rememberScreenConfig()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Reloj en la esquina superior derecha
        ClockDate(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(screenConfig.contentPadding)
        )

        // Contenido principal centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenConfig.contentPadding * 2),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo y nombre de empresa centrados
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Logo cuadrado
                Box(
                    modifier = Modifier
                        .size(if (screenConfig.isSmallScreen) 48.dp else 64.dp)
                        .background(
                            Color(0xFF455A64),
                            RoundedCornerShape(8.dp)
                        )
                )
                
                Spacer(Modifier.width(if (screenConfig.isSmallScreen) 12.dp else 16.dp))
                
                // Nombre de empresa
                Text(
                    text = "LOGO EMPRESA",
                    fontSize = (36 * screenConfig.textScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )
            }

            Spacer(Modifier.height(if (screenConfig.isSmallScreen) 8.dp else 12.dp))

            // Subtítulo
            Text(
                text = "Visualizador de Proyectos",
                fontSize = (18 * screenConfig.textScale).sp,
                color = Color(0xFF666666)
            )

            Spacer(Modifier.height(if (screenConfig.isSmallScreen) 60.dp else 80.dp))

            // Texto principal
            Text(
                text = "Seleccione un proyecto desde su celular",
                fontSize = (24 * screenConfig.textScale).sp,
                color = Color(0xFF333333)
            )

            Spacer(Modifier.height(if (screenConfig.isSmallScreen) 40.dp else 60.dp))

            // Icono de cast
            Icon(
                imageVector = Icons.Default.Cast,
                contentDescription = "Cast icon",
                modifier = Modifier.size(
                    if (screenConfig.isSmallScreen) 80.dp else 120.dp
                ),
                tint = Color(0xFF455A64)
            )

            Spacer(Modifier.height(if (screenConfig.isSmallScreen) 40.dp else 60.dp))

            // Botón oculto para testing (mantiene funcionalidad)
            Button(
                onClick = onCastClick,
                modifier = Modifier.size(1.dp) // Muy pequeño, casi invisible
            ) {
                Text("", fontSize = 1.sp)
            }
        }
    }
}
