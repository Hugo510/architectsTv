// feature-ui-tv/src/main/java/com/example/feature_ui_tv/ui/CronogramaScreen.kt
package com.example.feature_ui_tv.ui

import android.graphics.Paint
import android.graphics.Paint.Align
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
    tasks: List<Task> = sampleTasks
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // LogoHeader en la esquina superior izquierda
        LogoHeader(
            logoUrl     = "https://tu.cdn.com/logo_pequeño.png",
            companyName = "Nombre Empresa",
            modifier    = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopStart)
        )

        // ClockDate en la esquina superior derecha
        ClockDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentSize(Alignment.TopEnd)
        )

        PageIndicator(
            totalPages   = 4,
            currentPage  = 1,
            modifier     = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
                .wrapContentSize(Alignment.TopCenter)
        )

        // Contenido principal desplazado hacia abajo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp, start = 16.dp, end = 16.dp)
        ) {
            // Encabezado con título y estado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Card(shape = RoundedCornerShape(4.dp)) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFE082))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = status, fontSize = 14.sp, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u23F0 Cronograma", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Realiza el seguimiento de proyectos y próximas etapas",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        .padding(16.dp)
                ) {
                    GanttChart(tasks = tasks)
                }
            }
        }
    }
}

@Composable
private fun GanttChart(
    tasks: List<Task>,
    days: Int = 11,
    rowHeight: Float = 32f,
    spacing: Float = 16f
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val totalWidth = size.width - spacing * 2
        val dayWidth = totalWidth / days

        // Líneas y etiquetas de días
        for (i in 0..days) {
            val x = spacing + i * dayWidth
            drawLine(color = Color.LightGray, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
            if (i < days) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        when (i) {
                            0 -> "Lun 6"; 1 -> "Mar 7"; 2 -> "Mié 8"
                            3 -> "Jue 9"; 4 -> "Vie 10";5 -> "Sáb 11"
                            6 -> "Dom 12";7 -> "Lun 13";8 -> "Mar 14"
                            9 -> "Mié 15";else -> "Jue 16"
                        },
                        spacing + i * dayWidth + dayWidth / 2,
                        24f,
                        Paint().apply {
                            textAlign = Align.CENTER
                            textSize = 24f
                            color = android.graphics.Color.DKGRAY
                        }
                    )
                }
            }
        }

        // Barras de tareas
        tasks.forEachIndexed { idx, task ->
            val top = 32f + idx * (rowHeight + 8f)
            val left = spacing + task.startDay * dayWidth
            val right = spacing + (task.endDay + 1) * dayWidth
            drawRoundRect(color = task.color, topLeft = Offset(left, top), size = Size(right - left, rowHeight), cornerRadius = CornerRadius(8f, 8f))
        }
    }
}
