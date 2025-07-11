package com.example.feature_ui_tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator

/**
 * Pantalla de gestión de obras (primer paso del flujo de TV).
 */
@Composable
fun ManagementScreen(
    projectName: String = "Proyecto 2",
    status: String = "En Proceso",
    onNext: () -> Unit  // para navegar a la siguiente pantalla (Cronograma)
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1) Logo + nombre empresa (esquina superior izquierda)
        LogoHeader(
            logoUrl     = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier    = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopStart)
        )

        // 2) Reloj (esquina superior derecha)
        ClockDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopEnd)
        )

        // 3) Indicador de páginas (4 total, estamos en la 0)
        PageIndicator(
            totalPages  = 4,
            currentPage = 0,
            modifier    = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
                .wrapContentSize(Alignment.TopCenter)
        )

        // 4) Contenido principal
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 88.dp, start = 16.dp, end = 16.dp)
        ) {
            // Título + subtítulo
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\uD83D\uDEE0 Gestión de Obras",
                    fontSize = 32.sp,
                    color    = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Supervisa el avance y la información clave de la obra",
                fontSize = 16.sp,
                color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(16.dp))

            // Card con imagen y datos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Izquierda: foto de la obra
                    AsyncImage(
                        model               = "https://www.xtrafondos.com/wallpapers/construccion-en-minecraft-12384.jpg",
                        contentDescription  = "Obra ejemplo",
                        contentScale        = ContentScale.Crop,
                        modifier            = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(Modifier.width(16.dp))

                    // Derecha: info general
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier            = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text     = projectName,
                                fontSize = 20.sp,
                                color    = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.width(8.dp))
                            Card(shape = RoundedCornerShape(4.dp)) {
                                Box(
                                    Modifier
                                        .background(Color(0xFFFFE082))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text     = status,
                                        fontSize = 14.sp,
                                        color    = Color.Black
                                    )
                                }
                            }
                        }

                        InfoRow(label = "Ubicación",   value = "Calle X #123, Col Y, Ciudad")
                        InfoRow(label = "Presupuesto", value = "$2,250,000 MXN")
                        InfoRow(label = "Cliente",     value = "John Doe")
                        InfoRow(label = "Director",    value = "Arq. Steve")
                        InfoRow(label = "Ayudantes",   value = "20 personas")

                        Spacer(Modifier.height(8.dp))

                        // Botón “Siguiente”
                        Button(onClick = onNext) {
                            Text("Ver cronograma")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fila reutilizable de label / value.
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier              = Modifier.fillMaxWidth()
    ) {
        Text(
            text     = label,
            fontSize = 16.sp,
            color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Text(
            text     = value,
            fontSize = 16.sp,
            color    = MaterialTheme.colorScheme.onBackground
        )
    }
}
