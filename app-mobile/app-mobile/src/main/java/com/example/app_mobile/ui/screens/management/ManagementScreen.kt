package com.example.app_mobile.ui.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shared_domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToCreateProject: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Estado") }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }
    
    val statusOptions = listOf("Estado", "Diseño", "Revisión de Permisos", "Construcción", "Entrega")
    
    // Datos mockeados usando el dominio compartido
    val allProjects = remember {
        listOf(
            Project(
                id = "1",
                name = "Proyecto 1",
                description = "Casa residencial moderna",
                status = ProjectStatus.DESIGN,
                location = ProjectLocation(
                    address = "Calle Ejemplo #123",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(2250000.0),
                client = Client(
                    id = "client1",
                    name = "Juan Pérez",
                    email = "juan@example.com"
                ),
                projectManager = ProjectManager(
                    id = "pm1",
                    name = "Arq. Steve",
                    title = "Arquitecto Senior"
                ),
                timeline = ProjectTimeline(
                    startDate = "2024-01-15",
                    endDate = "2024-12-15",
                    estimatedDuration = 300
                ),
                metadata = ProjectMetadata(
                    createdAt = "2024-01-01T00:00:00Z",
                    updatedAt = "2024-01-10T12:00:00Z"
                ),
                progress = 0.15
            ),
            Project(
                id = "2",
                name = "Proyecto 2",
                description = "Edificio comercial",
                status = ProjectStatus.CONSTRUCTION,
                location = ProjectLocation(
                    address = "Av. Principal #456",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(5500000.0),
                client = Client(
                    id = "client2",
                    name = "María González",
                    company = "Empresa ABC"
                ),
                projectManager = ProjectManager(
                    id = "pm2",
                    name = "Arq. Alex",
                    title = "Director de Proyecto"
                ),
                timeline = ProjectTimeline(
                    startDate = "2023-08-01",
                    endDate = "2024-08-01",
                    estimatedDuration = 365
                ),
                metadata = ProjectMetadata(
                    createdAt = "2023-07-15T00:00:00Z",
                    updatedAt = "2024-01-09T10:30:00Z"
                ),
                progress = 0.65
            ),
            Project(
                id = "3",
                name = "Proyecto 3",
                description = "Complejo habitacional",
                status = ProjectStatus.DELIVERY,
                location = ProjectLocation(
                    address = "Blvd. Norte #789",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(8750000.0),
                client = Client(
                    id = "client3",
                    name = "Carlos López",
                    company = "Constructora XYZ"
                ),
                projectManager = ProjectManager(
                    id = "pm3",
                    name = "Arq. Carlos",
                    title = "Gerente de Proyecto"
                ),
                timeline = ProjectTimeline(
                    startDate = "2022-06-01",
                    endDate = "2023-12-01",
                    estimatedDuration = 550
                ),
                metadata = ProjectMetadata(
                    createdAt = "2022-05-15T00:00:00Z",
                    updatedAt = "2023-12-05T16:30:00Z"
                ),
                progress = 0.95
            ),
            Project(
                id = "4",
                name = "Proyecto 4",
                description = "Centro comercial",
                status = ProjectStatus.PERMITS_REVIEW,
                location = ProjectLocation(
                    address = "Zona Industrial #101",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(12000000.0),
                client = Client(
                    id = "client4",
                    name = "Ana Martínez",
                    company = "Inversiones del Norte"
                ),
                projectManager = ProjectManager(
                    id = "pm4",
                    name = "Arq. María",
                    title = "Coordinadora General"
                ),
                timeline = ProjectTimeline(
                    startDate = "2023-10-01",
                    endDate = "2025-03-01",
                    estimatedDuration = 520
                ),
                metadata = ProjectMetadata(
                    createdAt = "2023-09-01T00:00:00Z",
                    updatedAt = "2024-01-05T09:15:00Z"
                ),
                progress = 0.25
            )
        )
    }
    
    // Filtrar proyectos basado en búsqueda y estado
    val filteredProjects = remember(searchQuery, selectedStatus) {
        allProjects.filter { project ->
            val matchesSearch = searchQuery.isEmpty() || 
                project.name.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatus == "Estado" || 
                getStatusDisplayName(project.status) == selectedStatus
            matchesSearch && matchesStatus
        }
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateProject,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Agregar proyecto",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con logo y título
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ManagementHeader()
            }
            
            // Buscador y filtro
            item {
                SearchAndFilterSection(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedStatus = selectedStatus,
                    onStatusChange = { selectedStatus = it },
                    statusOptions = statusOptions,
                    isDropdownExpanded = isStatusDropdownExpanded,
                    onDropdownExpandedChange = { isStatusDropdownExpanded = it }
                )
            }
            
            // Lista de todos los proyectos
            items(filteredProjects) { project ->
                ProjectCard(
                    project = project,
                    onClick = { /* TODO: Navigate to project detail */ }
                )
            }
            
            // Spacer para el FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ManagementHeader() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Logo y nombre empresa
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Logo placeholder
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
        
        // Título principal
        Text(
            text = "Gestión de Obras",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Subtítulo
        Text(
            text = "Supervisa el avance e información de cada obra",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: String,
    onStatusChange: (String) -> Unit,
    statusOptions: List<String>,
    isDropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Buscador
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { 
                Text(
                    "Buscar proyecto",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        
        // Filtro de estado
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = onDropdownExpandedChange
        ) {
            OutlinedTextField(
                value = selectedStatus,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Estado") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .width(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { onDropdownExpandedChange(false) }
            ) {
                statusOptions.forEach { status =>
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            onStatusChange(status)
                            onDropdownExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen del proyecto (placeholder)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏗️",
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del proyecto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre y estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Chip de estado
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (project.status) {
                            ProjectStatus.DESIGN -> Color(0xFF9CA3FF)
                            ProjectStatus.PERMITS_REVIEW -> Color(0xFFFBB6CE)
                            ProjectStatus.CONSTRUCTION -> Color(0xFF68D391)
                            ProjectStatus.DELIVERY -> Color(0xFFA0AEC0)
                        }
                    ) {
                        Text(
                            text = getStatusDisplayName(project.status),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Actividad reciente
                Text(
                    text = project.description ?: "Sin descripción",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "Última actualización por: ${project.projectManager.name}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                // Barra de progreso
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (project.progressPercentage > 0) {
                        LinearProgressIndicator(
                            progress = { project.progress.toFloat() },
                            modifier = Modifier.weight(1f),
                            color = when (project.status) {
                                ProjectStatus.COMPLETED -> Color(0xFF4CAF50)
                                ProjectStatus.IN_PROGRESS -> Color(0xFF2196F3)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "Progreso - ${project.progressPercentage}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Función auxiliar para convertir ProjectStatus a string de UI
private fun getStatusDisplayName(status: ProjectStatus): String {
    return when (status) {
        ProjectStatus.DESIGN -> "Diseño"
        ProjectStatus.PERMITS_REVIEW -> "Revisión de Permisos"
        ProjectStatus.CONSTRUCTION -> "Construcción"
        ProjectStatus.DELIVERY -> "Entrega"
    }
}

