package com.example.app_mobile.ui.screens.cronograma.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared_domain.model.*

@Composable
fun CronogramaTimelineView(
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit = {},
    onTaskStatusUpdate: (String, TaskStatus) -> Unit = { _, _ -> }, // Nuevo parámetro
    onTaskProgressUpdate: (String, Double) -> Unit = { _, _ -> }, // Nuevo parámetro
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
                tasksByPhase.forEach { (phase, phaseTasks) ->
                    item(key = phase.name) {
                        ProjectPhaseSection(
                            phase = phase,
                            tasks = phaseTasks,
                            onTaskClick = onTaskClick,
                            onTaskStatusUpdate = onTaskStatusUpdate, // Pasar función
                            onTaskProgressUpdate = onTaskProgressUpdate // Pasar función
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CronogramaViewHeader() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "📅",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Vista de Cronograma",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { /* TODO: Implementar ordenamiento */ },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Ordenar",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = { /* TODO: Implementar filtros */ },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Filtros",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectPhaseSection(
    phase: ProjectStatus,
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit,
    onTaskStatusUpdate: (String, TaskStatus) -> Unit = { _, _ -> }, // Nuevo parámetro
    onTaskProgressUpdate: (String, Double) -> Unit = { _, _ -> } // Nuevo parámetro
) {
    var isExpanded by remember { mutableStateOf(true) }

    val phaseColor = getProjectStatusColor(phase)
    val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
    val phaseProgress = if (tasks.isNotEmpty()) completedTasks.toFloat() / tasks.size else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = phaseColor.copy(alpha = 0.05f)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = phaseColor.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header mejorado de la fase
            PhaseHeader(
                phase = phase,
                taskCount = tasks.size,
                completedTasks = completedTasks,
                progress = phaseProgress,
                isExpanded = isExpanded,
                onToggleExpanded = { isExpanded = !isExpanded }
            )

            // Lista de tareas con animación
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (tasks.isEmpty()) {
                        PhaseEmptyState(phase = phase)
                    } else {
                        tasks.forEachIndexed { index, task ->
                            TaskTimelineCard(
                                task = task,
                                phase = phase,
                                isLast = index == tasks.lastIndex,
                                onClick = { onTaskClick(task) },
                                onStatusUpdate = { status -> onTaskStatusUpdate(task.id, status) }, // Nueva funcionalidad
                                onProgressUpdate = { progress -> onTaskProgressUpdate(task.id, progress) } // Nueva funcionalidad
                            )

                            if (index != tasks.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
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
    progress: Float,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    val phaseColor = getProjectStatusColor(phase)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador visual mejorado
        Card(
            modifier = Modifier.size(48.dp),
            colors = CardDefaults.cardColors(
                containerColor = phaseColor.copy(alpha = 0.2f)
            ),
            border = BorderStroke(2.dp, phaseColor)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = getPhaseIcon(phase),
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Información de la fase
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getProjectStatusDisplayName(phase),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "$completedTasks de $taskCount tareas completadas",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Mini barra de progreso
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .padding(top = 4.dp),
                color = phaseColor,
                trackColor = phaseColor.copy(alpha = 0.2f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Progreso circular
        if (taskCount > 0) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(52.dp),
                    color = phaseColor,
                    strokeWidth = 4.dp,
                    trackColor = phaseColor.copy(alpha = 0.2f)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        // Botón de expandir/contraer
        Card(
            onClick = onToggleExpanded,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun TaskTimelineCard(
    task: ScheduleTask,
    phase: ProjectStatus,
    isLast: Boolean = false,
    onClick: () -> Unit,
    onStatusUpdate: (TaskStatus) -> Unit = {}, // Nuevo parámetro
    onProgressUpdate: (Double) -> Unit = {}, // Nuevo parámetro
    modifier: Modifier = Modifier
) {
    val phaseColor = getProjectStatusColor(phase)
    val taskStatusColor = getTaskStatusColor(task.status)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(
            width = 1.dp,
            color = taskStatusColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Timeline indicator mejorado
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Card(
                        modifier = Modifier.size(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = taskStatusColor
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            when (task.status) {
                                TaskStatus.COMPLETED -> Text("✓", fontSize = 8.sp, color = Color.White)
                                TaskStatus.IN_PROGRESS -> Text("⏳", fontSize = 6.sp)
                                TaskStatus.ON_HOLD -> Text("⏸", fontSize = 6.sp, color = Color.White)
                                else -> Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }

                    // Línea conectora
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(48.dp)
                                .background(
                                    phaseColor.copy(alpha = 0.3f),
                                    RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Contenido de la tarea mejorado
                Column(modifier = Modifier.weight(1f)) {
                    TaskHeader(task = task)

                    if (task.description != null) {
                        TaskDescription(description = task.description!!)
                    }

                    TaskMetadata(task = task)

                    if (task.progress > 0) {
                        TaskProgress(
                            progress = task.progress,
                            phase = phase,
                            onProgressUpdate = onProgressUpdate // Nueva funcionalidad
                        )
                    }

                    // Controles rápidos de estado
                    TaskQuickActions(
                        task = task,
                        onStatusUpdate = onStatusUpdate // Nueva funcionalidad
                    )
                }
            }
        }
    }
}

// Nuevo componente para acciones rápidas
@Composable
private fun TaskQuickActions(
    task: ScheduleTask,
    onStatusUpdate: (TaskStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (task.status == TaskStatus.NOT_STARTED) {
            OutlinedButton(
                onClick = { onStatusUpdate(TaskStatus.IN_PROGRESS) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Iniciar", fontSize = 10.sp)
            }
        }

        if (task.status == TaskStatus.IN_PROGRESS) {
            OutlinedButton(
                onClick = { onStatusUpdate(TaskStatus.COMPLETED) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Completar", fontSize = 10.sp)
            }
        }

        if (task.status == TaskStatus.ON_HOLD) {
            OutlinedButton(
                onClick = { onStatusUpdate(TaskStatus.IN_PROGRESS) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reanudar", fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun PhaseEmptyState(phase: ProjectStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getPhaseIcon(phase),
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "No hay tareas en ${getProjectStatusDisplayName(phase)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Las tareas aparecerán aquí cuando se agreguen a esta fase",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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
    phase: ProjectStatus,
    onProgressUpdate: (Double) -> Unit = {} // Nuevo parámetro
) {
    Column(
        modifier = Modifier.padding(top = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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

        // Botones de progreso rápido
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            listOf(0.25, 0.5, 0.75, 1.0).forEach { progressValue ->
                if (progress < progressValue) {
                    OutlinedButton(
                        onClick = { onProgressUpdate(progressValue) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("${(progressValue * 100).toInt()}%", fontSize = 8.sp)
                    }
                }
            }
        }
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

// Helper functions - moved to end and organized
private fun mapTaskCategoryToProjectStatus(category: TaskCategory): ProjectStatus {
    return when (category) {
        TaskCategory.DESIGN -> ProjectStatus.DESIGN
        TaskCategory.PERMITS -> ProjectStatus.PERMITS_REVIEW
        TaskCategory.CONSTRUCTION, TaskCategory.INSPECTION -> ProjectStatus.CONSTRUCTION
        TaskCategory.DELIVERY -> ProjectStatus.DELIVERY
        else -> ProjectStatus.DESIGN
    }
}

private fun getPhaseIcon(phase: ProjectStatus): String {
    return when (phase) {
        ProjectStatus.DESIGN -> "📐"
        ProjectStatus.PERMITS_REVIEW -> "📋"
        ProjectStatus.CONSTRUCTION -> "🏗️"
        ProjectStatus.DELIVERY -> "🎉"
    }
}

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
