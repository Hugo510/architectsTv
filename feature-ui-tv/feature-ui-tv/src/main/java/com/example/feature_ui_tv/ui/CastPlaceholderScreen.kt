package com.example.feature_ui_tv.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.rememberScreenConfig

@Composable
fun CastPlaceholderScreen(onCastClick: () -> Unit = {}) {
    val screenConfig = rememberScreenConfig()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = screenConfig.contentPadding * 3),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(if (screenConfig.isSmallScreen) 80.dp else 120.dp))

            Text(
                text = if (screenConfig.isSmallScreen) 
                    "Seleccione proyecto desde celular" 
                else 
                    "Seleccione un proyecto desde su celular",
                fontSize = (24 * screenConfig.textScale).sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(if (screenConfig.isSmallScreen) 32.dp else 48.dp))

            // Icono responsivo
            Icon(
                imageVector = Icons.Default.Cast,
                contentDescription = "Cast icon",
                modifier = Modifier.size(
                    if (screenConfig.isSmallScreen) 72.dp else 96.dp
                ),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(if (screenConfig.isSmallScreen) 16.dp else 24.dp))

            // Botón responsivo
            Button(onClick = onCastClick) {
                Text(
                    "Proyectar", 
                    fontSize = (18 * screenConfig.textScale).sp
                )
            }
        }

        LogoHeader(
            logoUrl = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier = Modifier
                .padding(screenConfig.contentPadding)
                .wrapContentSize(Alignment.TopStart)
        )

        ClockDate(
            modifier = Modifier
                .padding(screenConfig.contentPadding)
                .wrapContentSize(Alignment.TopEnd)
        )
    }
}
