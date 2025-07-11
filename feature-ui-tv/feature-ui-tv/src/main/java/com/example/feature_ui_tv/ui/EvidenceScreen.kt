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

// Componentes reutilizables
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator


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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1) Logo + nombre empresa
        LogoHeader(
            logoUrl     = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier    = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopStart)
        )

        // 2) Reloj
        ClockDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopEnd)
        )

        // 3) Page indicator (4 de 4)
        PageIndicator(
            totalPages  = 4,
            currentPage = 3,
            modifier    = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
                .wrapContentSize(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 88.dp, start = 16.dp, end = 16.dp)
        ) {
            // 4) Encabezado con icono + título + subtítulo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text     = "\uD83D\uDDBC Evidencia",
                    fontSize = 32.sp,
                    color    = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Explora la evidencia del proyecto para darle seguimiento",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(16.dp))

            // 5) Card con lista de evidencias
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Título del proyecto + estado
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text     = projectName,
                            fontSize = 20.sp,
                            color    = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(8.dp))
                        Card(shape = RoundedCornerShape(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text     = status,
                                    fontSize = 14.sp,
                                    color    = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // **Fila horizontal de evidencias** usando LazyRow
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(evidences) { item ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model               = item.imageUrl,
                                    contentDescription  = item.title,
                                    modifier            = Modifier
                                        .size(width = 280.dp, height = 160.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text     = item.title,
                                    fontSize = 18.sp,
                                    color    = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = onBack) { Text("Atrás") }
    }
}

