package com.example.app_mobile.ui.navigation

/**
 * Objeto que centraliza todas las rutas de navegación de la app móvil
 */
object MobileRoutes {
    const val HOME = "home"
    const val PROJECTS_LIST = "projects"
    const val PROJECT_DETAIL = "project_detail/{projectId}"
    const val PROJECT_CREATE = "project_create"
    const val PROJECT_EDIT = "project_edit/{projectId}"
    
    // CRUD Management
    const val MANAGEMENT_LIST = "management"
    const val MANAGEMENT_DETAIL = "management_detail/{projectId}"
    const val MANAGEMENT_CREATE = "management_create"
    const val MANAGEMENT_EDIT = "management_edit/{projectId}"
    
    // CRUD Cronograma
    const val CRONOGRAMA_LIST = "cronograma"
    const val CRONOGRAMA_DETAIL = "cronograma_detail/{projectId}"
    const val CRONOGRAMA_CREATE = "cronograma_create"
    const val CRONOGRAMA_EDIT = "cronograma_edit/{projectId}"
    
    // CRUD Planos
    const val PLANOS_LIST = "planos"
    const val PLANOS_DETAIL = "planos_detail/{projectId}"
    const val PLANOS_CREATE = "planos_create"
    const val PLANOS_EDIT = "planos_edit/{projectId}"
    
    // CRUD Evidencia
    const val EVIDENCIA_LIST = "evidencia"
    const val EVIDENCIA_DETAIL = "evidencia_detail/{projectId}"
    const val EVIDENCIA_CREATE = "evidencia_create"
    const val EVIDENCIA_EDIT = "evidencia_edit/{projectId}"
    
    // Cast Control
    const val CAST_CONTROL = "cast_control"
    const val CAST_DEVICES = "cast_devices"
    
    // Settings
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    
    // Helper functions
    fun projectDetail(projectId: String) = "project_detail/$projectId"
    fun projectEdit(projectId: String) = "project_edit/$projectId"
    fun managementDetail(projectId: String) = "management_detail/$projectId"
    fun managementEdit(projectId: String) = "management_edit/$projectId"
    // ... más helper functions según necesidad
}

/**
 * Sealed class para manejar argumentos de navegación de manera type-safe
 */
sealed class NavigationArgs {
    object None : NavigationArgs()
    data class ProjectId(val id: String) : NavigationArgs()
    data class ProjectWithMode(val id: String, val mode: EditMode) : NavigationArgs()
}

enum class EditMode {
    CREATE, EDIT, VIEW
}
