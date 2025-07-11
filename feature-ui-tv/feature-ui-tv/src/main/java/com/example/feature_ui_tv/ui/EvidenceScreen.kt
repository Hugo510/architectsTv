package com.example.feature_ui_tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale

// Componentes reutilizables
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator
import com.example.feature_ui_tv.ui.components.rememberScreenConfig
import com.example.feature_ui_tv.ui.components.ScreenConfig

data class EvidenceItem(val title: String, val imageUrl: String)

private val sampleEvidence = listOf(
    EvidenceItem("Evidencia 1", "https://cmpcmaderas.com/assets/uploads/2024/05/minecraft-portada.jpg"),
    EvidenceItem("Evidencia 2", "https://minecraftfullhd.weebly.com/uploads/5/2/9/9/52994245/3180102_orig.jpg")
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
            // Título del proyecto
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

            // Título de evidencia con ícono
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🖼️ Evidencia",
                    fontSize = (32 * screenConfig.textScale).sp,
                    color = Color(0xFF2D3748),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Explora la evidencia del proyecto para darle seguimiento",
                fontSize = (16 * screenConfig.textScale).sp,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Grilla de evidencias
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                evidences.take(2).forEach { evidence ->
                    EvidenceCard(
                        item = evidence,
                        screenConfig = screenConfig,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Botón responsivo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(screenConfig.contentPadding),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(onClick = onBack) { 
                Text("Atrás", fontSize = (16 * screenConfig.textScale).sp) 
            }
        }
    }
}

@Composable
private fun EvidenceCard(
    item: EvidenceItem,
    screenConfig: ScreenConfig,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = item.title,
            fontSize = (18 * screenConfig.textScale).sp,
            color = Color(0xFF2D3748),
            fontWeight = FontWeight.Medium
        )
    }
}
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

