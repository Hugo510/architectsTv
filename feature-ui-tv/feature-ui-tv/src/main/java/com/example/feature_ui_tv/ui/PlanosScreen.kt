package com.example.feature_ui_tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.example.feature_ui_tv.ui.components.PageIndicator

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
    scale: String = "1 : 100",
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Logo + nombre empresa
        LogoHeader(
            logoUrl     = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier    = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopStart)
        )

        // Reloj arriba a la derecha
        ClockDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopEnd)
        )

        // Indicador de página (tercer punto de 4)
        PageIndicator(
            totalPages  = 4,
            currentPage = 2,
            modifier    = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
                .wrapContentSize(Alignment.TopCenter)
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 88.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Planos Arquitectónicos",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Consulta las fechas clave y próximas entregas del proyecto",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(16.dp))

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
                    AsyncImage(
                        model = planUrl,
                        contentDescription = "Plano arquitectónico de $projectName",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = projectName,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.width(8.dp))
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
                        // Filas de info
                        InfoRow(label = "Última Revisión", value = lastRevision)
                        InfoRow(label = "Versión",           value = version)
                        InfoRow(label = "Tipo de Plano",     value = planType)
                        InfoRow(label = "Superficie Construida", value = builtArea)
                        InfoRow(label = "Superficie Terreno",    value = landArea)
                        InfoRow(label = "Escala",                value = scale)
                    }
                }
            }
        }

        // Botones de navegación abajo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Atrás")
            }
            Button(onClick = onNext) {
                Text("Siguiente")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
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
