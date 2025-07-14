package com.example.app_mobile.ui.screens.evidencia

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryProjectDetailScreen(
    projectId: String,
    onNavigateBack: () -> Unit
) {
    // Buscar el proyecto por ID (en una implementación real vendría de un ViewModel)
    val project = remember(projectId) {
        // Datos mockeados - en una implementación real vendría de un repositorio
        GalleryProject(
            id = projectId,
            name = "Casa Contemporánea Tropical",
            description = "Una residencia moderna que combina elementos naturales con gran iluminación, diseñada para integrarse perfectamente con el entorno tropical. El proyecto incluye espacios abiertos, uso de materiales sostenibles y tecnología de vanguardia para crear un hogar cómodo y eficiente energéticamente.",
            style = "Contemporáneo",
            location = "Durango, México",
            imageUrl = "https://example.com/house1.jpg",
            rating = 4.8,
            reviewCount = 24,
            completedDate = "2023-12-15",
            architect = "Arq. Steve Johnson",
            area = "320 m²",
            cardHeight = 320
        )
    }
    
    var isFavorite by remember { mutableStateOf(project.isFavorite) }
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    var isFullScreenVisible by remember { mutableStateOf(false) }
    
    // Animación de entrada para toda la pantalla
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Imágenes mockeadas del proyecto
    val projectImages = remember {
        listOf(
            "Fachada Principal" to "🏡",
            "Sala de Estar" to "🛋️", 
            "Cocina Moderna" to "🍽️",
            "Dormitorio Principal" to "🛏️",
            "Baño Principal" to "🛁",
            "Jardín Posterior" to "🌿",
            "Vista Nocturna" to "🌙",
            "Planos Arquitectónicos" to "📐"
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detalle del Proyecto",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de favorito animado
                    var favoriteScale by remember { mutableStateOf(1f) }
                    
                    IconButton(
                        onClick = { 
                            isFavorite = !isFavorite
                            favoriteScale = 1.3f
                        },
                        modifier = Modifier.scale(favoriteScale)
                    ) {
                        LaunchedEffect(favoriteScale) {
                            if (favoriteScale > 1f) {
                                animate(
                                    initialValue = 1.3f,
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ) { value, _ ->
                                    favoriteScale = value
                                }
                            }
                        }
                        
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    IconButton(onClick = { /* TODO: Compartir proyecto */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(400))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Imagen principal del proyecto con animación
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { -it / 2 },
                                animationSpec = tween(800, delayMillis = 200)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
                        ) {
                            ModernProjectMainImage(project = project)
                        }
                    }
                    
                    // Información básica con animación
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(800, delayMillis = 400)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 400))
                        ) {
                            ModernProjectBasicInfo(project = project)
                        }
                    }
                    
                    // Galería de imágenes con animación
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(800, delayMillis = 600)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 600))
                        ) {
                            ModernProjectImageGallery(
                                images = projectImages,
                                onImageClick = { index ->
                                    selectedImageIndex = index
                                    isFullScreenVisible = true
                                }
                            )
                        }
                    }
                    
                    // Especificaciones con animación
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(800, delayMillis = 800)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 800))
                        ) {
                            ModernProjectSpecifications(project = project)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
            
            // Overlay de imagen fullscreen
            if (isFullScreenVisible && selectedImageIndex != null) {
                FullScreenImageViewer(
                    images = projectImages,
                    initialIndex = selectedImageIndex!!,
                    onDismiss = {
                        isFullScreenVisible = false
                        selectedImageIndex = null
                    },
                    onImageChange = { index ->
                        selectedImageIndex = index
                    }
                )
            }
        }
    }
}

@Composable
private fun ModernProjectMainImage(project: GalleryProject) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = when (project.style) {
                            "Contemporáneo" -> listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            "Minimalista" -> listOf(Color(0xFF9FACE6), Color(0x74B9FF))
                            "Industrial" -> listOf(Color(0xFF74B9FF), Color(0xFF0984E3))
                            "Moderno" -> listOf(Color(0xFF81ECEC), Color(0xFF6C5CE7))
                            "Clásico" -> listOf(Color(0xFFFFBF93), Color(0xFFFF7675))
                            "Rústico" -> listOf(Color(0xFFD1A3FF), Color(0xFFA29BFE))
                            else -> listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E))
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Overlay con patrón sutil
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            ),
                            radius = 800f
                        )
                    )
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoji con shadow effect
                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
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
                        fontSize = 64.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Imagen Principal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernProjectBasicInfo(project: GalleryProject) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header con glassmorphism effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                            )
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = project.location,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    
                    // Rating con diseño moderno
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${project.rating}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            Text(
                                text = " (${project.reviewCount})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Descripción con mejor typography
            Text(
                text = project.description,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                letterSpacing = 0.1.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Tags modernos con animación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernInfoChip(
                    label = project.style,
                    icon = "🎨",
                    color = Color(0xFF667EEA)
                )
                ModernInfoChip(
                    label = project.area,
                    icon = "📐",
                    color = Color(0xFF81ECEC)
                )
                ModernInfoChip(
                    label = "Completado",
                    icon = "✅",
                    color = Color(0xFF00B894)
                )
            }
        }
    }
}

@Composable
private fun ModernProjectImageGallery(
    images: List<Pair<String, String>>,
    onImageClick: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Galería de Imágenes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(images.size) { index ->
                    val (title, emoji) = images[index]
                    ModernImageGalleryItem(
                        title = title,
                        emoji = emoji,
                        index = index,
                        onClick = { onImageClick(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernImageGalleryItem(
    title: String,
    emoji: String,
    index: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale_animation"
    )
    
    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .width(140.dp)
            .height(120.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoji con shadow effect
                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }
            
            // Número de imagen
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                ),
                shape = CircleShape
            ) {
                Text(
                    text = "${index + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ModernProjectSpecifications(project: GalleryProject) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Especificaciones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            val specifications = listOf(
                "Área Total" to project.area,
                "Estilo" to project.style,
                "Fecha Completado" to project.completedDate,
                "Habitaciones" to "3",
                "Baños" to "2.5",
                "Pisos" to "2",
                "Estacionamiento" to "2 autos",
                "Jardín" to "Sí"
            )
            
            specifications.chunked(2).forEachIndexed { rowIndex, rowSpecs ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowSpecs.forEachIndexed { itemIndex, (label, value) ->
                        ModernSpecificationItem(
                            label = label,
                            value = value,
                            modifier = Modifier.weight(1f),
                            delay = (rowIndex * 2 + itemIndex) * 100
                        )
                    }
                    
                    if (rowSpecs.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernSpecificationItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    delay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(400)
        ) + fadeIn(animationSpec = tween(400))
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = value,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernInfoChip(
    label: String,
    icon: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = icon,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = color.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun FullScreenImageViewer(
    images: List<Pair<String, String>>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    onImageChange: (Int) -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    
    // Animación de entrada/salida
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300)
            ),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(200)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismiss() }
                        )
                    }
            ) {
                // Imagen principal
                ImageDisplayArea(
                    image = images[currentIndex],
                    modifier = Modifier.align(Alignment.Center),
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
                
                // Header con información de la imagen
                FullScreenHeader(
                    image = images[currentIndex],
                    currentIndex = currentIndex,
                    totalImages = images.size,
                    onClose = onDismiss,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                
                // Controles de navegación
                if (images.size > 1) {
                    NavigationControls(
                        currentIndex = currentIndex,
                        totalImages = images.size,
                        onPrevious = {
                            if (currentIndex > 0) {
                                currentIndex--
                                onImageChange(currentIndex)
                            }
                        },
                        onNext = {
                            if (currentIndex < images.size - 1) {
                                currentIndex++
                                onImageChange(currentIndex)
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                    
                    // Indicadores de página
                    PageIndicators(
                        currentIndex = currentIndex,
                        totalImages = images.size,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
                
                // Información adicional en la parte inferior
                ImageInfoFooter(
                    image = images[currentIndex],
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun ImageDisplayArea(
    image: Pair<String, String>,
    modifier: Modifier = Modifier,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    val (title, emoji) = image
    
    Card(
        modifier = modifier
            .size(
                width = minOf(screenWidth * 0.8f, 400.dp),
                height = minOf(screenHeight * 0.6f, 300.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures { /* Evitar que el tap cierre el dialog */ }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 120.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FullScreenHeader(
    image: Pair<String, String>,
    currentIndex: Int,
    totalImages: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = image.first,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${currentIndex + 1} de $totalImages",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NavigationControls(
    currentIndex: Int,
    totalImages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Botón anterior
        if (currentIndex > 0) {
            FloatingActionButton(
                onClick = onPrevious,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .size(56.dp),
                containerColor = Color.Black.copy(alpha = 0.7f),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Anterior",
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(56.dp + 24.dp))
        }
        
        // Botón siguiente
        if (currentIndex < totalImages - 1) {
            FloatingActionButton(
                onClick = onNext,
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(56.dp),
                containerColor = Color.Black.copy(alpha = 0.7f),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Siguiente",
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(56.dp + 24.dp))
        }
    }
}

@Composable
private fun PageIndicators(
    currentIndex: Int,
    totalImages: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalImages) { index ->
                val isSelected = index == currentIndex
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) 
                                Color.White 
                            else 
                                Color.White.copy(alpha = 0.4f)
                        )
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun ImageInfoFooter(
    image: Pair<String, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Detalles de la imagen",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Imagen de alta resolución disponible",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Toca para cerrar",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

