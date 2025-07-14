package com.example.app_mobile.ui.screens.cronograma

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shared_domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CronogramaViewModel = viewModel()
) {
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var estimatedHours by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.DESIGN) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var assignedMember by remember { mutableStateOf("") }
    
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isPriorityDropdownExpanded by remember { mutableStateOf(false) }
    
    val categoryOptions = listOf(
        TaskCategory.DESIGN to "Diseño",
        TaskCategory.PERMITS to "Permisos",
        TaskCategory.CONSTRUCTION to "Construcción",
        TaskCategory.DELIVERY to "Entrega",
        TaskCategory.INSPECTION to "Inspección",
        TaskCategory.DOCUMENTATION to "Documentación",
        TaskCategory.OTHER to "Otro"
    )
    
    val priorityOptions = listOf(
        TaskPriority.LOW to "Baja",
        TaskPriority.MEDIUM to "Media",
        TaskPriority.HIGH to "Alta",
        TaskPriority.CRITICAL to "Crítica"
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Navegar de vuelta cuando se cree la tarea exitosamente
    LaunchedEffect(uiState.message) {
        if (uiState.message?.contains("creada correctamente") == true) {
            onNavigateBack()
        }
    }
    
    // Función para validar campos obligatorios
    val isFormValid = taskName.isNotBlank() && 
                     startDate.isNotBlank() && 
                     endDate.isNotBlank()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Nueva Tarea",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, 
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isFormValid) {
                        val newTask = ScheduleTask(
                            id = "task_${System.currentTimeMillis()}",
                            name = taskName.trim(),
                            description = taskDescription.trim().takeIf { it.isNotBlank() },
                            startDate = startDate.trim(),
                            endDate = endDate.trim(),
                            progress = 0.0,
                            status = TaskStatus.NOT_STARTED,
                            priority = selectedPriority,
                            assignedTo = if (assignedMember.trim().isNotBlank()) {
                                listOf(assignedMember.trim())
                            } else {
                                emptyList()
                            },
                            category = selectedCategory,
                            estimatedHours = estimatedHours.trim().toIntOrNull(),
                            actualHours = 0
                        )
                        viewModel.createTask(newTask)
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save, 
                        contentDescription = "Guardar tarea"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información básica
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Información Básica",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        OutlinedTextField(
                            value = taskName,
                            onValueChange = { taskName = it },
                            label = { Text("Nombre de la Tarea *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = taskName.isBlank()
                        )
                        
                        OutlinedTextField(
                            value = taskDescription,
                            onValueChange = { taskDescription = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        
                        // Categoría
                        ExposedDropdownMenuBox(
                            expanded = isCategoryDropdownExpanded,
                            onExpandedChange = { isCategoryDropdownExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = categoryOptions.find { it.first == selectedCategory }?.second ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Categoría") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = isCategoryDropdownExpanded
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = isCategoryDropdownExpanded,
                                onDismissRequest = { isCategoryDropdownExpanded = false }
                            ) {
                                categoryOptions.forEach { (category, displayName) ->
                                    DropdownMenuItem(
                                        text = { Text(displayName) },
                                        onClick = {
                                            selectedCategory = category
                                            isCategoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Prioridad
                        ExposedDropdownMenuBox(
                            expanded = isPriorityDropdownExpanded,
                            onExpandedChange = { isPriorityDropdownExpanded = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = priorityOptions.find { it.first == selectedPriority }?.second ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Prioridad") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = isPriorityDropdownExpanded
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = isPriorityDropdownExpanded,
                                onDismissRequest = { isPriorityDropdownExpanded = false }
                            ) {
                                priorityOptions.forEach { (priority, displayName) ->
                                    DropdownMenuItem(
                                        text = { Text(displayName) },
                                        onClick = {
                                            selectedPriority = priority
                                            isPriorityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Fechas y tiempo
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Cronograma",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = { startDate = it },
                                label = { Text("Fecha Inicio *") },
                                placeholder = { Text("YYYY-MM-DD") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                isError = startDate.isBlank()
                            )
                            
                            OutlinedTextField(
                                value = endDate,
                                onValueChange = { endDate = it },
                                label = { Text("Fecha Fin *") },
                                placeholder = { Text("YYYY-MM-DD") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                isError = endDate.isBlank()
                            )
                        }
                        
                        OutlinedTextField(
                            value = estimatedHours,
                            onValueChange = { newValue ->
                                // Solo permitir números
                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                    estimatedHours = newValue
                                }
                            },
                            label = { Text("Horas Estimadas") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            suffix = { Text("horas") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                
                // Asignación
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Asignación",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        OutlinedTextField(
                            value = assignedMember,
                            onValueChange = { assignedMember = it },
                            label = { Text("Miembro del Equipo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Ej: arq_steve") }
                        )
                    }
                }
                
                // Espaciado adicional para el FAB
                Spacer(modifier = Modifier.height(80.dp))
            }
            
            // Overlay de carga
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}