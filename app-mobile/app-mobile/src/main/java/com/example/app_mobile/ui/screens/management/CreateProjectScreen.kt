package com.example.app_mobile.ui.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onNavigateBack: () -> Unit,
    onSaveProject: (Project) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ProjectStatus.DESIGN) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }
    
    // Ubicación
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    
    // Presupuesto
    var budgetAmount by remember { mutableStateOf("") }
    
    // Cliente
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var clientCompany by remember { mutableStateOf("") }
    
    // Project Manager
    var pmName by remember { mutableStateOf("") }
    var pmTitle by remember { mutableStateOf("") }
    var pmEmail by remember { mutableStateOf("") }
    
    // Timeline
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var estimatedDuration by remember { mutableStateOf("") }
    
    val statusOptions = listOf(
        ProjectStatus.DESIGN to "Diseño",
        ProjectStatus.PERMITS_REVIEW to "Revisión de Permisos",
        ProjectStatus.CONSTRUCTION to "Construcción",
        ProjectStatus.DELIVERY to "Entrega"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Nuevo Proyecto",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (projectName.isNotBlank() && address.isNotBlank() && 
                        city.isNotBlank() && state.isNotBlank() && clientName.isNotBlank() &&
                        pmName.isNotBlank() && pmTitle.isNotBlank() && startDate.isNotBlank() &&
                        endDate.isNotBlank() && estimatedDuration.isNotBlank()) {
                        
                        val project = Project(
                            id = System.currentTimeMillis().toString(),
                            name = projectName,
                            description = projectDescription.takeIf { it.isNotBlank() },
                            status = selectedStatus,
                            location = ProjectLocation(
                                address = address,
                                city = city,
                                state = state
                            ),
                            budget = budgetAmount.toDoubleOrNull()?.let { Money(it) },
                            client = Client(
                                id = "client_${System.currentTimeMillis()}",
                                name = clientName,
                                email = clientEmail.takeIf { it.isNotBlank() },
                                phone = clientPhone.takeIf { it.isNotBlank() },
                                company = clientCompany.takeIf { it.isNotBlank() }
                            ),
                            projectManager = ProjectManager(
                                id = "pm_${System.currentTimeMillis()}",
                                name = pmName,
                                title = pmTitle,
                                email = pmEmail.takeIf { it.isNotBlank() }
                            ),
                            timeline = ProjectTimeline(
                                startDate = startDate,
                                endDate = endDate,
                                estimatedDuration = estimatedDuration.toIntOrNull() ?: 30
                            ),
                            metadata = ProjectMetadata(
                                createdAt = "2024-01-15T00:00:00Z",
                                updatedAt = "2024-01-15T00:00:00Z"
                            ),
                            progress = 0.0
                        )
                        
                        onSaveProject(project)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = "Guardar proyecto",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Header con logo (simplificado para sub-pantalla)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Crear Nuevo Proyecto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Información básica del proyecto
            SectionCard(title = "Información del Proyecto") {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Nombre del Proyecto *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = projectDescription,
                    onValueChange = { projectDescription = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                // Estado del proyecto
                ExposedDropdownMenuBox(
                    expanded = isStatusDropdownExpanded,
                    onExpandedChange = { isStatusDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = statusOptions.find { it.first == selectedStatus }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isStatusDropdownExpanded,
                        onDismissRequest = { isStatusDropdownExpanded = false }
                    ) {
                        statusOptions.forEach { (status, displayName) ->
                            DropdownMenuItem(
                                text = { Text(displayName) },
                                onClick = {
                                    selectedStatus = status
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Ubicación
            SectionCard(title = "Ubicación") {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ciudad *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("Estado *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            
            // Presupuesto
            SectionCard(title = "Presupuesto") {
                OutlinedTextField(
                    value = budgetAmount,
                    onValueChange = { budgetAmount = it },
                    label = { Text("Presupuesto (MXN)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("$") }
                )
            }
            
            // Cliente
            SectionCard(title = "Información del Cliente") {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nombre del Cliente *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = clientCompany,
                    onValueChange = { clientCompany = it },
                    label = { Text("Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = clientEmail,
                        onValueChange = { clientEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { clientPhone = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            
            // Project Manager
            SectionCard(title = "Director de Proyecto") {
                OutlinedTextField(
                    value = pmName,
                    onValueChange = { pmName = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pmTitle,
                        onValueChange = { pmTitle = it },
                        label = { Text("Título/Cargo *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = pmEmail,
                        onValueChange = { pmEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            
            // Timeline
            SectionCard(title = "Cronograma") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Fecha Inicio *") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fecha Fin *") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true
                    )
                }
                
                OutlinedTextField(
                    value = estimatedDuration,
                    onValueChange = { estimatedDuration = it },
                    label = { Text("Duración Estimada (días) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            // Spacer para el FAB (sin bottom nav)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

