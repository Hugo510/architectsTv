plugins {
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    // Kotlin Coroutines para operaciones asíncronas
    implementation(libs.kotlinx.coroutines.core)
    
    // Serialization para DTOs
    implementation(libs.kotlinx.serialization.json)
    
    // DateTime API
    implementation(libs.kotlinx.datetime)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
