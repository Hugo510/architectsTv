pluginManagement {
    repositories {
        google() // ¡Asegúrate de que esté aquí!
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // ¡Asegúrate de que esté aquí también!
        mavenCentral()
    }
}

rootProject.name = "architectsTv"

// Incluimos todos los módulos
include(":feature-projection:feature-projection")
include(":feature-ui-tv:feature-ui-tv")
include(":feature-domain:feature-domain")
include(":feature-data:feature-data")
include(":app-tv")
include(":app-mobile:app-mobile")
include(":shared-domain")
