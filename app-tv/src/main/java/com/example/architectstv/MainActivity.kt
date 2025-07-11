package com.example.architectstv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

                // 1) Observamos el flujo que dispara la proyección
                val startCast by castVm.startCasting.collectAsState(initial = false)

                Surface(Modifier.fillMaxSize()) {
                    // 2) Cuando el móvil dispara, mostramos loader; al completar, navegamos a gestión
                    if (startCast) {
                        LoadingScreen {
                            navController.navigate("management") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    } else {
                        // 3) Mientras no disparen, placeholder con botón de cast
                        CastPlaceholderScreen(onCastClick = { castVm.triggerCast() })
                    }
                }

                // 4) Definimos nuestras rutas
                NavHost(
                    navController    = navController,
                    startDestination = "home"
                ) {
                    // Ruta inicial: placeholder (sin proyección)
                    composable("home") {
                        CastPlaceholderScreen(onCastClick = { castVm.triggerCast() })
                    }

                    // 1) Gestión de obras (página 1)
                    composable("management") {
                        ManagementScreen(
                            projectName = "Proyecto 2",
                            status      = "En Proceso",
                            onNext      = { navController.navigate("cronograma") }
                        )
                    }

                    // 2) Cronograma (página 2)
                    composable("cronograma") {
                        CronogramaScreen(
                            title  = "Proyecto 2",
                            status = "En Proceso",
                            onNext = { navController.navigate("planos") },
                            onBack = { navController.popBackStack() }
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
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 4) Evidencia (página 4)
                    composable("evidence") {
                        EvidenceScreen(
                            projectName = "Proyecto 2",
                            status      = "En Proceso",
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
