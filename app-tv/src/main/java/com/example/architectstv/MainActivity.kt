package com.example.architectstv

import android.os.Bundle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    private val castVm: CastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArchitectsTvTheme {
                val navController = rememberNavController()
                val startCast by castVm.startCasting.collectAsState(initial = false)
                
                // Estados para controlar la transición
                val hasNavigated = remember { mutableStateOf(false) }
                val showLoading = remember { mutableStateOf(false) }

                Surface(Modifier.fillMaxSize()) {
                    // Manejo del estado de carga
                    LaunchedEffect(startCast) {
                        if (startCast && !hasNavigated.value) {
                            showLoading.value = true
                        }
                    }

                    // Reset cuando vuelve al home
                    LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
                        if (navController.currentBackStackEntry?.destination?.route == "home") {
                            hasNavigated.value = false
                            showLoading.value = false
                        }
                    }

                    // Mostrar loading screen cuando se activa el cast
                    if (showLoading.value && !hasNavigated.value) {
                        LoadingScreen {
                            // Cuando termina el loading, navega con transición
                            showLoading.value = false
                            hasNavigated.value = true
                            navController.navigate("management") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    // Animaciones de transición entre pantallas
                    enterTransition = {
                        when (targetState.destination.route) {
                            "management" -> fadeIn(
                                animationSpec = tween(600)
                            ) + slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(600)
                            )
                            else -> fadeIn(animationSpec = tween(300))
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "management" -> fadeOut(
                                animationSpec = tween(300)
                            )
                            else -> slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(300)
                            )
                        }
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
                        CastPlaceholderScreen(onCastClick = { castVm.triggerCast() })
                    }

                    composable(
                        "management",
                        enterTransition = {
                            fadeIn(
                                animationSpec = tween(800)
                            ) + slideInHorizontally(
                                initialOffsetX = { it / 2 },
                                animationSpec = tween(800)
                            )
                        }
                    ) {
                        ManagementScreen(
                            projectName = "Proyecto 2",
                            status = "En Proceso",
                            onNext = { navController.navigate("cronograma") }
                        )
                    }

                    // 2) Cronograma (página 2)
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

                    // 3) Planos (página 3)
                    composable("planos") {
                        PlanosScreen(
                            projectName  = "Proyecto 2",
                            status       = "En Proceso",
                            planUrl      = "https://i.redd.it/dyv0kopwbxe31.png",
                            lastRevision = "15/Junio/2025",
                            version      = "V.02",
                            planType     = "Planta Alta / Estructural",
                            builtArea    = "120.00 m²",
                            landArea     = "180.00 m²",
                            scale        = "1 : 200",
                            onNext = { navController.navigate("evidence") },
                            onBack = { 
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    // 4) Evidencia (página 4)
                    composable("evidence") {
                        EvidenceScreen(
                            projectName = "Proyecto 2",
                            status      = "En Proceso",
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
