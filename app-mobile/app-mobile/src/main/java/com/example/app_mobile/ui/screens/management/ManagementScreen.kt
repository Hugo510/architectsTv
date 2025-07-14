package com.example.app_mobile.ui.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DeliveryDining
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    viewModel: ManagementViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToCreateProject: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val statusOptions = listOf("Estado", "Diseño", "Revisión de Permisos", "Construcción", "Entrega")
    
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
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    selectedStatus = uiState.selectedStatus,
                    onStatusChange = viewModel::updateSelectedStatus,
                    statusOptions = statusOptions,
                    isDropdownExpanded = false, // Mantener estado local para dropdown
                    onDropdownExpandedChange = { }
                )
            }
            
            // Lista de todos los proyectos
            items(uiState.filteredProjects) { project ->
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
            // Icono profesional del proyecto
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (project.status) {
                            ProjectStatus.DESIGN -> Color(0xFF9CA3FF).copy(alpha = 0.2f)
                            ProjectStatus.PERMITS_REVIEW -> Color(0xFFFBB6CE).copy(alpha = 0.2f)
                            ProjectStatus.CONSTRUCTION -> Color(0xFF68D391).copy(alpha = 0.2f)
                            ProjectStatus.DELIVERY -> Color(0xFFA0AEC0).copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (project.status) {
                        ProjectStatus.DESIGN -> Icons.Default.Architecture
                        ProjectStatus.PERMITS_REVIEW -> Icons.Default.Assignment
                        ProjectStatus.CONSTRUCTION -> Icons.Default.Construction
                        ProjectStatus.DELIVERY -> Icons.Default.DeliveryDining
                    },
                    contentDescription = "Proyecto",
                    tint = when (project.status) {
                        ProjectStatus.DESIGN -> Color(0xFF9CA3FF)
                        ProjectStatus.PERMITS_REVIEW -> Color(0xFFFBB6CE)
                        ProjectStatus.CONSTRUCTION -> Color(0xFF68D391)
                        ProjectStatus.DELIVERY -> Color(0xFFA0AEC0)
                    },
                    modifier = Modifier.size(32.dp)
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
                                ProjectStatus.DELIVERY -> Color(0xFF4CAF50)
                                ProjectStatus.CONSTRUCTION -> Color(0xFF2196F3)
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
            // Icono profesional del proyecto
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (project.status) {
                            ProjectStatus.DESIGN -> Color(0xFF9CA3FF).copy(alpha = 0.2f)
                            ProjectStatus.PERMITS_REVIEW -> Color(0xFFFBB6CE).copy(alpha = 0.2f)
                            ProjectStatus.CONSTRUCTION -> Color(0xFF68D391).copy(alpha = 0.2f)
                            ProjectStatus.DELIVERY -> Color(0xFFA0AEC0).copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (project.status) {
                        ProjectStatus.DESIGN -> Icons.Default.Architecture
                        ProjectStatus.PERMITS_REVIEW -> Icons.Default.Assignment
                        ProjectStatus.CONSTRUCTION -> Icons.Default.Construction
                        ProjectStatus.DELIVERY -> Icons.Default.DeliveryDining
                    },
                    contentDescription = "Proyecto",
                    tint = when (project.status) {
                        ProjectStatus.DESIGN -> Color(0xFF9CA3FF)
                        ProjectStatus.PERMITS_REVIEW -> Color(0xFFFBB6CE)
                        ProjectStatus.CONSTRUCTION -> Color(0xFF68D391)
                        ProjectStatus.DELIVERY -> Color(0xFFA0AEC0)
                    },
                    modifier = Modifier.size(32.dp)
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
                                ProjectStatus.DELIVERY -> Color(0xFF4CAF50)
                                ProjectStatus.CONSTRUCTION -> Color(0xFF2196F3)
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

