package com.example.app_mobile.ui.screens.evidencia

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared_domain.model.GalleryProject
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenciaScreen(
    onNavigateToProjectDetail: (String) -> Unit = {},
    onNavigateToAddProject: () -> Unit = {}, // Nueva función de navegación
    viewModel: EvidenciaViewModel = getViewModel() // Usar Koin injection
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val filteredProjects by viewModel.filteredProjects.collectAsState()
    
    var isStyleDropdownExpanded by remember { mutableStateOf(false) }
    
    val styleOptions = listOf(
        "Estilos", "Contemporáneo", "Minimalista", "Industrial", 
        "Clásico", "Moderno", "Rústico", "Colonial"
    )
    
    // Mostrar mensajes y errores
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            // Implementar SnackbarHost si es necesario
            viewModel.clearMessage()
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Implementar SnackbarHost si es necesario
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Galería",
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProject, // Conectar navegación
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Agregar a galería",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header con logo y título estilo Airbnb
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    GalleryHeader()
                }
                
                // Buscador y filtro de estilos
                item {
                    SearchAndStyleFilter(
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        selectedStyle = selectedStyle,
                        onStyleChange = viewModel::updateSelectedStyle,
                        styleOptions = styleOptions,
                        isDropdownExpanded = isStyleDropdownExpanded,
                        onDropdownExpandedChange = { isStyleDropdownExpanded = it }
                    )
                }
                
                // Chips de estilos populares
                item {
                    PopularStylesSection(
                        selectedStyle = selectedStyle,
                        onStyleSelected = viewModel::updateSelectedStyle
                    )
                }
                
                // Contador de resultados
                item {
                    Text(
                        text = "${filteredProjects.size} obras ${if (filteredProjects.size == 1) "encontrada" else "encontradas"}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // Grid staggered de proyectos estilo Pinterest/Instagram
                item {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 16.dp,
                        modifier = Modifier.height(800.dp)
                    ) {
                        items(filteredProjects) { project =>
                            ModernGalleryProjectCard(
                                project = project,
                                onClick = { onNavigateToProjectDetail(project.id) },
                                onToggleFavorite = { viewModel.toggleProjectFavorite(project.id) } // Nueva funcionalidad
                            )
                        }
                    }
                }
                
                // Spacer para el FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // Overlay de carga
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun GalleryHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Logo y nombre empresa modernizado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Card(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "L",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "LOGO EMPRESA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Arquitectura & Construcción",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Título principal con efecto gradiente
                Text(
                    text = "Galería",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.5.sp
                )
                
                // Subtítulo mejorado
                Text(
                    text = "Explora nuestra colección de proyectos exitosamente completados",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                    lineHeight = 22.sp,
                    letterSpacing = 0.1.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndStyleFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStyle: String,
    onStyleChange: (String) -> Unit,
    styleOptions: List<String>,
    isDropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Buscador modernizado
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { 
                    Text(
                        "Buscar proyectos...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ) 
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )
            
            // Filtro modernizado
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = onDropdownExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedStyle,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = isDropdownExpanded,
                            modifier = Modifier.scale(1.2f)
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .width(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
                
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { onDropdownExpandedChange(false) }
                ) {
                    styleOptions.forEach { style =>
                        DropdownMenuItem(
                            text = { Text(style) },
                            onClick = {
                                onStyleChange(style)
                                onDropdownExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularStylesSection(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit
) {
    val popularStyles = listOf("Contemporáneo", "Minimalista", "Moderno", "Industrial")
    
    Column {
        Text(
            text = "Estilos Populares",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
            letterSpacing = 0.5.sp
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(popularStyles) { style =>
                var isPressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "chip_scale"
                )
                
                FilterChip(
                    onClick = { 
                        isPressed = true
                        onStyleSelected(if (selectedStyle == style) "Estilos" else style)
                    },
                    label = { 
                        Text(
                            style,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp
                        ) 
                    },
                    selected = selectedStyle == style,
                    modifier = Modifier.scale(scale),
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedStyle == style,
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        kotlinx.coroutines.delay(100)
                        isPressed = false
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernGalleryProjectCard(
    project: GalleryProject,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(project.isFavorite) }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(project.cardHeight.dp), // Altura variable para efecto staggered
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp) // Bordes más redondeados para look moderno
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen del proyecto con overlay de favorito
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Placeholder con icono profesional
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(
                            when (project.style) {
                                "Contemporáneo" -> Color(0xFF667EEA)
                                "Minimalista" -> Color(0xFF9FACE6)
                                "Industrial" -> Color(0xFF74B9FF)
                                "Moderno" -> Color(0xFF81ECEC)
                                "Clásico" -> Color(0xFFFFBF93)
                                "Rústico" -> Color(0xFFD1A3FF)
                                else -> Color(0xFFBDBDBD)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = when (project.style) {
                                "Contemporáneo" -> Icons.Default.Home
                                "Minimalista" -> Icons.Default.Business
                                "Industrial" -> Icons.Default.Factory
                                "Moderno" -> Icons.Default.Apartment
                                "Clásico" -> Icons.Default.AccountBalance
                                "Rústico" -> Icons.Default.Cottage
                                else -> Icons.Default.Construction
                            },
                            contentDescription = project.style,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(if (project.cardHeight > 300) 56.dp else 40.dp)
                        )
                        
                        // Texto del estilo para cards grandes
                        if (project.cardHeight > 300) {
                            Text(
                                text = project.style,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                
                // Overlay con gradiente sutil para cards grandes
                if (project.cardHeight > 300) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )
                }
                
                // Botón de favorito estilo moderno
                IconButton(
                    onClick = onToggleFavorite, // Usar función del ViewModel
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (project.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (project.isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Badge de rating para cards grandes
                if (project.cardHeight > 300) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${project.rating}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
            
            // Información del proyecto con espaciado dinámico
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Nombre del proyecto
                Text(
                    text = project.name,
                    fontSize = if (project.cardHeight > 300) 16.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (project.cardHeight > 300) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = if (project.cardHeight > 300) 20.sp else 16.sp
                )
                
                // Descripción solo para cards grandes
                if (project.cardHeight > 300 && project.description.length > 30) {
                    Text(
                        text = project.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
                
                // Ubicación
                Text(
                    text = project.location,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = if (project.cardHeight > 300) 6.dp else 4.dp)
                )
                
                // Rating y área para cards pequeñas (no mostradas en overlay)
                if (project.cardHeight <= 300) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${project.rating}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                        
                        Text(
                            text = project.area,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Arquitecto para todas las cards
                Text(
                    text = "Por ${project.architect}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Área para cards grandes (mostrada prominentemente)
                if (project.cardHeight > 300) {
                    Card(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = project.area,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}