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
// 1. Importa ClockDate y LogoHeader
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader

@Composable
fun CastPlaceholderScreen(onCastClick: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(120.dp))

            Text(
                text     = "Seleccione un proyecto desde su celular",
                fontSize = 24.sp,
                color    = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(48.dp))

            // 2. Icono grande para “Cast”
            Icon(
                imageVector        = Icons.Default.Cast,
                contentDescription = "Cast icon",
                modifier          = Modifier.size(96.dp),
                tint               = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            // 3. Botón “Proyectar”
            Button(onClick = onCastClick) {
                Text("Proyectar", fontSize = 18.sp)
            }
        }

        // 4. LogoHeader arriba a la izquierda
        LogoHeader(
            logoUrl     = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier    = Modifier
                .padding(16.dp)
                .wrapContentSize(Alignment.TopStart)
        )

        // 5. ClockDate arriba a la derecha
        ClockDate(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize(Alignment.TopEnd)
        )
    }
}
