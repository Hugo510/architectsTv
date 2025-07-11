package com.example.feature_ui_tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

// Componentes reutilizables
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator
import com.example.feature_ui_tv.ui.components.rememberScreenConfig
import com.example.feature_ui_tv.ui.components.ScreenConfig

data class EvidenceItem(val title: String, val imageUrl: String)

private val sampleEvidence = listOf(
    EvidenceItem("Evidencia 1", "https://cmpcmaderas.com/assets/uploads/2024/05/minecraft-portada.jpg"),
    EvidenceItem("Evidencia 2", "https://minecraftfullhd.weebly.com/uploads/5/2/9/9/52994245/3180102_orig.jpg"),
    EvidenceItem("Evidencia 3", "https://static.planetminecraft.com/files/image/minecraft/project/2023/389/17216741_xl.webp")
)

@Composable
fun EvidenceScreen(
    projectName: String = "Proyecto 1",
    status: String = "En Proceso",
    evidences: List<EvidenceItem> = sampleEvidence,
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
            currentPage = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = screenConfig.contentPadding * 3.5f)
                .wrapContentSize(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 88.dp, 
                    start = screenConfig.contentPadding, 
                    end = screenConfig.contentPadding
                )
        ) {
            // Encabezado responsivo
            if (screenConfig.isLargeScreen) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\uD83D\uDDBC Evidencia",
                        fontSize = (32 * screenConfig.textScale).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Explora la evidencia del proyecto para darle seguimiento",
                    fontSize = (16 * screenConfig.textScale).sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            } else {
                Column {
                    Text(
                        text = "\uD83D\uDDBC Evidencia",
                        fontSize = (28 * screenConfig.textScale).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Evidencia del proyecto",
                        fontSize = (16 * screenConfig.textScale).sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(screenConfig.cardSpacing))

            // Card responsiva
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
                    // Título del proyecto responsivo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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

                    Spacer(Modifier.height(screenConfig.cardSpacing))

                    // LazyRow responsiva
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = screenConfig.contentPadding),
                        horizontalArrangement = Arrangement.spacedBy(screenConfig.cardSpacing * 1.5f)
                    ) {
                        items(evidences) { item ->
                            EvidenceCard(
                                item = item,
                                screenConfig = screenConfig
                            )
                        }
                    }
                }
            }
        }

        // Botón responsivo - ahora más grande y llamativo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .height(64.dp)
                    .padding(end = 16.dp),
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
            
            // Espacio vacío donde iría el botón "Siguiente" en otras pantallas
            Spacer(modifier = Modifier.width(120.dp))
        }
    }
}

@Composable
private fun EvidenceCard(
    item: EvidenceItem,
    screenConfig: ScreenConfig
) {
    val cardWidth = when {
        screenConfig.isSmallScreen -> 200.dp
        screenConfig.isMediumScreen -> 280.dp
        else -> 320.dp
    }
    val cardHeight = when {
        screenConfig.isSmallScreen -> 120.dp
        screenConfig.isMediumScreen -> 160.dp
        else -> 180.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(width = cardWidth, height = cardHeight)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            fontSize = (18 * screenConfig.textScale).sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

