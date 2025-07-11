package com.example.feature_ui_tv.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage

import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator
import com.example.feature_ui_tv.ui.components.rememberScreenConfig
import com.example.feature_ui_tv.ui.components.ScreenConfig

@Composable
fun ManagementScreen(
    projectName: String = "Proyecto 2",
    status: String = "En Proceso",
    onNext: () -> Unit
) {
    val screenConfig = rememberScreenConfig()
    
    // Animación de entrada
    var contentVisible by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "contentAlpha"
    )
    
    LaunchedEffect(Unit) {
        contentVisible = true
    }
    
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .graphicsLayer { alpha = contentAlpha }
    ) {
        // 1) Logo + nombre empresa (esquina superior izquierda)
        LogoHeader(
            logoUrl     = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier    = Modifier
                .fillMaxWidth()
                .padding(screenConfig.contentPadding)
                .wrapContentSize(Alignment.TopStart)
        )

        // 2) Reloj (esquina superior derecha)
        ClockDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenConfig.contentPadding)
                .wrapContentSize(Alignment.TopEnd)
        )

        // 3) Indicador de páginas (4 total, estamos en la 0)
        PageIndicator(
            totalPages  = 4,
            currentPage = 0,
            modifier    = Modifier
                .fillMaxWidth()
                .padding(top = screenConfig.contentPadding * 3.5f)
                .wrapContentSize(Alignment.TopCenter)
        )

        // Contenido adaptativo
        if (screenConfig.isSmallScreen) {
            SmallScreenManagementContent(
                projectName = projectName,
                status = status,
                screenConfig = screenConfig,
                onNext = onNext
            )
        } else {
            LargeScreenManagementContent(
                projectName = projectName,
                status = status,
                screenConfig = screenConfig,
                onNext = onNext
            )
        }
    }
}

@Composable
private fun SmallScreenManagementContent(
    projectName: String,
    status: String,
    screenConfig: ScreenConfig,
    onNext: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(
                top = 88.dp, 
                start = screenConfig.contentPadding, 
                end = screenConfig.contentPadding
            )
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDEE0 Gestión de Obras",
                fontSize = (28 * screenConfig.textScale).sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Spacer(Modifier.height(screenConfig.cardSpacing))
        
        // Layout vertical para pantallas pequeñas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(screenConfig.contentPadding)
            ) {
                AsyncImage(
                    model = "https://www.xtrafondos.com/wallpapers/construccion-en-minecraft-12384.jpg",
                    contentDescription = "Obra ejemplo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(screenConfig.imageAspectRatio)
                        .clip(RoundedCornerShape(8.dp))
                )
                
                Spacer(Modifier.height(screenConfig.cardSpacing))
                
                ProjectInfoColumn(
                    projectName = projectName,
                    status = status,
                    screenConfig = screenConfig,
                    onNext = onNext
                )
            }
        }
    }
}

@Composable
private fun LargeScreenManagementContent(
    projectName: String,
    status: String,
    screenConfig: ScreenConfig,
    onNext: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(
                top = 88.dp,
                start = screenConfig.contentPadding,
                end = screenConfig.contentPadding
            )
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDEE0 Gestión de Obras",
                fontSize = (32 * screenConfig.textScale).sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = "Supervisa el avance y la información clave de la obra",
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
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(screenConfig.contentPadding)
            ) {
                AsyncImage(
                    model = "https://www.xtrafondos.com/wallpapers/construccion-en-minecraft-12384.jpg",
                    contentDescription = "Obra ejemplo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(screenConfig.imageAspectRatio)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.width(screenConfig.cardSpacing))

                ProjectInfoColumn(
                    modifier = Modifier.weight(1f),
                    projectName = projectName,
                    status = status,
                    screenConfig = screenConfig,
                    onNext = onNext
                )
            }
        }
    }
}

@Composable
private fun ProjectInfoColumn(
    modifier: Modifier = Modifier,
    projectName: String,
    status: String,
    screenConfig: ScreenConfig,
    onNext: () -> Unit
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
                    Modifier
                        .background(Color(0xFFFFE082))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = status,
                        fontSize = (14 * screenConfig.textScale).sp,
                        color = Color.Black
                    )
                }
            }
        }

        InfoRow(label = "Ubicación", value = "Calle X #123, Col Y, Ciudad", screenConfig = screenConfig)
        InfoRow(label = "Presupuesto", value = "$2,250,000 MXN", screenConfig = screenConfig)
        InfoRow(label = "Cliente", value = "John Doe", screenConfig = screenConfig)
        InfoRow(label = "Director", value = "Arq. Steve", screenConfig = screenConfig)
        InfoRow(label = "Ayudantes", value = "20 personas", screenConfig = screenConfig)

        Spacer(Modifier.height(8.dp))

        Button(onClick = onNext) {
            Text(
                "Ver cronograma",
                fontSize = (16 * screenConfig.textScale).sp
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, screenConfig: ScreenConfig) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
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
