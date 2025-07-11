package com.example.architectstv

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.architectstv.ui.theme.ArchitectsTvTheme
import com.example.feature_projection.CastViewModel
import com.example.feature_ui_tv.ui.CastPlaceholderScreen
import com.example.feature_ui_tv.ui.LoadingScreen
import com.example.feature_ui_tv.ui.ManagementScreen
import com.example.feature_ui_tv.ui.CronogramaScreen
import com.example.feature_ui_tv.ui.PlanosScreen
import com.example.feature_ui_tv.ui.EvidenceScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Cambiar temporalmente a TempCastViewModel para testing
    private val castVm: TempCastViewModel by viewModels()
    // private val castVm: CastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArchitectsTvTheme {
                val navController = rememberNavController()
                val startCast by castVm.startCasting.collectAsState(initial = false)
                
                // Estado local para controlar el loading
                var isLoading by remember { mutableStateOf(false) }
                var hasNavigated by remember { mutableStateOf(false) }

                // Debug: Log para verificar estados
                LaunchedEffect(startCast, isLoading, hasNavigated) {
                    Log.d("MainActivity", "startCast: $startCast, isLoading: $isLoading, hasNavigated: $hasNavigated")
                }

                Surface(Modifier.fillMaxSize()) {
                    // Efecto para manejar el inicio del cast
                    LaunchedEffect(startCast) {
                        Log.d("MainActivity", "LaunchedEffect triggered - startCast: $startCast, hasNavigated: $hasNavigated")
                        if (startCast && !hasNavigated) {
                            Log.d("MainActivity", "Setting isLoading to true")
                            isLoading = true
                        }
                    }

                    // Mostrar loading cuando isLoading es true
                    if (isLoading) {
                        Log.d("MainActivity", "Showing LoadingScreen")
                        LoadingScreen {
                            Log.d("MainActivity", "LoadingScreen completed, navigating to management")
                            // Cuando termina el loading
                            isLoading = false
                            hasNavigated = true
                            navController.navigate("management") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    } else {
                        // Reset hasNavigated cuando volvemos al home
                        LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
                            val currentRoute = navController.currentBackStackEntry?.destination?.route
                            Log.d("MainActivity", "Current route: $currentRoute")
                            if (currentRoute == "home") {
                                hasNavigated = false
                            }
                        }

                        // Navegación normal
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            enterTransition = {
                                when (targetState.destination.route) {
                                    "management" -> fadeIn(
                                        animationSpec = tween(800)
                                    ) + slideInHorizontally(
                                        initialOffsetX = { it / 2 },
                                        animationSpec = tween(800)
                                    )
                                    else -> fadeIn(animationSpec = tween(300))
                                }
                            },
                            exitTransition = {
                                fadeOut(animationSpec = tween(300))
                            },
                            popEnterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(300)
                                )
                            },
                            popExitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(300)
                                )
                            }
                        ) {
                            composable("home") {
                                CastPlaceholderScreen(
                                    onCastClick = { 
                                        Log.d("MainActivity", "Cast button clicked, calling triggerCast()")
                                        castVm.triggerCast() 
                                    }
                                )
                            }

                            composable("management") {
                                ManagementScreen(
                                    projectName = "Proyecto 2",
                                    status = "En Proceso",
                                    onNext = { navController.navigate("cronograma") }
                                )
                            }

                            composable("cronograma") {
                                CronogramaScreen(
                                    title = "Proyecto 2",
                                    status = "En Proceso",
                                    onNext = { navController.navigate("planos") },
                                    onBack = { 
                                        if (navController.previousBackStackEntry != null) {
                                            navController.popBackStack()
                                        }
                                    }
                                )
                            }

                            composable("planos") {
                                PlanosScreen(
                                    projectName = "Proyecto 2",
                                    status = "En Proceso",
                                    planUrl = "https://i.redd.it/dyv0kopwbxe31.png",
                                    lastRevision = "15/Junio/2025",
                                    version = "V.02",
                                    planType = "Planta Alta / Estructural",
                                    builtArea = "120.00 m²",
                                    landArea = "180.00 m²",
                                    scale = "1 : 200",
                                    onNext = { navController.navigate("evidence") },
                                    onBack = { 
                                        if (navController.previousBackStackEntry != null) {
                                            navController.popBackStack()
                                        }
                                    }
                                )
                            }

                            composable("evidence") {
                                EvidenceScreen(
                                    projectName = "Proyecto 2",
                                    status = "En Proceso",
                                    onBack = { 
                                        if (navController.previousBackStackEntry != null) {
                                            navController.popBackStack()
                                        } else {
                                            navController.navigate("home") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
                        )
                    }
                }
            }
        }
    }
}
