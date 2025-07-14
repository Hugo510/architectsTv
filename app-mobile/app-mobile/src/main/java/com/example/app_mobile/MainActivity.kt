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
import androidx.compose.ui.unit.dp
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
            composable("home") {
                HomeScreen(
                    repository = managementRepository,
                    onNavigateToProjects = { navController.navigate("management") { launchSingleTop = true } },
                    onNavigateToManagement = { navController.navigate("management") { launchSingleTop = true } },
                    onNavigateToCast = { /* ... */ }
                )
            }
            composable("management") {
                ManagementScreen(
                    viewModel = managementViewModel,
                    onNavigateToHome = { navController.navigate("home") { launchSingleTop = true } },
                    onNavigateToCreateProject = { navController.navigate("create_project") }
                )
            }
            composable("create_project") {
                CreateProjectScreen(
                    viewModel = managementViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveProject = { project ->
                        managementViewModel.createProject(project)
                        navController.popBackStack()
                    }
                )
            }
            composable("cronograma") {
                CronogramaScreen(
                    onNavigateToTaskDetail = { taskId -> navController.navigate("task_detail/$taskId") },
                    onNavigateToCreateTask = { navController.navigate("create_task") }
                )
            }
            composable("task_detail/{taskId}") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskDetailScreen(
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditTask = { /* ... */ }
                )
            }
            composable("create_task") {
                CreateTaskScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("planos") {
                PlanosScreen(
                    onNavigateToHome = { navController.navigate("home") { launchSingleTop = true } }
                )
            }
            composable("evidencia") {
                EvidenciaScreen(
                    viewModel = evidenciaViewModel,
                    onNavigateToProjectDetail = { projectId -> navController.navigate("project_detail/$projectId") { launchSingleTop = true } },
                    onNavigateToAddProject = { navController.navigate("add_gallery_project") { launchSingleTop = true } }
                )
            }
            composable("project_detail/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                GalleryProjectDetailScreen(
                    projectId = projectId,
                    viewModel = evidenciaViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("add_gallery_project") {
                AddGalleryProjectScreen(
                    viewModel = evidenciaViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}