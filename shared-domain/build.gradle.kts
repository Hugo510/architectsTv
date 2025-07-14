plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version "2.2.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
