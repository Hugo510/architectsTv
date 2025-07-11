// feature-ui-tv/src/main/java/com/example/feature_ui_tv/ui/CronogramaScreen.kt
package com.example.feature_ui_tv.ui

import android.graphics.Paint
import android.graphics.Paint.Align
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator
import com.example.feature_ui_tv.ui.components.rememberScreenConfig
import com.example.feature_ui_tv.ui.components.ScreenConfig


// Datos de ejemplo actualizados para coincidir con la imagen
data class Task(
    val name: String,
    val startDay: Int,
    val endDay: Int,
    val color: Color
)

private val sampleTasks = listOf(
    Task("Diseño",            0, 4,  Color(0xFF9CA3FF)),
    Task("Construcción",      0, 8,  Color(0xFF68D391)),
    Task("Entrega",           0, 2,  Color(0xFFA0AEC0)),
    Task("Revisión de Permisos", 3, 7,  Color(0xFFFBB6CE)),
    Task("Entrega",           2, 5,  Color(0xFFA0AEC0)),
    Task("Diseño",            8, 10, Color(0xFF9CA3FF)),
    Task("Construcción",      5, 10, Color(0xFF68D391))
)

@Composable
fun CronogramaScreen(
    title: String = "Proyecto 1",
    status: String = "En Proceso",
    tasks: List<Task> = sampleTasks,
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
            currentPage = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = screenConfig.contentPadding * 3.5f)
                .wrapContentSize(Alignment.TopCenter)
        )

        // Contenido adaptativo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 88.dp, 
                    start = screenConfig.contentPadding, 
                    end = screenConfig.contentPadding
                )
        ) {
            // Encabezado con título del proyecto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title, 
                    fontSize = (24 * screenConfig.textScale).sp, 
                    color = Color(0xFF2D3748),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título del cronograma con ícono
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "🕐 Cronograma", 
                    fontSize = (32 * screenConfig.textScale).sp,
                    color = Color(0xFF2D3748),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            Text(
                "Realiza el seguimiento de proyectos y próximas etapas",
                fontSize = (16 * screenConfig.textScale).sp,
                color = Color(0xFF718096),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Leyenda de colores horizontal
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LegendItem("Diseño", Color(0xFF9CA3FF), screenConfig)
                LegendItem("Revisión de Permisos", Color(0xFFFBB6CE), screenConfig)
                LegendItem("Construcción", Color(0xFF68D391), screenConfig)
                LegendItem("Entrega", Color(0xFFA0AEC0), screenConfig)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta del gráfico
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    GanttChart(tasks = tasks, screenConfig = screenConfig)
                }
            }
        }

        // Botones responsivos - ahora más grandes y llamativos
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
                    "Planos", 
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
private fun LegendItem(
    label: String,
    color: Color,
    screenConfig: ScreenConfig
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            fontSize = (14 * screenConfig.textScale).sp,
            color = Color(0xFF4A5568)
        )
    }
}

@Composable
private fun GanttChart(
    tasks: List<Task>,
    screenConfig: ScreenConfig,
    days: Int = 11
) {
    val rowHeight = 40f
    val headerHeight = 60f
    val spacing = 16f
    val textSize = 12f * screenConfig.textScale
    val dayLabels = listOf("Lun 6", "Mar 7", "Mier 9", "Juev 10", "Vier 11", "Sab 12", "Dom 13", "Lun 13", "Mar 14", "Mier 15", "Juev 16")

    Canvas(modifier = Modifier.fillMaxSize()) {
        val totalWidth = size.width - spacing * 2
        val dayWidth = totalWidth / days
        val currentDay = 2 // Línea vertical del día actual

        // Dibujar encabezado con días
        dayLabels.forEachIndexed { i, dayLabel ->
            if (i < days) {
                drawContext.canvas.nativeCanvas.apply {
                    val shortLabel = when {
                        screenConfig.isSmallScreen -> dayLabel.substring(0, 3)
                        else -> dayLabel
                    }
                    drawText(
                        shortLabel,
                        spacing + i * dayWidth + dayWidth / 2,
                        30f,
                        Paint().apply {
                            textAlign = Paint.Align.CENTER
                            this.textSize = textSize
                            color = android.graphics.Color.parseColor("#4A5568")
                            isFakeBoldText = false
                        }
                    )
                }
            }
        }

        // Líneas verticales de la grilla
        for (i in 0..days) {
            val x = spacing + i * dayWidth
            drawLine(
                color = Color(0xFFE2E8F0), 
                start = Offset(x, headerHeight), 
                end = Offset(x, size.height), 
                strokeWidth = 1f
            )
        }

        // Línea vertical del día actual (más gruesa y de color distintivo)
        val currentDayX = spacing + currentDay * dayWidth
        drawLine(
            color = Color(0xFF3182CE),
            start = Offset(currentDayX, 0f),
            end = Offset(currentDayX, size.height),
            strokeWidth = 2f,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
        )

        // Barras de tareas
        tasks.forEachIndexed { idx, task ->
            val top = headerHeight + 20f + idx * (rowHeight + 8f)
            val left = spacing + task.startDay * dayWidth + 4f
            val right = spacing + (task.endDay + 1) * dayWidth - 4f
            
            drawRoundRect(
                color = task.color, 
                topLeft = Offset(left, top), 
                size = Size(right - left, rowHeight), 
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}
