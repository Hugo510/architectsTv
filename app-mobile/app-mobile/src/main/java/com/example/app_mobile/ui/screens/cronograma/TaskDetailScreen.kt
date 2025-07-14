package com.example.app_mobile.ui.screens.cronograma

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.app_mobile.ui.screens.cronograma.CronogramaViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    onEditTask: (String) -> Unit = {},
    onStartTask: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CronogramaViewModel = viewModel()
) {
    val task by viewModel.getTaskById(taskId).collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    task?.let { currentTask ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Detalle de Tarea",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditTask(taskId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar tarea")
                        }
                    }
                )
            },
            floatingActionButton = if (currentTask.status != TaskStatus.COMPLETED) {
                {
                    ExtendedFloatingActionButton(
                        onClick = { 
                            val newStatus = when (currentTask.status) {
                                TaskStatus.NOT_STARTED -> TaskStatus.IN_PROGRESS
                                TaskStatus.ON_HOLD -> TaskStatus.IN_PROGRESS
                                else -> TaskStatus.IN_PROGRESS
                            }
                            viewModel.updateTaskStatus(taskId, newStatus)
                        },
                        icon = { 
                            Icon(
                                Icons.Default.PlayArrow, 
                                contentDescription = null
                            ) 
                        },
                        text = { 
                            Text(
                                when (currentTask.status) {
                                    TaskStatus.NOT_STARTED -> "Iniciar Tarea"
                                    TaskStatus.IN_PROGRESS -> "Continuar"
                                    TaskStatus.ON_HOLD -> "Reanudar"
                                    else -> "Actualizar"
                                }
                            ) 
                        }
                    )
                }
            } else null
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
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header de la tarea
                    TaskDetailHeader(task = currentTask)
                    
                    // Información general
                    TaskGeneralInfo(task = currentTask)
                    
                    // Progreso y tiempo
                    TaskProgressInfo(
                        task = currentTask,
                        onProgressUpdate = { progress ->
                            viewModel.updateTaskProgress(taskId, progress)
                        }
                    )
                    
                    // Equipo asignado
                    TaskTeamInfo(task = currentTask)
                    
                    // Controles de estado
                    TaskStatusControls(
                        task = currentTask,
                        onStatusChange = { status ->
                            viewModel.updateTaskStatus(taskId, status)
                        }
                    )
                    
                    // Actividad reciente
                    TaskActivitySection()
                }
            }
        }
    } ?: run {
        // Mostrar loading o error si la tarea no existe
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Tarea no encontrada")
        }
    }
}

@Composable
private fun TaskDetailHeader(task: ScheduleTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    if (task.description != null) {
                        Text(
                            text = task.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                
                // Estado y prioridad
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getTaskStatusColor(task.status)
                    ) {
                        Text(
                            text = getDisplayStatus(task.status),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getPriorityColor(task.priority)
                    ) {
                        Text(
                            text = "Prioridad ${getDisplayPriority(task.priority)}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskGeneralInfo(task: ScheduleTask) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información General",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            InfoRow(
                label = "Fecha de Inicio",
                value = task.startDate,
                icon = "📅"
            )
            
            InfoRow(
                label = "Fecha de Fin",
                value = task.endDate,
                icon = "🏁"
            )
            
            InfoRow(
                label = "Categoría",
                value = getCategoryDisplayName(task.category),
                icon = "📋"
            )
            
            if (task.dependencies.isNotEmpty()) {
                InfoRow(
                    label = "Dependencias",
                    value = "${task.dependencies.size} tareas",
                    icon = "🔗"
                )
            }
        }
    }
}

@Composable
private fun TaskProgressInfo(
    task: ScheduleTask,
    onProgressUpdate: (Double) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Progreso y Tiempo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Progreso visual con botones de actualización
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso Actual",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(task.progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                LinearProgressIndicator(
                    progress = { task.progress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = getTaskStatusColor(task.status)
                )
                
                // Botones de progreso rápido
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.25, 0.5, 0.75, 1.0).forEach { progress ->
                        OutlinedButton(
                            onClick = { onProgressUpdate(progress) },
                            modifier = Modifier.weight(1f),
                            enabled = task.status != TaskStatus.COMPLETED
                        ) {
                            Text("${(progress * 100).toInt()}%")
                        }
                    }
                }
            }
            
            // Información de horas
            if (task.estimatedHours != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TimeInfoCard(
                        label = "Estimado",
                        value = "${task.estimatedHours}h",
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TimeInfoCard(
                        label = "Actual",
                        value = "${task.actualHours ?: 0}h",
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TimeInfoCard(
                        label = "Restante",
                        value = "${(task.estimatedHours ?: 0) - (task.actualHours ?: 0)}h",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskTeamInfo(task: ScheduleTask) {
    if (task.assignedTo.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Equipo Asignado",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                task.assignedTo.forEach { memberId ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = memberId.take(2).uppercase(),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = memberId.replace("_", " ").replaceFirstChar { it.uppercase() },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Miembro del equipo",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskStatusControls(
    task: ScheduleTask,
    onStatusChange: (TaskStatus) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Control de Estado",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskStatus.values().forEach { status ->
                    if (status != TaskStatus.CANCELLED) {
                        FilterChip(
                            selected = task.status == status,
                            onClick = { onStatusChange(status) },
                            label = { Text(getDisplayStatus(status)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskActivitySection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Actividad Reciente",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Lista de actividades mockeadas
            listOf(
                "Tarea iniciada por Arq. Steve" to "Hace 2 horas",
                "Progreso actualizado al 75%" to "Hace 1 día",
                "Comentario agregado" to "Hace 2 días"
            ).forEach { (activity, time) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = activity,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TimeInfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper functions
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

private fun getCategoryDisplayName(category: TaskCategory): String {
    return when (category) {
        TaskCategory.DESIGN -> "Diseño"
        TaskCategory.PERMITS -> "Permisos"
        TaskCategory.CONSTRUCTION -> "Construcción"
        TaskCategory.DELIVERY -> "Entrega"
        TaskCategory.INSPECTION -> "Inspección"
        TaskCategory.DOCUMENTATION -> "Documentación"
        TaskCategory.OTHER -> "Otro"
    }
}
