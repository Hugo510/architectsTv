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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    onNavigateToHome: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Estado") }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }
    
    val statusOptions = listOf("Estado", "Activo", "En Proceso", "Completado", "En Pausa")
    
    // Datos mockeados de todos los proyectos
    val allProjects = remember {
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
            ),
            ProjectItem(
                id = "3",
                name = "Proyecto 3",
                status = "Completado",
                progress = 100,
                imageUrl = "https://static.planetminecraft.com/files/image/minecraft/project/2023/389/17216741_xl.webp",
                lastActivity = "Completado hace 1 semana por: Arq. Carlos"
            ),
            ProjectItem(
                id = "4",
                name = "Proyecto 4",
                status = "En Pausa",
                progress = 25,
                imageUrl = "https://cmpcmaderas.com/assets/uploads/2024/05/minecraft-portada.jpg",
                lastActivity = "Pausado hace 3 días por: Arq. María"
            )
        )
    }
    
    // Filtrar proyectos basado en búsqueda y estado
    val filteredProjects = remember(searchQuery, selectedStatus) {
        allProjects.filter { project =>
            val matchesSearch = searchQuery.isEmpty() || 
                project.name.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatus == "Estado" || 
                project.status == selectedStatus
            matchesSearch && matchesStatus
        }
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navegar a crear proyecto */ },
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