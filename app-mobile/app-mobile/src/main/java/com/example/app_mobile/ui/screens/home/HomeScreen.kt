package com.example.app_mobile.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun HomeScreen(
    onNavigateToProjects: () -> Unit,
    onNavigateToManagement: () -> Unit,
    onNavigateToCast: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }
    
    val statusOptions = listOf("Todos", "Diseño", "Revisión de Permisos", "Construcción", "Entrega")
    
    // Datos mockeados usando el dominio compartido
    val recentProjects = remember {
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
            )
        )
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCast,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Cast, contentDescription = "Transmitir a TV")
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
            // Header con logo y bienvenida
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CompanyHeader()
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
            
            // Sección de últimas actualizaciones
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Últimas Actualizaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToProjects) {
                        Text(
                            text = "Ver todos",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Lista de proyectos recientes
            items(recentProjects) { project ->
                ProjectCard(
                    project = project,
                    onClick = { /* Navigate to project detail */ }
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
private fun CompanyHeader() {
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
        
        // Bienvenida
        Text(
            text = "Bienvenido, Arq. Uriel",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Subtítulo
        Text(
            text = "[Poner de subtítulo o frase aquí]",
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
                            text = when (project.status) {
                                ProjectStatus.DESIGN -> "Diseño"
                                ProjectStatus.PERMITS_REVIEW -> "Revisión de Permisos"
                                ProjectStatus.CONSTRUCTION -> "Construcción"
                                ProjectStatus.DELIVERY -> "Entrega"
                            },
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
