package com.example.app_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app_mobile.ui.theme.ArchitectsTvTheme
import com.example.app_mobile.ui.components.BottomNavigationBar
import com.example.app_mobile.ui.screens.home.HomeScreen
import com.example.app_mobile.ui.screens.management.ManagementScreen
import com.example.app_mobile.ui.screens.management.CreateProjectScreen
import com.example.app_mobile.ui.screens.cronograma.CronogramaScreen
import com.example.app_mobile.ui.screens.planos.PlanosScreen
import com.example.app_mobile.ui.screens.evidencia.EvidenciaScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_mobile.ui.screens.management.ManagementViewModel
import com.example.app_mobile.ui.screens.evidencia.EvidenciaViewModel
import com.example.app_mobile.ui.screens.evidencia.GalleryProjectDetailScreen
import com.example.app_mobile.ui.screens.evidencia.AddGalleryProjectScreen
import com.example.app_mobile.data.repository.ManagementRepository
import androidx.compose.runtime.remember
import com.example.app_mobile.ui.screens.cronograma.TaskDetailScreen
import com.example.app_mobile.ui.screens.cronograma.CreateTaskScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArchitectsTvTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MobileApp()
                }
            }
        }
    }
}

@Composable
fun MobileApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Repository compartido para integridad de datos
    val managementRepository = remember { ManagementRepository() }
    
    // ViewModels compartidos que usan el mismo repository
    val managementViewModel: ManagementViewModel = viewModel {
        ManagementViewModel(managementRepository)
    }
    val evidenciaViewModel: EvidenciaViewModel = viewModel()
    
    // Lista de rutas principales que muestran el bottom navigation
    val bottomNavRoutes = listOf("home", "management", "cronograma", "planos", "evidencia")
    val showBottomNav = currentRoute in bottomNavRoutes
    
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(
                bottom = if (showBottomNav) paddingValues.calculateBottomPadding() else 0.dp
            )
        ) {
            composable(
                "home",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                AnimatedContent(
                    targetState = "home",
                    transitionSpec = {
                        scaleIn(
                            initialScale = 0.9f,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) togetherWith scaleOut(
                            targetScale = 1.1f,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
                    },
                    label = "home_animation"
                ) {
                    HomeScreen(
                        repository = managementRepository, // Inyectar repository compartido
                        onNavigateToProjects = { 
                            navController.navigate("management") {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToManagement = { 
                            navController.navigate("management") {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToCast = { /* TODO: Implementar cast */ }
                    )
                }
            }
            
            composable(
                "management",
                enterTransition = {
                    when (initialState.destination.route) {
                        "home" -> slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        "cronograma" -> slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        else -> slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                    } + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        "home" -> slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        "cronograma" -> slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        else -> slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                    } + fadeOut(animationSpec = tween(400))
                }
            ) {
                AnimatedContent(
                    targetState = "management",
                    transitionSpec = {
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeIn() togetherWith slideOutVertically(
                            targetOffsetY = { -it / 3 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeOut()
                    },
                    label = "management_animation"
                ) {
                    ManagementScreen(
                        viewModel = managementViewModel,
                        onNavigateToHome = { 
                            navController.navigate("home") {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToCreateProject = { 
                            navController.navigate("create_project") 
                        }
                    )
                }
            }
            
            composable(
                "create_project",
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                CreateProjectScreen(
                    viewModel = managementViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveProject = { project ->
                        managementViewModel.createProject(project)
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                "cronograma",
                enterTransition = {
                    when (initialState.destination.route) {
                        "management" -> slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        "planos" -> slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        else -> slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                    } + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        "management" -> slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        "planos" -> slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        else -> slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                    } + fadeOut(animationSpec = tween(400))
                }
            ) {
                AnimatedContent(
                    targetState = "cronograma",
                    transitionSpec = {
                        scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeIn() togetherWith scaleOut(
                            targetScale = 1.2f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeOut()
                    },
                    label = "cronograma_animation"
                ) {
                    CronogramaScreen(
                        onNavigateToTaskDetail = { taskId ->
                            navController.navigate("task_detail/$taskId")
                        },
                        onNavigateToCreateTask = {
                            navController.navigate("create_task")
                        }
                    )
                }
            }
            // Pantalla de detalle de tarea
            composable(
                "task_detail/{taskId}",
                enterTransition = {
                    // TODO: Puedes personalizar la animación si lo deseas
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskDetailScreen(
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditTask = { /* TODO: Implementar edición de tarea si es necesario */ }
                )
            }
            // Pantalla de crear tarea
            composable(
                "create_task",
                enterTransition = {
                    // TODO: Puedes personalizar la animación si lo deseas
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                CreateTaskScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "planos",
                enterTransition = {
                    when (initialState.destination.route) {
                        "cronograma" -> slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        "evidencia" -> slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        else -> slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                    } + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        "cronograma" -> slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        "evidencia" -> slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                        else -> slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                    } + fadeOut(animationSpec = tween(400))
                }
            ) {
                AnimatedContent(
                    targetState = "planos",
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        ) + fadeIn() togetherWith slideOutHorizontally(
                            targetOffsetX = { -it / 2 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        ) + fadeOut()
                    },
                    label = "planos_animation"
                ) {
                    PlanosScreen(
                        onNavigateToHome = { 
                            navController.navigate("home") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
            
            composable(
                "evidencia",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                AnimatedContent(
                    targetState = "evidencia",
                    transitionSpec = {
                        slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn() togetherWith slideOutVertically(
                            targetOffsetY = { -it / 4 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeOut()
                    },
                    label = "evidencia_animation"
                ) {
                    EvidenciaScreen(
                        viewModel = evidenciaViewModel,
                        onNavigateToProjectDetail = { projectId ->
                            navController.navigate("project_detail/$projectId") {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToAddProject = {
                            navController.navigate("add_gallery_project") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
            
            composable(
                "project_detail/{projectId}",
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                GalleryProjectDetailScreen(
                    projectId = projectId,
                    viewModel = evidenciaViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "add_gallery_project",
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                AddGalleryProjectScreen(
                    viewModel = evidenciaViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}