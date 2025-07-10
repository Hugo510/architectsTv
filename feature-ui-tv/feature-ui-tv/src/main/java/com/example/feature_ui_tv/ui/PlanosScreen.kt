// feature-ui-tv/src/main/java/com/example/feature_ui_tv/ui/PlanosScreen.kt
package com.example.feature_ui_tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// 1) Importa los componentes reutilizables
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator  // ← NUEVO

@Composable
fun PlanosScreen(
    projectName: String = "Proyecto 1",
    status: String = "En Proceso",
    planUrl: String,
    lastRevision: String = "01/Junio/2025",
    version: String = "V.01",
    planType: String = "Planta Baja / Arquitectónica",
    builtArea: String = "150.00 m² (P.B. 80 m² / P.A. 70 m²)",
    landArea: String = "200.00 m²",
    scale: String = "1 : 100"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- LogoHeader arriba a la izq. ---
        LogoHeader(
            logoUrl     = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier    = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopStart)
        )

        // --- ClockDate arriba a la derecha ---
        ClockDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopEnd)
        )

        // --- PageIndicator centrado (tercer punto) ---
        PageIndicator(
            totalPages  = 4,
            currentPage = 2,
            modifier    = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
                .wrapContentSize(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                // Empuja el contenido principal hacia abajo para no solaparse
                .fillMaxSize()
                .padding(top = 88.dp, start = 16.dp, end = 16.dp)
        ) {
            // --- Encabezado: título y subtítulo ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Planos Arquitectónicos",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Consulta las fechas clave y próximas entregas del proyecto",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Card con contenido principal ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Izquierda: imagen del plano
                    AsyncImage(
                        model = planUrl,
                        contentDescription = "Plano arquitectónico de $projectName",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Derecha: info general
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Título del proyecto + estado
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = projectName,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(shape = RoundedCornerShape(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = status,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }

                        // Filas de información
                        InfoRow(label = "Última Revisión",      value = lastRevision)
                        InfoRow(label = "Versión",               value = version)
                        InfoRow(label = "Tipo de Plano",         value = planType)
                        InfoRow(label = "Superficie Construida", value = builtArea)
                        InfoRow(label = "Superficie Terreno",    value = landArea)
                        InfoRow(label = "Escala",                value = scale)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
