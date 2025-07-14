package com.example.app_mobile.ui.screens.cronograma.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.shared_domain.model.*
import kotlin.math.roundToInt

// Modelo de tarea extendido para incluir estado de proyecto
data class KanbanTask(
    val task: ScheduleTask,
    val projectStatus: ProjectStatus
)

// Estado global para drag & drop
class DragDropState {
    var draggedTask by mutableStateOf<KanbanTask?>(null)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var dropTargetColumn by mutableStateOf<ProjectStatus?>(null)
    
    fun startDrag(task: KanbanTask, position: Offset) {
        draggedTask = task
        dragPosition = position
        dragOffset = Offset.Zero
    }
    
    fun updateDrag(offset: Offset) {
        dragOffset = offset
    }
    
    fun endDrag() {
        draggedTask = null
        dragOffset = Offset.Zero
        dropTargetColumn = null
    }
    
    fun setDropTarget(column: ProjectStatus?) {
        dropTargetColumn = column
    }
}

@Composable
fun KanbanBoardView(
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit = {},
    onTaskMove: (ScheduleTask, ProjectStatus) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val dragDropState = remember { DragDropState() }
    
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
        Box {
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
                            tasks = kanbanTasks.filter { 
                                it.projectStatus == status && it.task.id != dragDropState.draggedTask?.task?.id 
                            },
                            onTaskClick = onTaskClick,
                            dragDropState = dragDropState,
                            onTaskMove = onTaskMove
                        )
                    }
                }
            }
            
            // Overlay para la tarea siendo arrastrada
            dragDropState.draggedTask?.let { draggedTask ->
                DraggedTaskOverlay(
                    task = draggedTask,
                    position = dragDropState.dragPosition + dragDropState.dragOffset
                )
            }
        }
    }
}

@Composable
private fun KanbanViewHeader() {
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
                    text = "📋",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Vista Kanban",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { /* TODO: Implementar configuración de columnas */ },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurar",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun KanbanColumn(
    status: ProjectStatus,
    tasks: List<KanbanTask>,
    onTaskClick: (ScheduleTask) -> Unit,
    dragDropState: DragDropState,
    onTaskMove: (ScheduleTask, ProjectStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnColor = getProjectStatusColor(status)
    val isDropTarget = dragDropState.dropTargetColumn == status
    
    // Animación para el estado de drop
    val animatedElevation by animateDpAsState(
        targetValue = if (isDropTarget && dragDropState.draggedTask != null) 8.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elevation_animation"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isDropTarget && dragDropState.draggedTask != null) 0.4f else 0.1f,
        animationSpec = tween(200),
        label = "alpha_animation"
    )
    
    Card(
        modifier = modifier
            .width(300.dp)
            .heightIn(min = 200.dp)
            .pointerInput(status) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { _ ->
                        dragDropState.setDropTarget(status)
                    },
                    onDragEnd = {
                        val draggedTask = dragDropState.draggedTask
                        if (isDropTarget && draggedTask != null && draggedTask.projectStatus != status) {
                            onTaskMove(draggedTask.task, status)
                        }
                        dragDropState.endDrag()
                    }
                ) { _, dragAmount ->
                    dragDropState.updateDrag(dragDropState.dragOffset + Offset(dragAmount.x, dragAmount.y))
                    dragDropState.setDropTarget(status)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = columnColor.copy(alpha = animatedAlpha)
        ),
        border = if (isDropTarget && dragDropState.draggedTask != null) {
            BorderStroke(3.dp, columnColor)
        } else {
            BorderStroke(1.dp, columnColor.copy(alpha = 0.3f))
        },
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            KanbanColumnHeader(
                status = status,
                taskCount = tasks.size,
                color = columnColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de tareas con mejor espaciado
            if (tasks.isEmpty()) {
                KanbanEmptyState(status = status)
            } else {
                tasks.forEach { kanbanTask ->
                    DraggableTaskCard(
                        kanbanTask = kanbanTask,
                        onClick = { onTaskClick(kanbanTask.task) },
                        dragDropState = dragDropState,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanColumnHeader(
    status: ProjectStatus,
    taskCount: Int,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Icono de fase
            Text(
                text = getPhaseIcon(status),
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getProjectStatusDisplayName(status),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$taskCount tareas",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Badge con contador mejorado
            if (taskCount > 0) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = color
                    )
                ) {
                    Text(
                        text = taskCount.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DraggableTaskCard(
    kanbanTask: KanbanTask,
    onClick: () -> Unit,
    dragDropState: DragDropState,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    
    val animatedElevation by animateDpAsState(
        targetValue = if (isDragging) 12.dp else 3.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elevation_animation"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.7f else 1f,
        animationSpec = tween(200),
        label = "alpha_animation"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isDragging) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale_animation"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { 
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha 
            }
            .pointerInput(kanbanTask.task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset -> 
                        isDragging = true
                        dragDropState.startDrag(kanbanTask, offset)
                    },
                    onDragEnd = {
                        isDragging = false
                    }
                ) { _, dragAmount ->
                    dragDropState.updateDrag(dragDropState.dragOffset + Offset(dragAmount.x, dragAmount.y))
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        border = BorderStroke(
            width = if (isDragging) 2.dp else 1.dp,
            color = getProjectStatusColor(kanbanTask.projectStatus).copy(
                alpha = if (isDragging) 0.8f else 0.3f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
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
    position: Offset
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .offset { 
                IntOffset(
                    x = position.x.roundToInt(),
                    y = position.y.roundToInt()
                ) 
            }
            .shadow(12.dp, RoundedCornerShape(8.dp))
            .zIndex(10f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = task.task.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            task.task.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getPriorityColor(task.task.priority),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = getDisplayPriority(task.task.priority),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
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
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
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
            color = getProjectStatusColor(status),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 2.dp)
        )
    }
}

@Composable
private fun KanbanEmptyState(status: ProjectStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getPhaseIcon(status),
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Sin tareas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Arrastra tareas aquí",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Helper functions
private fun getPhaseIcon(status: ProjectStatus): String {
    return when (status) {
        ProjectStatus.DESIGN -> "📐"
        ProjectStatus.PERMITS_REVIEW -> "📋"
        ProjectStatus.CONSTRUCTION -> "🏗️"
        ProjectStatus.DELIVERY -> "🎉"
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
