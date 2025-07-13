package com.example.app_mobile.ui.screens.cronograma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared_domain.model.*

enum class ViewType {
    CRONOGRAMA, KANBAN
}

@Composable
fun CronogramaHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Logo y nombre empresa
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "L",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "LOGO EMPRESA",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        // Título
        Text(
            text = "Cronograma",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Subtítulo
        Text(
            text = "Planifica y da seguimiento a las etapas del proyecto",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ScheduleInfoCard(
    schedule: ProjectSchedule,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = schedule.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            if (schedule.description != null) {
                Text(
                    text = schedule.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Estadísticas del cronograma
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScheduleStatItem(
                    label = "Total Tareas",
                    value = schedule.totalTasks.toString()
                )
                ScheduleStatItem(
                    label = "Completadas",
                    value = schedule.completedTasks.toString()
                )
                ScheduleStatItem(
                    label = "Progreso",
                    value = "${(schedule.totalProgress * 100).toInt()}%"
                )
            }
            
            // Barra de progreso general
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { schedule.totalProgress.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ScheduleStatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ViewSelector(
    selectedView: ViewType,
    onViewChange: (ViewType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón Cronograma
            FilterChip(
                selected = selectedView == ViewType.CRONOGRAMA,
                onClick = { onViewChange(ViewType.CRONOGRAMA) },
                label = { Text("Cronograma") },
                leadingIcon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
            
            // Botón Kanban
            FilterChip(
                selected = selectedView == ViewType.KANBAN,
                onClick = { onViewChange(ViewType.KANBAN) },
                label = { Text("Kanban") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Dashboard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatusLegend(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Fases del Proyecto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProjectStatusLegendItem(
                    status = ProjectStatus.DESIGN,
                    label = "Diseño",
                    color = Color(0xFF9CA3FF)
                )
                ProjectStatusLegendItem(
                    status = ProjectStatus.PERMITS_REVIEW,
                    label = "Revisión de Permisos",
                    color = Color(0xFFFBB6CE)
                )
                ProjectStatusLegendItem(
                    status = ProjectStatus.CONSTRUCTION,
                    label = "Construcción",
                    color = Color(0xFF68D391)
                )
                ProjectStatusLegendItem(
                    status = ProjectStatus.DELIVERY,
                    label = "Entrega",
                    color = Color(0xFFA0AEC0)
                )
            }
        }
    }
}

@Composable
private fun ProjectStatusLegendItem(
    status: ProjectStatus,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MilestonesSection(
    milestones: List<Milestone>,
    onMilestoneClick: (Milestone) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🎯 Hitos del Proyecto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            milestones.forEach { milestone ->
                MilestoneCard(
                    milestone = milestone,
                    onClick = { onMilestoneClick(milestone) }
                )
                if (milestone != milestones.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MilestoneCard(
    milestone: Milestone,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isCompleted) 
                MaterialTheme.colorScheme.primaryContainer
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de estado
            Icon(
                imageVector = if (milestone.isCompleted) 
                    Icons.Default.CheckCircle 
                else 
                    Icons.Default.Schedule,
                contentDescription = null,
                tint = if (milestone.isCompleted) 
                    Color(0xFF4CAF50) 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = milestone.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (milestone.description != null) {
                    Text(
                        text = milestone.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = if (milestone.isCompleted) 
                        "Completado: ${milestone.completedDate}"
                    else 
                        "Fecha objetivo: ${milestone.targetDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Importancia
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getMilestoneImportanceColor(milestone.importance)
            ) {
                Text(
                    text = getDisplayImportance(milestone.importance),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getMilestoneImportanceColor(importance: MilestoneImportance): Color {
    return when (importance) {
        MilestoneImportance.LOW -> Color(0xFF4CAF50)
        MilestoneImportance.MEDIUM -> Color(0xFFFF9800)
        MilestoneImportance.HIGH -> Color(0xFFFF5722)
        MilestoneImportance.CRITICAL -> Color(0xFFF44336)
    }
}

private fun getDisplayImportance(importance: MilestoneImportance): String {
    return when (importance) {
        MilestoneImportance.LOW -> "Baja"
        MilestoneImportance.MEDIUM -> "Media"
        MilestoneImportance.HIGH -> "Alta"
        MilestoneImportance.CRITICAL -> "Crítica"
    }
}
