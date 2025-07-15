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
import com.example.shared_domain.repository.GalleryProject
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenciaScreen(
    viewModel: EvidenciaViewModel = hiltViewModel(), // Inyección automática con Hilt/KSP
    onNavigateToProjectDetail: (String) -> Unit = {},
    onNavigateToAddProject: () -> Unit = {}, // Nueva función de navegación
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
                        // Cambia el tipo de items a Any para evitar el error de tipo
                        items(filteredProjects as List<Any>) { project ->
                            ModernGalleryProjectCard(
                                project = project,
                                onClick = { /* Navegación deshabilitada */ },
                                onToggleFavorite = { /* Favorito deshabilitado */ }
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
                    styleOptions.forEach { style ->
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
            items(popularStyles) { style ->
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
    project: Any,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen/placeholder simple
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Gray)
            ) {
                // Aquí se eliminan overlays y badges que dependían de datos no implementados
            }
            // Información básica del proyecto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Proyecto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

