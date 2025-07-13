package com.example.app_mobile.ui.screens.cronograma.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.shared_domain.model.*

// Modelo de tarea extendido para incluir estado de proyecto
data class KanbanTask(
    val task: ScheduleTask,
    val projectStatus: ProjectStatus
)

@Composable
fun KanbanBoardView(
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit = {},
    onTaskMove: (ScheduleTask, ProjectStatus) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    // Estado para manejar el drag & drop
    var draggedTask by remember { mutableStateOf<KanbanTask?>(null) }
    var dragOffset by remember { mutableStateOf(Pair(0f, 0f)) }
    
    // Convertir tareas a KanbanTask con estados de proyecto
    val kanbanTasks = remember(tasks) {
        tasks.map { task ->
            KanbanTask(
                task = task,
                projectStatus = mapTaskCategoryToProjectStatus(task.category)
            )
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            KanbanViewHeader()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(
                    items = ProjectStatus.values().toList(),
                    key = { it.name }
                ) { status ->
                    KanbanColumn(
                        status = status,
                        tasks = kanbanTasks.filter { it.projectStatus == status },
                        onTaskClick = onTaskClick,
                        onTaskMove = { task, newStatus -> 
                            onTaskMove(task.task, newStatus)
                        },
                        draggedTask = draggedTask,
                        onDragStart = { task ->
                            draggedTask = task
                        },
                        onDragEnd = {
                            draggedTask = null
                        },
                        onDrop = { droppedTask, targetStatus ->
                            onTaskMove(droppedTask.task, targetStatus)
                            draggedTask = null
                        }
                    )
                }
            }
            
            // Overlay para la tarea siendo arrastrada
            if (draggedTask != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                ) {
                    DraggedTaskOverlay(
                        task = draggedTask!!,
                        offset = dragOffset
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanViewHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "📋 Vista Kanban",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        TextButton(onClick = { /* TODO: Implementar configuración de columnas */ }) {
            Text("Configurar")
        }
    }
}

@Composable
fun KanbanColumn(
    status: ProjectStatus,
    tasks: List<KanbanTask>,
    onTaskClick: (ScheduleTask) -> Unit,
    onTaskMove: (KanbanTask, ProjectStatus) -> Unit,
    draggedTask: KanbanTask?,
    onDragStart: (KanbanTask) -> Unit,
    onDragEnd: () -> Unit,
    onDrop: (KanbanTask, ProjectStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDropTarget by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .width(280.dp)
            .pointerInput(status) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { },
                    onDragEnd = {
                        if (isDropTarget && draggedTask != null) {
                            onDrop(draggedTask, status)
                        }
                        isDropTarget = false
                        onDragEnd()
                    }
                ) { _, _ ->
                    isDropTarget = true
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isDropTarget && draggedTask != null) {
                getProjectStatusColor(status).copy(alpha = 0.3f)
            } else {
                getProjectStatusColor(status).copy(alpha = 0.1f)
            }
        ),
        border = if (isDropTarget && draggedTask != null) {
            BorderStroke(2.dp, getProjectStatusColor(status))
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            KanbanColumnHeader(
                status = status,
                taskCount = tasks.size
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Lista de tareas en la columna
            tasks.forEach { kanbanTask ->
                if (draggedTask?.task?.id != kanbanTask.task.id) {
                    DraggableTaskCard(
                        kanbanTask = kanbanTask,
                        onClick = { onTaskClick(kanbanTask.task) },
                        onDragStart = { onDragStart(kanbanTask) },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            
            // Placeholder para cuando no hay tareas
            if (tasks.isEmpty()) {
                KanbanEmptyState(status = status)
            }
        }
    }
}

@Composable
private fun KanbanColumnHeader(
    status: ProjectStatus,
    taskCount: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(getProjectStatusColor(status))
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "${getProjectStatusDisplayName(status)} ($taskCount)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        
        // Badge con contador
        if (taskCount > 0) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = taskCount.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun DraggableTaskCard(
    kanbanTask: KanbanTask,
    onClick: () -> Unit,
    onDragStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(kanbanTask.task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { 
                        isDragging = true
                        onDragStart()
                    },
                    onDragEnd = {
                        isDragging = false
                    }
                ) { _, _ -> }
            }
            .graphicsLayer {
                alpha = if (isDragging) 0.5f else 1f
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            KanbanTaskHeader(task = kanbanTask.task)
            
            if (kanbanTask.task.description != null) {
                KanbanTaskDescription(description = kanbanTask.task.description)
            }
            
            KanbanTaskFooter(task = kanbanTask.task)
            
            if (kanbanTask.task.progress > 0) {
                KanbanTaskProgress(
                    progress = kanbanTask.task.progress,
                    status = kanbanTask.projectStatus
                )
            }
        }
    }
}

@Composable
private fun DraggedTaskOverlay(
    task: KanbanTask,
    offset: Pair<Float, Float>
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .offset(offset.first.dp, offset.second.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .zIndex(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = task.task.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (task.task.description != null) {
                Text(
                    text = task.task.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun KanbanTaskHeader(task: ScheduleTask) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = task.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        
        // Indicador de prioridad
        if (task.priority == TaskPriority.CRITICAL || task.priority == TaskPriority.HIGH) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(getPriorityColor(task.priority))
            )
        }
    }
}

@Composable
private fun KanbanTaskDescription(description: String) {
    Text(
        text = description,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 6.dp),
        maxLines = 2
    )
}

@Composable
private fun KanbanTaskFooter(task: ScheduleTask) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Asignado
        if (task.assignedTo.isNotEmpty()) {
            Text(
                text = "👤 ${task.assignedTo.first()}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Prioridad badge
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = getPriorityColor(task.priority),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = getDisplayPriority(task.priority),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun KanbanTaskProgress(
    progress: Double,
    status: ProjectStatus
) {
    Column(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = getProjectStatusColor(status)
        )
        
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
        )
    }
}

@Composable
private fun KanbanEmptyState(status: ProjectStatus) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📋",
                fontSize = 24.sp
            )
            Text(
                text = "Sin tareas en ${getProjectStatusDisplayName(status)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper functions para estados de proyecto
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

private fun getPriorityColor(priority: TaskPriority): Color {
    return when (priority) {
        TaskPriority.LOW -> Color(0xFF4CAF50)
        TaskPriority.MEDIUM -> Color(0xFFFF9800)
        TaskPriority.HIGH -> Color(0xFFFF5722)
        TaskPriority.CRITICAL -> Color(0xFFF44336)
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

// Función para mapear categoría de tarea a estado de proyecto
private fun mapTaskCategoryToProjectStatus(category: TaskCategory): ProjectStatus {
    return when (category) {
        TaskCategory.DESIGN -> ProjectStatus.DESIGN
        TaskCategory.PERMITS -> ProjectStatus.PERMITS_REVIEW
        TaskCategory.CONSTRUCTION, TaskCategory.INSPECTION -> ProjectStatus.CONSTRUCTION
        TaskCategory.DELIVERY -> ProjectStatus.DELIVERY
        else -> ProjectStatus.DESIGN
    }
}
