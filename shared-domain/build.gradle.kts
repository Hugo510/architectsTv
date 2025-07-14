plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "2.2.0"
}

android {
    namespace = "com.example.shared_domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
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
}

dependencies {
    // Kotlin Coroutines para operaciones asíncronas
    implementation(libs.kotlinx.coroutines.core)
    
    // Serialization para DTOs
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    
    // DateTime API
    implementation(libs.kotlinx.datetime)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Desugaring para soporte de nuevas APIs de Java en Android
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)

    testImplementation(libs.kotlinx.coroutines.test)

}
