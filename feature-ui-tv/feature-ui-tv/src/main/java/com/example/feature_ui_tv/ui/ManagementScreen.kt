package com.example.feature_ui_tv.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
            .background(Color(0xFFF5F5F5))
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

        // Añadir un botón grande y llamativo en la parte inferior derecha
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047) // Verde más llamativo
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Text(
                    "Cronograma",
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
                text = "👥 Gestión de Obras",
                fontSize = (32 * screenConfig.textScale).sp,
                color = Color(0xFF4A5568),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Supervisa el avance y la información clave de la obra",
            fontSize = (16 * screenConfig.textScale).sp,
            color = Color(0xFF718096)
        )

        Spacer(Modifier.height(screenConfig.cardSpacing))

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Columna izquierda - Proyecto e imagen
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = projectName,
                        fontSize = (24 * screenConfig.textScale).sp,
                        color = Color(0xFF2D3748),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(12.dp))
                    Surface(
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

                Spacer(Modifier.height(16.dp))

                AsyncImage(
                    model = "https://www.xtrafondos.com/wallpapers/construccion-en-minecraft-12384.jpg",
                    contentDescription = "Obra ejemplo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.3f)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // Columna derecha - Información general
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
                    title = "Ubicación",
                    content = "Calle [Nombre] # [Número]\nCol. [Colonia], Durango, Dgo.,\nMéxico",
                    screenConfig = screenConfig
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoSection(
                        title = "Presupuesto",
                        content = "$2,250,000 MXN",
                        screenConfig = screenConfig,
                        modifier = Modifier.weight(1f)
                    )
                    InfoSection(
                        title = "Cliente",
                        content = "John Doe",
                        screenConfig = screenConfig,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoSection(
                        title = "Director de Obra",
                        content = "Arq. Steve",
                        screenConfig = screenConfig,
                        modifier = Modifier.weight(1f)
                    )
                    InfoSection(
                        title = "Ayudantes",
                        content = "20 personas",
                        screenConfig = screenConfig,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Barra de progreso
                Column {
                    Text(
                        text = "Progreso - 50%",
                        fontSize = (16 * screenConfig.textScale).sp,
                        color = Color(0xFF2D3748),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight()
                                .background(Color(0xFF3182CE), RoundedCornerShape(4.dp))
                        )
                    }
                }
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
                fontSize = (32 * screenConfig.textScale).sp
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
