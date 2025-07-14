package com.example.app_mobile.ui.screens.cronograma

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_mobile.ui.screens.cronograma.components.*
import com.example.shared_domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen(
    onNavigateToTaskDetail: (String) -> Unit = {},
    onNavigateToCreateTask: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CronogramaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSchedule by viewModel.currentSchedule.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val milestones by viewModel.milestones.collectAsState()
    
    // Mostrar mensajes
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            // Implementación de SnackbarHost para mostrar mensajes
            // viewModel.clearMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Cronogramas",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    // Eliminar botón de configuración, dejar TODO
                    // TODO: Botón de configuración eliminado, no implementado
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header empresarial mejorado
                item {
                    CronogramaHeader() // Usar componente avanzado
                }
                
                // Información del cronograma mejorada
                currentSchedule?.let { schedule ->
                    item {
                        ScheduleInfoCard(schedule = schedule) // Usar componente avanzado
                    }
                }
                
                // Selector de vista mejorado
                item {
                    ViewSelector(
                        selectedView = uiState.selectedView,
                        onViewChange = viewModel::setViewType
                    )
                }
                
                // Leyenda de estados mejorada
                item {
                    StatusLegend() // Usar componente avanzado
                }
                
                // Contenido principal según vista seleccionada
                item {
                    when (uiState.selectedView) {
                        ViewType.CRONOGRAMA -> {
                            CronogramaTimelineView(
                                tasks = allTasks,
                                onTaskClick = { task ->
                                    onNavigateToTaskDetail(task.id)
                                },
                                onTaskStatusUpdate = { taskId, status ->
                                    viewModel.updateTaskStatus(taskId, status)
                                },
                                onTaskProgressUpdate = { taskId, progress ->
                                    viewModel.updateTaskProgress(taskId, progress)
                                }
                            )
                        }
                        ViewType.KANBAN -> {
                            KanbanBoardView(
                                tasks = allTasks,
                                onTaskClick = { task ->
                                    onNavigateToTaskDetail(task.id)
                                },
                                onTaskMove = { task, newPhase ->
                                    viewModel.moveTaskToPhase(task, newPhase)
                                }
                            )
                        }
                    }
                }
                
                // Hitos del proyecto mejorados
                item {
                    // La sección de hitos solo muestra información, no navega a detalle
                    MilestonesSection(
                        milestones = milestones,
                        onMilestoneClick = { /* No hace nada */ },
                        onMilestoneToggle = { milestoneId ->
                            viewModel.toggleMilestoneCompletion(milestoneId)
                        }
                    )
                }
            }
        }
    }
}

// Implementaciones simples de componentes
@Composable
private fun CronogramaHeaderSimple() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Panel de Control",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Gestión de cronogramas y tareas",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ScheduleInfoCardSimple(schedule: ProjectSchedule) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = schedule.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            schedule.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Inicio", schedule.startDate)
                StatItem("Fin", schedule.endDate)
                StatItem("Estado", getDisplayProjectStatus(schedule.status))
            }
        }
    }
}

@Composable
private fun ViewSelectorSimple(
    selectedView: ViewType,
    onViewChange: (ViewType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            ViewType.values().forEach { viewType ->
                FilterChip(
                    onClick = { onViewChange(viewType) },
                    label = { 
                        Text(
                            when (viewType) {
                                ViewType.CRONOGRAMA -> "Cronograma"
                                ViewType.KANBAN -> "Kanban"
                            }
                        ) 
                    },
                    selected = selectedView == viewType,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusLegendSimple() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Leyenda de Estados",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TaskStatus.values()) { status =>
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(getTaskStatusColor(status))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = getDisplayStatus(status),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CronogramaTimelineViewSimple(
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Vista Cronograma",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (tasks.isEmpty()) {
                Text(
                    text = "No hay tareas disponibles",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                tasks.forEachIndexed { index, task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClick(task) }
                    )
                    if (index < tasks.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanBoardViewSimple(
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit,
    onTaskMove: (ScheduleTask, TaskStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Vista Kanban",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(TaskStatus.values()) { status =>
                    val statusTasks = tasks.filter { it.status == status }
                    KanbanColumn(
                        status = status,
                        tasks = statusTasks,
                        onTaskClick = onTaskClick
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
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
private fun TaskCard(
    task: ScheduleTask,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            getTaskStatusColor(task.status).copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                // Estado y prioridad
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Prioridad
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getPriorityColor(task.priority)
                    ) {
                        Text(
                            text = getDisplayPriority(task.priority),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Estado
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getTaskStatusColor(task.status)
                    ) {
                        Text(
                            text = getDisplayStatus(task.status),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            task.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Información de fechas y asignación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📅 ${task.startDate} - ${task.endDate}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (task.assignedTo.isNotEmpty()) {
                    Text(
                        text = "👤 ${task.assignedTo.first()}", 
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Horas estimadas vs actuales
            task.estimatedHours?.let { estimatedHours ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏱️ Est: ${estimatedHours}h",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "⏰ Real: ${task.actualHours ?: 0}h",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Progreso
            if (task.progress > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { task.progress.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = getTaskStatusColor(task.status)
                )
                Text(
                    text = "Progreso: ${(task.progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun KanbanColumn(
    status: TaskStatus,
    tasks: List<ScheduleTask>,
    onTaskClick: (ScheduleTask) -> Unit
) {
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = getTaskStatusColor(status).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header de la columna
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(getTaskStatusColor(status))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${getDisplayStatus(status)} (${tasks.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Items de la columna
            tasks.forEach { task ->
                KanbanTaskCard(
                    task = task,
                    onClick = { onTaskClick(task) }
                )
                if (task != tasks.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun KanbanTaskCard(
    task: ScheduleTask,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = task.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            task.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            
            if (task.assignedTo.isNotEmpty()) {
                Text(
                    text = "👤 ${task.assignedTo.first()}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Prioridad
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
            
            if (task.progress > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { task.progress.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = getTaskStatusColor(task.status)
                )
            }
        }
    }
}

@Composable
private fun MilestonesSection(
    milestones: List<Milestone>,
    onMilestoneClick: (Milestone) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                
                milestone.description?.let { description ->
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = if (milestone.isCompleted) 
                        "Completado: ${milestone.completedDate ?: "Fecha no especificada"}"
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

// Helper functions para colores y display
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

private fun getMilestoneImportanceColor(importance: MilestoneImportance): Color {
    return when (importance) {
        MilestoneImportance.LOW -> Color(0xFF4CAF50)
        MilestoneImportance.MEDIUM -> Color(0xFFFF9800)
        MilestoneImportance.HIGH -> Color(0xFFFF5722)
        MilestoneImportance.CRITICAL -> Color(0xFFF44336)
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

private fun getDisplayImportance(importance: MilestoneImportance): String {
    return when (importance) {
        MilestoneImportance.LOW -> "Baja"
        MilestoneImportance.MEDIUM -> "Media"
        MilestoneImportance.HIGH -> "Alta"
        MilestoneImportance.CRITICAL -> "Crítica"
    }
}

private fun getDisplayProjectStatus(status: ProjectStatus): String {
    return when (status) {
        ProjectStatus.DESIGN -> "Diseño"
        ProjectStatus.PERMITS_REVIEW -> "Revisión Permisos"
        ProjectStatus.CONSTRUCTION -> "Construcción"
        ProjectStatus.DELIVERY -> "Entrega"
    }
}

// Función auxiliar para mapear ProjectStatus a TaskCategory
private fun mapProjectStatusToTaskCategory(status: ProjectStatus): TaskCategory {
    return when (status) {
        ProjectStatus.DESIGN -> TaskCategory.DESIGN
        ProjectStatus.PERMITS_REVIEW -> TaskCategory.PERMITS
        ProjectStatus.CONSTRUCTION -> TaskCategory.CONSTRUCTION
        ProjectStatus.DELIVERY -> TaskCategory.DELIVERY
    }
}
        ProjectStatus.DELIVERY -> TaskCategory.DELIVERY
    }
}
        ProjectStatus.CONSTRUCTION -> TaskCategory.CONSTRUCTION
        ProjectStatus.DELIVERY -> TaskCategory.DELIVERY
    }
}

