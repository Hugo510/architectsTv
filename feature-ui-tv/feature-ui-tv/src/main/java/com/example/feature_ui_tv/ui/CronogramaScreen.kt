// feature-ui-tv/src/main/java/com/example/feature_ui_tv/ui/CronogramaScreen.kt
package com.example.feature_ui_tv.ui

import android.graphics.Paint
import android.graphics.Paint.Align
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_ui_tv.ui.components.ClockDate
import com.example.feature_ui_tv.ui.components.LogoHeader
import com.example.feature_ui_tv.ui.components.PageIndicator
import com.example.feature_ui_tv.ui.components.rememberScreenConfig
import com.example.feature_ui_tv.ui.components.ScreenConfig


// Datos de ejemplo
data class Task(
    val name: String,
    val startDay: Int,
    val endDay: Int,
    val color: Color
)

private val sampleTasks = listOf(
    Task("Diseño",            0, 4,  Color(0xFF8888FF)),
    Task("Construcción",      0, 8,  Color(0xFF88FF88)),
    Task("Entrega parcial",   0, 2,  Color(0xFFBBBBBB)),
    Task("Revisión permisos", 3, 7,  Color(0xFFFFCCAA)),
    Task("Entrega final",     2, 5,  Color(0xFFCCCCCC)),
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
            // Encabezado responsivo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title, 
                    fontSize = (24 * screenConfig.textScale).sp, 
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Card(shape = RoundedCornerShape(4.dp)) {
                    Box(
                        modifier = Modifier
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

            Spacer(modifier = Modifier.height(screenConfig.cardSpacing))

            // Subtítulo responsivo
            if (screenConfig.isLargeScreen) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "\u23F0 Cronograma", 
                        fontSize = (28 * screenConfig.textScale).sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Realiza el seguimiento de proyectos y próximas etapas",
                        fontSize = (16 * screenConfig.textScale).sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            } else {
                Column {
                    Text(
                        "\u23F0 Cronograma", 
                        fontSize = (28 * screenConfig.textScale).sp
                    )
                    Text(
                        "Seguimiento de proyectos",
                        fontSize = (16 * screenConfig.textScale).sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(screenConfig.cardSpacing))

            // Tarjeta del gráfico
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenConfig.contentPadding)
                ) {
                    GanttChart(tasks = tasks, screenConfig = screenConfig)
                }
            }
        }

        // Botones responsivos
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
                Text("Planos", fontSize = (16 * screenConfig.textScale).sp) 
            }
        }
    }
}

@Composable
private fun GanttChart(
    tasks: List<Task>,
    screenConfig: ScreenConfig,
    days: Int = 11
) {
    val rowHeight = if (screenConfig.isSmallScreen) 28f else 32f
    val spacing = screenConfig.contentPadding.value
    val textSize = 20f * screenConfig.textScale

    Canvas(modifier = Modifier.fillMaxSize()) {
        val totalWidth = size.width - spacing * 2
        val dayWidth = totalWidth / days

        // Líneas y etiquetas de días con texto escalable
        for (i in 0..days) {
            val x = spacing + i * dayWidth
            drawLine(
                color = Color.LightGray, 
                start = Offset(x, 0f), 
                end = Offset(x, size.height), 
                strokeWidth = 1f
            )
            if (i < days) {
                drawContext.canvas.nativeCanvas.apply {
                    val dayLabel = when (i) {
                        0 -> if (screenConfig.isSmallScreen) "L6" else "Lun 6"
                        1 -> if (screenConfig.isSmallScreen) "M7" else "Mar 7"
                        2 -> if (screenConfig.isSmallScreen) "X8" else "Mié 8"
                        3 -> if (screenConfig.isSmallScreen) "J9" else "Jue 9"
                        4 -> if (screenConfig.isSmallScreen) "V10" else "Vie 10"
                        5 -> if (screenConfig.isSmallScreen) "S11" else "Sáb 11"
                        6 -> if (screenConfig.isSmallScreen) "D12" else "Dom 12"
                        7 -> if (screenConfig.isSmallScreen) "L13" else "Lun 13"
                        8 -> if (screenConfig.isSmallScreen) "M14" else "Mar 14"
                        9 -> if (screenConfig.isSmallScreen) "X15" else "Mié 15"
                        else -> if (screenConfig.isSmallScreen) "J16" else "Jue 16"
                    }
                    drawText(
                        dayLabel,
                        spacing + i * dayWidth + dayWidth / 2,
                        24f,
                        Paint().apply {
                            textAlign = Paint.Align.CENTER
                            this.textSize = textSize
                            color = android.graphics.Color.DKGRAY
                        }
                    )
                }
            }
        }

        // Barras de tareas con altura responsiva
        tasks.forEachIndexed { idx, task ->
            val top = 32f + idx * (rowHeight + 8f)
            val left = spacing + task.startDay * dayWidth
            val right = spacing + (task.endDay + 1) * dayWidth
            drawRoundRect(
                color = task.color, 
                topLeft = Offset(left, top), 
                size = Size(right - left, rowHeight), 
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}
