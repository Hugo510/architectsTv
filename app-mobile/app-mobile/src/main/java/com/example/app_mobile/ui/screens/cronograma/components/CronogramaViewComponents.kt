package com.example.app_mobile.ui.screens.cronograma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared_domain.model.*

@Composable
fun CronogramaTimelineView(
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Agrupar tareas por fase de proyecto
    val tasksByPhase = remember(tasks) {
        tasks.groupBy { mapTaskCategoryToProjectStatus(it.category) }
            .toSortedMap(compareBy { it.ordinal })
    }
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            CronogramaViewHeader()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tasksByPhase.forEach { (phase, phaseTasks) =>
                    item(key = phase.name) {
                        ProjectPhaseSection(
                            phase = phase,
                            tasks = phaseTasks,
                            onTaskClick = onTaskClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CronogramaViewHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "📅 Vista de Cronograma",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { /* TODO: Implementar ordenamiento */ }) {
                Text("Ordenar")
            }
            TextButton(onClick = { /* TODO: Implementar filtros */ }) {
                Text("Filtros")
            }
        }
    }
}

@Composable
private fun ProjectPhaseSection(
    phase: ProjectStatus,
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getProjectStatusColor(phase).copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, getProjectStatusColor(phase).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header de la fase
            PhaseHeader(
                phase = phase,
                taskCount = tasks.size,
                completedTasks = tasks.count { it.status == TaskStatus.COMPLETED },
                isExpanded = isExpanded,
                onToggleExpanded = { isExpanded = !isExpanded }
            )
            
            // Lista de tareas (expandible)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                tasks.forEach { task ->
                    TaskTimelineCard(
                        task = task,
                        phase = phase,
                        onClick = { onTaskClick(task) }
                    )
                    
                    if (task != tasks.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                if (tasks.isEmpty()) {
                    PhaseEmptyState(phase = phase)
                }
            }
        }
    }
}

@Composable
private fun PhaseHeader(
    phase: ProjectStatus,
    taskCount: Int,
    completedTasks: Int,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador de fase
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(getProjectStatusColor(phase))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Información de la fase
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getProjectStatusDisplayName(phase),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "$completedTasks de $taskCount tareas completadas",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Progreso de la fase
        if (taskCount > 0) {
            val phaseProgress = completedTasks.toFloat() / taskCount.toFloat()
            
            CircularProgressIndicator(
                progress = { phaseProgress },
                modifier = Modifier.size(40.dp),
                color = getProjectStatusColor(phase),
                strokeWidth = 3.dp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        // Botón de expandir/contraer
        IconButton(onClick = onToggleExpanded) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Contraer" else "Expandir"
            )
        }
    }
}

@Composable
fun TaskTimelineCard(
    task: ScheduleTask,
    phase: ProjectStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timeline indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (task.status) {
                                TaskStatus.COMPLETED -> Color(0xFF4CAF50)
                                TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
                                TaskStatus.ON_HOLD -> Color(0xFFF44336)
                                else -> Color(0xFF9E9E9E)
                            }
                        )
                )
                
                if (task != tasks.last()) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(40.dp)
                            .background(getProjectStatusColor(phase).copy(alpha = 0.3f))
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenido de la tarea
            Column(modifier = Modifier.weight(1f)) {
                TaskHeader(task = task)
                
                if (task.description != null) {
                    TaskDescription(description = task.description)
                }
                
                TaskMetadata(task = task)
                
                if (task.progress > 0) {
                    TaskProgress(
                        progress = task.progress,
                        phase = phase
                    )
                }
            }
        }
    }
}

@Composable
private fun PhaseEmptyState(phase: ProjectStatus) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📋",
                fontSize = 20.sp
            )
            Text(
                text = "No hay tareas en ${getProjectStatusDisplayName(phase)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TaskHeader(task: ScheduleTask) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = task.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (task.priority == TaskPriority.CRITICAL || task.priority == TaskPriority.HIGH) {
                PriorityChip(priority = task.priority)
            }
            
            TaskStatusChip(status = task.status)
        }
    }
}

@Composable
private fun TaskDescription(description: String) {
    Text(
        text = description,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
        maxLines = 2
    )
}

@Composable
private fun TaskMetadata(task: ScheduleTask) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "📅 ${task.startDate} - ${task.endDate}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (task.assignedTo.isNotEmpty()) {
            Text(
                text = "👤 ${task.assignedTo.first()}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TaskProgress(
    progress: Double,
    phase: ProjectStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = { progress.toFloat() },
            modifier = Modifier.weight(1f),
            color = getProjectStatusColor(phase)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PriorityChip(priority: TaskPriority) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = getPriorityColor(priority)
    ) {
        Text(
            text = getDisplayPriority(priority),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            fontSize = 9.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TaskStatusChip(status: TaskStatus) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = getTaskStatusColor(status)
    ) {
        Text(
            text = getDisplayStatus(status),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            fontSize = 9.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper functions
private fun getProjectStatusColor(status: ProjectStatus): Color {
    return when (status) {
        ProjectStatus.DESIGN -> Color(0xFF9CA3FF)
        ProjectStatus.PERMITS_REVIEW -> Color(0xFFFBB6CE)
        ProjectStatus.CONSTRUCTION -> Color(0xFF68D391)
        ProjectStatus.DELIVERY -> Color(0xFFA0AEC0)
    }
}

private fun getProjectStatusDisplayName(status: ProjectStatus): String {
    return when (status) {
        ProjectStatus.DESIGN -> "Diseño"
        ProjectStatus.PERMITS_REVIEW -> "Revisión de Permisos"
        ProjectStatus.CONSTRUCTION -> "Construcción"
        ProjectStatus.DELIVERY -> "Entrega"
    }
}

private fun getTaskStatusColor(status: TaskStatus): Color {
    return when (status) {
        TaskStatus.NOT_STARTED -> Color(0xFF2196F3)
        TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
        TaskStatus.COMPLETED -> Color(0xFF4CAF50)
        TaskStatus.ON_HOLD -> Color(0xFFF44336)
        TaskStatus.CANCELLED -> Color(0xFF9E9E9E)
    }
}

private fun getPriorityColor(priority: TaskPriority): Color {
    return when (priority) {
        TaskPriority.LOW -> Color(0xFF4CAF50)
        TaskPriority.MEDIUM -> Color(0xFFFF9800)
        TaskPriority.HIGH -> Color(0xFFFF5722)
        TaskPriority.CRITICAL -> Color(0xFFF44336)
    }
}

private fun getDisplayStatus(status: TaskStatus): String {
    return when (status) {
        TaskStatus.NOT_STARTED -> "Por Iniciar"
        TaskStatus.IN_PROGRESS -> "En Proceso"
        TaskStatus.COMPLETED -> "Completado"
        TaskStatus.ON_HOLD -> "En Pausa"
        TaskStatus.CANCELLED -> "Cancelado"
    }
}

private fun getDisplayPriority(priority: TaskPriority): String {
    return when (priority) {
        TaskPriority.LOW -> "Baja"
        TaskPriority.MEDIUM -> "Media"
        TaskPriority.HIGH -> "Alta"
        TaskPriority.CRITICAL -> "Crítica"
    }
}

private fun mapTaskCategoryToProjectStatus(category: TaskCategory): ProjectStatus {
    return when (category) {
        TaskCategory.DESIGN -> ProjectStatus.DESIGN
        TaskCategory.PERMITS -> ProjectStatus.PERMITS_REVIEW
        TaskCategory.CONSTRUCTION, TaskCategory.INSPECTION -> ProjectStatus.CONSTRUCTION
        TaskCategory.DELIVERY -> ProjectStatus.DELIVERY
        else -> ProjectStatus.DESIGN
    }
}
