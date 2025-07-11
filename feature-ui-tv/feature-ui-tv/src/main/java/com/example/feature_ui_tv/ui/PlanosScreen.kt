package com.example.feature_ui_tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    planUrl: String = "https://www.xtrafondos.com/wallpapers/construccion-en-minecraft-12384.jpg",
    lastRevision: String = "01/Junio/2025",
    version: String = "V.01",
    planType: String = "Planta Baja / Arquitectónica",
    builtArea: String = "150.00 m² (P.B. 80m² / P.A. 70m²)",
    landArea: String = "200.00 m²",
    scale: String = "1 : 100",
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val screenConfig = rememberScreenConfig()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = projectName,
                    fontSize = (24 * screenConfig.textScale).sp,
                    color = Color(0xFF2D3748),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                androidx.compose.material3.Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFE082)
                ) {
                    Text(
                        text = status,
                        fontSize = (14 * screenConfig.textScale).sp,
                        color = Color(0xFF744210),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🏗️ Planos Arquitectónicos",
                    fontSize = (32 * screenConfig.textScale).sp,
                    color = Color(0xFF2D3748),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Consulta las fechas clave y próximas entregas del proyecto",
                fontSize = (16 * screenConfig.textScale).sp,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Plano arquitectónico
                Card(
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    AsyncImage(
                        model = planUrl,
                        contentDescription = "Plano arquitectónico de $projectName",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                
                // Información General
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Información General",
                        fontSize = (20 * screenConfig.textScale).sp,
                        color = Color(0xFF2D3748),
                        fontWeight = FontWeight.Bold
                    )
                    
                    InfoSection(
                        title = "Última Revisión",
                        content = lastRevision,
                        screenConfig = screenConfig
                    )
                    
                    InfoSection(
                        title = "Vesión",
                        content = version,
                        screenConfig = screenConfig
                    )
                    
                    InfoSection(
                        title = "Tipo de Plano",
                        content = planType,
                        screenConfig = screenConfig
                    )
                    
                    InfoSection(
                        title = "Superficie Construida",
                        content = builtArea,
                        screenConfig = screenConfig
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoSection(
                            title = "Superficie Terreno",
                            content = landArea,
                            screenConfig = screenConfig,
                            modifier = Modifier.weight(1f)
                        )
                        InfoSection(
                            title = "Escala",
                            content = scale,
                            screenConfig = screenConfig,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Botones de navegación - ahora más grandes y llamativos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5) // Azul más llamativo
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Atrás", 
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = onNext,
                modifier = Modifier.height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047) // Verde más llamativo
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Text(
                    "Evidencia", 
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Siguiente",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: String,
    screenConfig: ScreenConfig,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = (14 * screenConfig.textScale).sp,
            color = Color(0xFF4A5568),
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = (16 * screenConfig.textScale).sp,
            color = Color(0xFF2D3748),
            fontWeight = FontWeight.Normal
        )
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
