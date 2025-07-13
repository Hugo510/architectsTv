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
    
    val statusOptions = listOf("Todos", "Activo", "En Proceso", "Completado", "En Pausa")
    
    // Datos mockeados de proyectos
    val recentProjects = remember {
        listOf(
            ProjectItem(
                id = "1",
                name = "Proyecto 1",
                status = "Activo",
                progress = 0,
                imageUrl = "https://www.xtrafondos.com/wallpapers/construccion-en-minecraft-12384.jpg",
                lastActivity = "Última actualización hace 1 día por: Arq. Steve"
            ),
            ProjectItem(
                id = "2", 
                name = "Proyecto 2",
                status = "En Proceso",
                progress = 50,
                imageUrl = "https://minecraftfullhd.weebly.com/uploads/5/2/9/9/52994245/3180102_orig.jpg",
                lastActivity = "Última actualización hace 2hrs por: Arq. Alex"
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
    project: ProjectItem,
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
            // Imagen del proyecto
            AsyncImage(
                model = project.imageUrl,
                contentDescription = "Imagen de ${project.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
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
                            "Activo" -> Color(0xFF4CAF50)
                            "En Proceso" -> Color(0xFFFF9800)
                            "Completado" -> Color(0xFF2196F3)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = project.status,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Actividad reciente
                Text(
                    text = "Nombre Actividad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = project.lastActivity,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                // Barra de progreso
                if (project.progress > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { project.progress / 100f },
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Progreso - ${project.progress}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "Progreso - ${project.progress}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

// Data class para los proyectos
data class ProjectItem(
    val id: String,
    val name: String,
    val status: String,
    val progress: Int,
    val imageUrl: String,
    val lastActivity: String
)
