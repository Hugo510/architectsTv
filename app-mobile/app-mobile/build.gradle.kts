plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    // Agregar plugin de serialización
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.app_mobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app_mobile"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Hilt DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Coil para imágenes
    implementation(libs.coil.compose)
    
    // Material Icons Extended
    implementation(libs.androidx.material.icons.extended)
    
    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // Retrofit para API
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    
    // NUEVAS DEPENDENCIAS AGREGADAS:
    
    // Kotlinx Serialization - CRÍTICA para shared-domain
    implementation(libs.kotlinx.serialization.json)
    
    // Compose Animation - Para las animaciones avanzadas del Kanban/Timeline
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.animation.core)
    
    // Collections Immutable - Para mejor rendimiento con StateFlow
    implementation(libs.kotlinx.collections.immutable)
    
    // Lifecycle State - Para manejar estados del ciclo de vida
    implementation(libs.androidx.lifecycle.runtime.compose)
    
    // Permission handling - Para futuras funcionalidades de evidencia
    implementation(libs.accompanist.permissions)
    
    // Date/Time support
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
    
    // Shared Domain Module
    implementation(project(":shared-domain"))
    
    // Testing mejorado
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine) // Para testing de StateFlow
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    // DEPENDENCIAS FALTANTES IDENTIFICADAS:
    
    // BorderStroke - Para los chips modernos con bordes
    implementation(libs.androidx.compose.foundation)
    
    // Animaciones avanzadas - Para las animaciones fluidas implementadas
    implementation(libs.androidx.compose.animation.graphics)
    
    // UI graphics - Para gradientes y efectos visuales avanzados
    implementation(libs.androidx.compose.ui.graphics)
    
    // Brush y efectos gráficos - Para glassmorphism y gradientes
    implementation(libs.androidx.compose.ui.graphics.android)
    
    // Scale animation - Para las micro-interacciones
    implementation(libs.androidx.compose.animation.core.android)
    
    // Material3 WindowSizeClass - Para responsive design
    implementation(libs.androidx.compose.material3.window.size)
    
    // Activity result APIs - Para funcionalidades futuras de la galería
    implementation(libs.androidx.activity.result)
    
    // Lifecycle viewmodel savedstate - Para persistir estado en rotaciones
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    
    // Datastore preferences - Para configuraciones de usuario
    implementation(libs.androidx.datastore.preferences)
    
    // Splash screen API - Para pantalla de inicio moderna
    implementation(libs.androidx.core.splashscreen)
    
    // System UI controller - Para control de barras de estado
    implementation(libs.accompanist.systemuicontroller)
    
    // Pager support - Para futuras funcionalidades de galería
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    
    // Flow layout - Para layouts dinámicos de chips
    implementation(libs.accompanist.flowlayout)
    
    // Swipe refresh - Para actualización por deslizamiento
    implementation(libs.accompanist.swiperefresh)
    
    // Placeholder - Para estados de carga
    implementation(libs.accompanist.placeholder.material)
    
    // Navigation animation - Para transiciones entre pantallas
    implementation(libs.accompanist.navigation.animation)
    
    // Insets - Para manejo de padding del sistema
    implementation(libs.accompanist.insets)
    implementation(libs.accompanist.insets.ui)
}