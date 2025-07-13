package com.example.app_mobile.ui.screens.cronograma

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_mobile.ui.screens.cronograma.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen(
    onNavigateToTaskDetail: (String) -> Unit = {},
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToMilestoneDetail: (String) -> Unit = {},
    onNavigateToScheduleSettings: () -> Unit = {},
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
            // TODO: Mostrar snackbar o toast
            viewModel.clearMessage()
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
                    IconButton(onClick = onNavigateToScheduleSettings) {
                        Icon(
                            Icons.Default.Settings, 
                            contentDescription = "Configuración de cronograma"
                        )
                    }
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
                contentAlignment = androidx.compose.ui.Alignment.Center
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
                // Header empresarial
                item {
                    CronogramaHeader()
                }
                
                // Información del cronograma
                currentSchedule?.let { schedule =>
                    item {
                        ScheduleInfoCard(schedule = schedule)
                    }
                }
                
                // Selector de vista
                item {
                    ViewSelector(
                        selectedView = uiState.selectedView,
                        onViewChange = viewModel::setViewType
                    )
                }
                
                // Leyenda de estados
                item {
                    StatusLegend()
                }
                
                // Contenido principal según vista seleccionada
                item {
                    when (uiState.selectedView) {
                        ViewType.CRONOGRAMA -> {
                            CronogramaTimelineView(
                                tasks = allTasks,
                                onTaskClick = { task ->
                                    onNavigateToTaskDetail(task.id)
                                }
                            )
                        }
                        ViewType.KANBAN -> {
                            KanbanBoardView(
                                tasks = allTasks,
                                onTaskClick = { task ->
                                    onNavigateToTaskDetail(task.id)
                                },
                                onTaskMove = viewModel::moveTaskToPhase
                            )
                        }
                    }
                }
                
                // Hitos del proyecto
                item {
                    MilestonesSection(
                        milestones = milestones,
                        onMilestoneClick = { milestone ->
                            onNavigateToMilestoneDetail(milestone.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CronogramaHeader() {
    Column(
        modifier = Modifier.fillMaxWidth()
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
private fun ScheduleInfoCard(schedule: ProjectSchedule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                StatItem(
                    label = "Total Tareas",
                    value = schedule.totalTasks.toString()
                )
                StatItem(
                    label = "Completadas",
                    value = schedule.completedTasks.toString()
                )
                StatItem(
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
private fun ViewSelector(
    selectedView: ViewType,
    onViewChange: (ViewType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
private fun StatusLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estados de las Tareas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusLegendItem(
                    status = TaskStatus.NOT_STARTED,
                    label = "Por Iniciar",
                    color = Color(0xFF2196F3)
                )
                StatusLegendItem(
                    status = TaskStatus.IN_PROGRESS,
                    label = "En Proceso",
                    color = Color(0xFFFF9800)
                )
                StatusLegendItem(
                    status = TaskStatus.COMPLETED,
                    label = "Completado",
                    color = Color(0xFF4CAF50)
                )
                StatusLegendItem(
                    status = TaskStatus.ON_HOLD,
                    label = "En Pausa",
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
private fun StatusLegendItem(
    status: TaskStatus,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CronogramaView(tasks: List<ScheduleTask>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📅 Vista de Cronograma",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            tasks.forEach { task ->
                TaskCard(task = task)
                if (task != tasks.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun KanbanView(tasks: List<ScheduleTask>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📋 Vista Kanban",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(TaskStatus.values()) { status =>
                    KanbanColumn(
                        status = status,
                        tasks = tasks.filter { it.status == status }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: ScheduleTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            
            if (task.description != null) {
                Text(
                    text = task.description,
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
                        text = "👤 ${task.assignedTo.first()}", // Mostrar primer asignado
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Horas estimadas vs actuales
            if (task.estimatedHours != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏱️ Est: ${task.estimatedHours}h",
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
                    text = "Progreso: ${task.progressPercentage}%",
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
    tasks: List<ScheduleTask>
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
                KanbanTaskCard(task = task)
                if (task != tasks.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun KanbanTaskCard(task: ScheduleTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            
            if (task.description != null) {
                Text(
                    text = task.description,
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
private fun MilestonesSection(milestones: List<Milestone>) {
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
                MilestoneCard(milestone = milestone)
                if (milestone != milestones.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MilestoneCard(milestone: Milestone) {
    Card(
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

// Función auxiliar para mapear ProjectStatus a TaskCategory
private fun mapProjectStatusToTaskCategory(status: ProjectStatus): TaskCategory {
    return when (status) {
        ProjectStatus.DESIGN -> TaskCategory.DESIGN
        ProjectStatus.PERMITS_REVIEW -> TaskCategory.PERMITS
        ProjectStatus.CONSTRUCTION -> TaskCategory.CONSTRUCTION
        ProjectStatus.DELIVERY -> TaskCategory.DELIVERY
    }
}
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

// Función auxiliar para mapear ProjectStatus a TaskCategory
private fun mapProjectStatusToTaskCategory(status: ProjectStatus): TaskCategory {
    return when (status) {
        ProjectStatus.DESIGN -> TaskCategory.DESIGN
        ProjectStatus.PERMITS_REVIEW -> TaskCategory.PERMITS
        ProjectStatus.CONSTRUCTION -> TaskCategory.CONSTRUCTION
        ProjectStatus.DELIVERY -> TaskCategory.DELIVERY
    }
}
