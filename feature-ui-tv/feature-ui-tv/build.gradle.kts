plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace   = "com.example.featureuitv"
    compileSdk  = 35

    defaultConfig {
        minSdk    = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // Para usar java.time en Android < API 26:
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        // Asegúrate de usar la versión del BOM
        kotlinCompilerExtensionVersion = libs.versions.composeBom.get()
    }
}

dependencies {
    // 1) BOM de Compose: unifica versiones
    implementation(platform(libs.androidx.compose.bom))

    // 2) Core de Compose UI (ya tenías):
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)

    // 3) Foundation (layouts, focus, BorderStroke…)
    implementation(libs.androidx.compose.foundation)

    // 4) Material3 para TV y Móvil
    implementation(libs.androidx.material3)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.graphics)

    // 5) Navegación
    implementation(libs.androidx.navigation.compose)

    // 6) Coil
    implementation(libs.coil.compose)

    // Hilt Navigation for Compose
    implementation(libs.hilt.navigation.compose)

    coreLibraryDesugaring(libs.android.desugar.jdk.libs)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
