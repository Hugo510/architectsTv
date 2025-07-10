package com.example.feature_ui_tv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * Un header con el logo y el nombre de la empresa,
 * posicionado en la esquina superior izquierda.
 *
 * @param logoUrl URL o recurso del logo (puede ser local o remoto)
 * @param companyName Nombre de la empresa a mostrar al lado del logo
 * @param modifier Modificador para personalizar layout
 */
@Composable
fun LogoHeader(
    logoUrl: String,
    companyName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo circular pequeño
        AsyncImage(
            model = logoUrl,
            contentDescription = "Logo de la empresa",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Nombre de la empresa
        Text(
            text = companyName,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
