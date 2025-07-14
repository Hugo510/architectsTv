plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
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
    ksp(libs.hilt.compiler)
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
    ksp(libs.androidx.room.compiler)
    
    // Retrofit para API
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    
    // DEPENDENCIAS CRÍTICAS AGREGADAS:
    
    // Kotlinx Serialization - CRÍTICA para shared-domain
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    
    // Compose Animation - Para las animaciones avanzadas implementadas
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.animation.graphics)
    implementation(libs.androidx.compose.animation.core.android)
    
    // UI Graphics avanzados - Para gradientes y efectos implementados
    implementation(libs.androidx.compose.ui.graphics.android)
    implementation(libs.androidx.compose.foundation.layout.staggeredgrid)
    
    // Collections Immutable - Para mejor rendimiento con StateFlow
    implementation(libs.kotlinx.collections.immutable)
    
    // Lifecycle State - Para manejar estados del ciclo de vida
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    
    // Foundation - Para BorderStroke y efectos de las cards
    implementation(libs.androidx.compose.foundation)
    
    // Permission handling - Para futuras funcionalidades de evidencia
    implementation(libs.accompanist.permissions)
    
    // Material3 WindowSizeClass - Para responsive design
    implementation(libs.androidx.compose.material3.window.size)
    
    // Activity result APIs - Para funcionalidades de la galería
    implementation(libs.androidx.activity.result)
    
    // Datastore preferences - Para configuraciones de usuario
    implementation(libs.androidx.datastore.preferences)
    
    // Splash screen API - Para pantalla de inicio moderna
    implementation(libs.androidx.core.splashscreen)
    
    // System UI controller - Para control de barras de estado
    implementation(libs.accompanist.systemuicontroller)
    
    // Pager support - Para funcionalidades de galería
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
    // implementation(libs.accompanist.insets)
    // implementation(libs.accompanist.insets.ui)
    
    // Material3 Adaptive - Para ExposedDropdownMenuBox avanzado
    // implementation(libs.androidx.compose.material3.adaptive)
    
    // Koin DI - Para EvidenciaViewModel como se muestra en el código
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    
    // Koin BOM y core
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    // Date/Time support
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
    
    // Shared Domain Module
    implementation(project(":shared-domain"))
    
    // Testing mejorado
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    // DEPENDENCIAS CRÍTICAS FALTANTES IDENTIFICADAS:

    // Material Icons Extended - CRÍTICA para todos los Icons.Default.* en el código
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material.icons.core)

    //Animation Core Android - Para spring animations en KanbanViewComponents
    implementation(libs.androidx.compose.animation.core.android)

    //Foundation Gestures - Para detectDragGesturesAfterLongPress en Kanban
    implementation(libs.androidx.compose.foundation.gestures)

    //UI Graphics avanzados - Para gradientes y efectos visuales
    implementation(libs.androidx.compose.ui.graphics.shapes)
    implementation(libs.androidx.compose.ui.graphics.vector)

    //UI Platform - Para LocalConfiguration en responsive design
    implementation(libs.androidx.compose.ui.platform)

    //UI Window - Para DialogProperties en modales
    implementation(libs.androidx.compose.ui.window)

    //UI Util - Para configuraciones de pantalla y responsive design
    implementation(libs.androidx.compose.ui.util)

    //Runtime Saveable - Para remember y state management
    implementation(libs.androidx.compose.runtime.saveable)

    //Foundation Text - Para KeyboardActions y focus management
    implementation(libs.androidx.compose.foundation.text)

    //Material3 Adaptive - Para ExposedDropdownMenuBox avanzado
    //implementation(libs.androidx.compose.material3.adaptive)

    //Animation Content - Para AnimatedVisibility en componentes
    implementation(libs.androidx.compose.animation.content)

    //LiveData KTX - Para state management avanzado
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Foundation Lazy - Para LazyColumn/LazyRow optimizations
    implementation(libs.androidx.compose.foundation.lazy)

    //Navigation Animation - Para transiciones suaves
    implementation(libs.androidx.navigation.animation)

    //DEPENDENCIAS PARA FUNCIONALIDADES ESPECÍFICAS DEL CÓDIGO:

    //Layout Staggered Grid - Para layouts avanzados
    implementation(libs.androidx.compose.foundation.layout.staggeredgrid)

    //Accompanist específicos para funcionalidades detectadas
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.accompanist.navigation.animation)
    // implementation(libs.accompanist.insets)
    // implementation(libs.accompanist.insets.ui)

    //DEPENDENCIAS PARA DEBUGGING Y TESTING MEJORADO:
    debugImplementation(libs.androidx.compose.ui.test.manifest.debug)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4.android)
}