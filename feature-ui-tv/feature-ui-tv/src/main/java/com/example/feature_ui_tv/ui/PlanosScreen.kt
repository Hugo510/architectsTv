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
import com.example.feature_ui_tv.ui.components.rememberScreenConfig
import com.example.feature_ui_tv.ui.components.ScreenConfig

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
    val screenConfig = rememberScreenConfig()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LogoHeader(
            logoUrl = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenConfig.contentPadding)
                .wrapContentSize(Alignment.TopStart)
        )

        ClockDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenConfig.contentPadding)
                .wrapContentSize(Alignment.TopEnd)
        )

        PageIndicator(
            totalPages = 4,
            currentPage = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = screenConfig.contentPadding * 3.5f)
                .wrapContentSize(Alignment.TopCenter)
        )

        // Contenido principal adaptativo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 88.dp, 
                    start = screenConfig.contentPadding, 
                    end = screenConfig.contentPadding
                )
        ) {
            Text(
                text = "Planos Arquitectónicos",
                fontSize = (28 * screenConfig.textScale).sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Consulta las fechas clave y próximas entregas del proyecto",
                fontSize = (16 * screenConfig.textScale).sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(screenConfig.cardSpacing))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (screenConfig.isSmallScreen) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(screenConfig.contentPadding)
                    ) {
                        AsyncImage(
                            model = planUrl,
                            contentDescription = "Plano arquitectónico de $projectName",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(screenConfig.imageAspectRatio)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.height(screenConfig.cardSpacing))
                        PlanInfoColumn(
                            projectName = projectName,
                            status = status,
                            lastRevision = lastRevision,
                            version = version,
                            planType = planType,
                            builtArea = builtArea,
                            landArea = landArea,
                            scale = scale,
                            screenConfig = screenConfig
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(screenConfig.contentPadding)
                    ) {
                        AsyncImage(
                            model = planUrl,
                            contentDescription = "Plano arquitectónico de $projectName",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(screenConfig.imageAspectRatio)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.width(screenConfig.cardSpacing))
                        PlanInfoColumn(
                            modifier = Modifier.weight(1f),
                            projectName = projectName,
                            status = status,
                            lastRevision = lastRevision,
                            version = version,
                            planType = planType,
                            builtArea = builtArea,
                            landArea = landArea,
                            scale = scale,
                            screenConfig = screenConfig
                        )
                    }
                }
            }
        }

        // Botones de navegación
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(screenConfig.contentPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Atrás", fontSize = (16 * screenConfig.textScale).sp)
            }
            Button(onClick = onNext) {
                Text("Siguiente", fontSize = (16 * screenConfig.textScale).sp)
            }
        }
    }
}

@Composable
private fun PlanInfoColumn(
    modifier: Modifier = Modifier,
    projectName: String,
    status: String,
    lastRevision: String,
    version: String,
    planType: String,
    builtArea: String,
    landArea: String,
    scale: String,
    screenConfig: ScreenConfig
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(screenConfig.cardSpacing),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = projectName,
                fontSize = (20 * screenConfig.textScale).sp,
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
                        fontSize = (14 * screenConfig.textScale).sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        InfoRow(label = "Última Revisión", value = lastRevision, screenConfig = screenConfig)
        InfoRow(label = "Versión", value = version, screenConfig = screenConfig)
        InfoRow(label = "Tipo de Plano", value = planType, screenConfig = screenConfig)
        InfoRow(label = "Superficie Construida", value = builtArea, screenConfig = screenConfig)
        InfoRow(label = "Superficie Terreno", value = landArea, screenConfig = screenConfig)
        InfoRow(label = "Escala", value = scale, screenConfig = screenConfig)
    }
}

@Composable
private fun InfoRow(label: String, value: String, screenConfig: ScreenConfig) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = (16 * screenConfig.textScale).sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = (16 * screenConfig.textScale).sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
