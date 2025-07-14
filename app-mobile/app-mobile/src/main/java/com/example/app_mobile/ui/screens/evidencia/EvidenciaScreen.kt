package com.example.app_mobile.ui.screens.evidencia

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared_domain.model.*
import kotlin.random.Random

data class GalleryProject(
    val id: String,
    val name: String,
    val description: String,
    val style: String,
    val location: String,
    val imageUrl: String,
    val rating: Double,
    val reviewCount: Int,
    val completedDate: String,
    val architect: String,
    val area: String,
    val isFavorite: Boolean = false,
    val cardHeight: Int = 280 // Nueva propiedad para altura variable
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenciaScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProjectDetail: (String) -> Unit = {} // Nuevo parámetro
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Estilos") }
    var isStyleDropdownExpanded by remember { mutableStateOf(false) }
    
    val styleOptions = listOf(
        "Estilos", "Contemporáneo", "Minimalista", "Industrial", 
        "Clásico", "Moderno", "Rústico", "Colonial"
    )
    
    // Datos mockeados con alturas variables para efecto staggered
    val galleryProjects = remember {
        listOf(
            GalleryProject(
                id = "1",
                name = "Casa Contemporánea Tropical",
                description = "Residencia moderna con elementos naturales y gran iluminación que se integra perfectamente con el entorno tropical",
                style = "Contemporáneo",
                location = "Durango, México",
                imageUrl = "https://example.com/house1.jpg",
                rating = 4.8,
                reviewCount = 24,
                completedDate = "2023-12-15",
                architect = "Arq. Steve Johnson",
                area = "320 m²",
                cardHeight = 320
            ),
            GalleryProject(
                id = "2",
                name = "Villa Minimalista",
                description = "Diseño limpio y funcional",
                style = "Minimalista",
                location = "Durango, México",
                imageUrl = "https://example.com/house2.jpg",
                rating = 4.9,
                reviewCount = 18,
                completedDate = "2023-11-20",
                architect = "Arq. María López",
                area = "280 m²",
                cardHeight = 260
            ),
            GalleryProject(
                id = "3",
                name = "Casa Social Moderna",
                description = "Vivienda accesible con diseño contemporáneo que maximiza espacios y funcionalidad",
                style = "Moderno",
                location = "Durango, México",
                imageUrl = "https://example.com/house3.jpg",
                rating = 4.7,
                reviewCount = 32,
                completedDate = "2023-10-30",
                architect = "Arq. Carlos Mendoza",
                area = "180 m²",
                cardHeight = 300
            ),
            GalleryProject(
                id = "4",
                name = "Loft Industrial",
                description = "Espacios amplios con elementos de acero",
                style = "Industrial",
                location = "Durango, México",
                imageUrl = "https://example.com/house4.jpg",
                rating = 4.6,
                reviewCount = 15,
                completedDate = "2023-09-12",
                architect = "Arq. Ana Ruiz",
                area = "450 m²",
                cardHeight = 240
            ),
            GalleryProject(
                id = "5",
                name = "Residencia Clásica Elegante",
                description = "Elegancia tradicional con toques modernos que respeta la arquitectura histórica mientras incorpora comodidades contemporáneas",
                style = "Clásico",
                location = "Durango, México",
                imageUrl = "https://example.com/house5.jpg",
                rating = 4.8,
                reviewCount = 28,
                completedDate = "2023-08-25",
                architect = "Arq. Roberto Silva",
                area = "380 m²",
                cardHeight = 340
            ),
            GalleryProject(
                id = "6",
                name = "Cabaña Rústica",
                description = "Lo natural y lo contemporáneo",
                style = "Rústico",
                location = "Durango, México",
                imageUrl = "https://example.com/house6.jpg",
                rating = 4.9,
                reviewCount = 22,
                completedDate = "2023-07-18",
                architect = "Arq. Laura García",
                area = "250 m²",
                cardHeight = 220
            ),
            GalleryProject(
                id = "7",
                name = "Penthouse Moderno",
                description = "Lujo y modernidad en las alturas con vistas panorámicas espectaculares",
                style = "Moderno",
                location = "Durango, México",
                imageUrl = "https://example.com/house7.jpg",
                rating = 4.9,
                reviewCount = 35,
                completedDate = "2023-06-10",
                architect = "Arq. Diego Ramírez",
                area = "450 m²",
                cardHeight = 360
            ),
            GalleryProject(
                id = "8",
                name = "Casa Ecológica",
                description = "Sostenibilidad y diseño",
                style = "Contemporáneo",
                location = "Durango, México",
                imageUrl = "https://example.com/house8.jpg",
                rating = 4.7,
                reviewCount = 19,
                completedDate = "2023-05-22",
                architect = "Arq. Elena Vásquez",
                area = "290 m²",
                cardHeight = 280
            )
        )
    }
    
    // Filtrar proyectos basado en búsqueda y estilo
    val filteredProjects = remember(searchQuery, selectedStyle) {
        galleryProjects.filter { project =>
            val matchesSearch = searchQuery.isEmpty() || 
                project.name.contains(searchQuery, ignoreCase = true) ||
                project.description.contains(searchQuery, ignoreCase = true)
            val matchesStyle = selectedStyle == "Estilos" || 
                project.style == selectedStyle
            matchesSearch && matchesStyle
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
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Inicio")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navegar a agregar proyecto a galería */ },
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
                    onSearchQueryChange = { searchQuery = it },
                    selectedStyle = selectedStyle,
                    onStyleChange = { selectedStyle = it },
                    styleOptions = styleOptions,
                    isDropdownExpanded = isStyleDropdownExpanded,
                    onDropdownExpandedChange = { isStyleDropdownExpanded = it }
                )
            }
            
            // Chips de estilos populares
            item {
                PopularStylesSection(
                    selectedStyle = selectedStyle,
                    onStyleSelected = { selectedStyle = it }
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
                            onClick = { onNavigateToProjectDetail(project.id) } // Navegar a detalle
                        )
                    }
                }
            }
            
            // Spacer para el FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
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
    onClick: () -> Unit
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
                    .weight(1f) // Toma el espacio restante dinámicamente
            ) {
                // Placeholder de imagen con gradientes más modernos
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
                        Text(
                            text = when (project.style) {
                                "Contemporáneo" -> "🏡"
                                "Minimalista" -> "🏢"
                                "Industrial" -> "🏭"
                                "Moderno" -> "🏠"
                                "Clásico" -> "🏛️"
                                "Rústico" -> "🏘️"
                                else -> "🏗️"
                            },
                            fontSize = if (project.cardHeight > 300) 56.sp else 40.sp
                        )
                        
                        // Añadir texto estilo para cards más grandes
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
                    onClick = { isFavorite = !isFavorite },
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
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else Color.Gray,
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
