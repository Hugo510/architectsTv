package com.example.app_mobile.ui.screens.planos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanosScreen(
    onNavigateToHome: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Activar animaciones de entrada
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                TopAppBar(
                    title = { 
                        Text(
                            "Planos",
                            fontWeight = FontWeight.Bold
                        ) 
                    }
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    initialScale = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        delayMillis = 600
                    )
                ) + fadeIn(animationSpec = tween(400, delayMillis = 600)),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { /* TODO: Navegar a agregar plano */ },
                    modifier = Modifier.animateContentSize()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar plano")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            delayMillis = 200
                        )
                    ) + fadeIn(animationSpec = tween(600, delayMillis = 200)),
                    exit = scaleOut() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📐",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "Planos Arquitectónicos",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Gestiona y revisa los planos de tus proyectos",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Lista de planos con animación escalonada
            items(4) { index ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            delayMillis = 400 + (index * 100)
                        )
                    ) + fadeIn(animationSpec = tween(500, delayMillis = 400 + (index * 100))),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        onClick = { /* TODO: Navegar a detalle */ }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Plano ${listOf("Planta Baja", "Planta Alta", "Fachada", "Cortes")[index]}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Versión: V.0${index + 1}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Escala: 1:${listOf(100, 200, 150, 75)[index]}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

