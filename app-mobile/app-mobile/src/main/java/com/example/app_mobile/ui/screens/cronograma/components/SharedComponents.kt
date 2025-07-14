package com.example.app_mobile.ui.screens.cronograma.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Logo y nombre empresa con mejor diseño
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Card(
                    modifier = Modifier.size(48.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "L",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "LOGO EMPRESA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Gestión de Proyectos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Título con gradiente visual
            Text(
                text = "Cronograma de Proyecto",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                lineHeight = 36.sp
            )
            
            // Subtítulo mejorado
            Text(
                text = "Planifica y da seguimiento detallado a las etapas del proyecto",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ScheduleInfoCard(
    schedule: ProjectSchedule,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = schedule.totalProgress.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header del card con icono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = schedule.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            if (schedule.description != null) {
                Text(
                    text = schedule.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 20.dp),
                    lineHeight = 18.sp
                )
            }
            
            // Estadísticas mejoradas con cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ScheduleStatCard(
                    label = "Total Tareas",
                    value = schedule.totalTasks.toString(),
                    icon = "📋",
                    modifier = Modifier.weight(1f)
                )
                ScheduleStatCard(
                    label = "Completadas",
                    value = schedule.completedTasks.toString(),
                    icon = "✅",
                    modifier = Modifier.weight(1f)
                )
                ScheduleStatCard(
                    label = "Progreso",
                    value = "${(schedule.totalProgress * 100).toInt()}%",
                    icon = "📊",
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Barra de progreso mejorada
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Progreso General",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
                Text(
                    text = "100%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ScheduleStatCard(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = icon,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Botón Cronograma
            ViewSelectorButton(
                selected = selectedView == ViewType.CRONOGRAMA,
                onClick = { onViewChange(ViewType.CRONOGRAMA) },
                label = "Cronograma",
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f)
            )
            
            // Botón Kanban
            ViewSelectorButton(
                selected = selectedView == ViewType.KANBAN,
                onClick = { onViewChange(ViewType.KANBAN) },
                label = "Kanban",
                icon = Icons.Default.Dashboard,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ViewSelectorButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale_animation"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier.scale(animatedScale),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surface,
            contentColor = if (selected) 
                MaterialTheme.colorScheme.onPrimary 
            else 
                MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "🏗️",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Fases del Proyecto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                item {
                    ProjectStatusLegendCard(
                        status = ProjectStatus.DESIGN,
                        label = "Diseño",
                        color = Color(0xFF9CA3FF),
                        icon = "📐"
                    )
                }
                item {
                    ProjectStatusLegendCard(
                        status = ProjectStatus.PERMITS_REVIEW,
                        label = "Permisos",
                        color = Color(0xFFFBB6CE),
                        icon = "📋"
                    )
                }
                item {
                    ProjectStatusLegendCard(
                        status = ProjectStatus.CONSTRUCTION,
                        label = "Construcción",
                        color = Color(0xFF68D391),
                        icon = "🏗️"
                    )
                }
                item {
                    ProjectStatusLegendCard(
                        status = ProjectStatus.DELIVERY,
                        label = "Entrega",
                        color = Color(0xFFA0AEC0),
                        icon = "🎉"
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectStatusLegendCard(
    status: ProjectStatus,
    label: String,
    color: Color,
    icon: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun MilestonesSection(
    milestones: List<Milestone>,
    onMilestoneClick: (Milestone) -> Unit = {},
    onMilestoneToggle: (String) -> Unit = {}, // Nuevo parámetro
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "🎯",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Hitos del Proyecto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (milestones.isEmpty()) {
                EmptyMilestonesState()
            } else {
                milestones.forEach { milestone ->
                    MilestoneCard(
                        milestone = milestone,
                        onClick = { onMilestoneClick(milestone) },
                        onToggle = { onMilestoneToggle(milestone.id) } // Nueva funcionalidad
                    )
                    if (milestone != milestones.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMilestonesState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "No hay hitos definidos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Los hitos aparecerán aquí cuando se agreguen",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MilestoneCard(
    milestone: Milestone,
    onClick: () -> Unit,
    onToggle: () -> Unit = {} // Nuevo parámetro
) {
    val cardColor by animateColorAsState(
        targetValue = if (milestone.isCompleted) 
            MaterialTheme.colorScheme.primaryContainer
        else 
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300),
        label = "card_color_animation"
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (milestone.isCompleted) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono animado clickeable para toggle
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (milestone.isCompleted) 
                        Icons.Default.CheckCircle 
                    else 
                        Icons.Default.Schedule,
                    contentDescription = if (milestone.isCompleted) "Marcar como pendiente" else "Marcar como completado",
                    tint = if (milestone.isCompleted) 
                        Color(0xFF4CAF50) 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(28.dp)
                        .scale(iconScale)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = milestone.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                milestone.description?.let { description ->
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp),
                        lineHeight = 16.sp
                    )
                }
                
                Text(
                    text = if (milestone.isCompleted) 
                        "✅ Completado: ${milestone.completedDate ?: "Sin fecha"}"
                    else 
                        "📅 Fecha objetivo: ${milestone.targetDate}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Badge de importancia
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = getMilestoneImportanceColor(milestone.importance)
            ) {
                Text(
                    text = getDisplayImportance(milestone.importance),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
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
