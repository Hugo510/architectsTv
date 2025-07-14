package com.example.app_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    
    // Lista de rutas principales que muestran el bottom navigation
    val bottomNavRoutes = listOf("home", "management", "cronograma", "planos", "evidencia")
    val showBottomNav = currentRoute in bottomNavRoutes
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home", // Cambiar inicio a home
            modifier = Modifier.padding(
                bottom = if (showBottomNav) paddingValues.calculateBottomPadding() else 0.dp
            )
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToProjects = { navController.navigate("management") },
                    onNavigateToManagement = { navController.navigate("management") },
                    onNavigateToCast = { /* TODO: Implementar cast */ }
                )
            }
            
            composable("management") {
                ManagementScreen(
                    onNavigateToHome = { navController.navigate("home") },
                    onNavigateToCreateProject = { navController.navigate("create_project") }
                )
            }
            
            composable("create_project") {
                CreateProjectScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveProject = { project ->
                        // TODO: Guardar proyecto en repositorio
                        navController.popBackStack()
                    }
                )
            }
            
            composable("cronograma") {
                CronogramaScreen(
                    onNavigateToTaskDetail = { taskId ->
                        // TODO: Navegar a detalle de tarea
                    },
                    onNavigateToCreateTask = {
                        // TODO: Navegar a crear tarea
                    },
                    onNavigateToMilestoneDetail = { milestoneId ->
                        // TODO: Navegar a detalle de hito
                    },
                    onNavigateToScheduleSettings = {
                        // TODO: Navegar a configuración de cronograma
                    }
                )
            }
            
            composable("planos") {
                PlanosScreen(
                    onNavigateToHome = { navController.navigate("home") }
                )
            }
            
            composable("evidencia") {
                EvidenciaScreen(
                    onNavigateToProjectDetail = { projectId ->
                        // TODO: Navegar a detalle de proyecto
                    },
                    onNavigateToAddProject = {
                        // TODO: Navegar a agregar proyecto a galería
                    }
                )
            }
        }
    }
}