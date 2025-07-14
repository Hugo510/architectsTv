package com.example.app_mobile.ui.screens.cronograma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shared_domain.model.*
import com.example.app_mobile.data.repository.CronogramaRepository
import com.example.app_mobile.ui.screens.cronograma.components.ViewType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel para gestionar el estado del módulo de cronogramas
 * Maneja la lógica de presentación y comunicación con el repositorio
 */
class CronogramaViewModel(
    private val repository: CronogramaRepository = CronogramaRepository()
) : ViewModel() {

    // Estados privados
    private val _uiState = MutableStateFlow(CronogramaUiState())
    val uiState: StateFlow<CronogramaUiState> = _uiState.asStateFlow()

    // Estados derivados del repositorio
    val currentSchedule: StateFlow<ProjectSchedule?> = repository.schedules
        .map { schedules -> schedules.values.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allTasks: StateFlow<List<ScheduleTask>> = repository.tasks
        .map { tasks -> tasks.values.toList().sortedBy { it.startDate } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val milestones: StateFlow<List<Milestone>> = repository.milestones
        .map { milestones -> milestones.values.toList().sortedBy { it.targetDate } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Tareas agrupadas por estado
    val tasksByStatus: StateFlow<Map<TaskStatus, List<ScheduleTask>>> = allTasks
        .map { tasks -> tasks.groupBy { it.status } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Tareas por fase de proyecto
    val tasksByPhase: StateFlow<Map<ProjectStatus, List<ScheduleTask>>> = allTasks
        .map { tasks ->
            tasks.groupBy { mapTaskCategoryToProjectStatus(it.category) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    init {
        // Inicialización del ViewModel
        loadInitialData()
    }

    // === OPERACIONES DE CRONOGRAMA ===

    /**
     * Carga los datos iniciales del cronograma
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            setLoading(true)
            try {
                // Los datos se cargan automáticamente desde el repositorio
                // gracias a los StateFlow reactivos
                setLoading(false)
            } catch (e: Exception) {
                handleError("Error al cargar datos iniciales", e)
            }
        }
    }

    /**
     * Crear un nuevo cronograma
     */
    fun createSchedule(
        projectId: String,
        name: String,
        description: String? = null
    ) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val newSchedule = ProjectSchedule(
                    id = "schedule_${UUID.randomUUID()}",
                    projectId = projectId,
                    name = name,
                    description = description,
                    tasks = emptyList(),
                    milestones = emptyList(),
                    metadata = ScheduleMetadata(
                        createdAt = getCurrentTimestamp(),
                        updatedAt = getCurrentTimestamp(),
                        version = 1
                    )
                )

                repository.createSchedule(newSchedule)
                    .onSuccess {
                        showMessage("Cronograma creado correctamente")
                    }
                    .onFailure { error ->
                        handleError("Error al crear cronograma", error)
                    }
            } catch (e: Exception) {
                handleError("Error inesperado al crear cronograma", e)
            } finally {
                setLoading(false)
            }
        }
    }

    // === OPERACIONES DE TAREAS ===

    /**
     * Obtener una tarea por ID
     */
    fun getTaskById(taskId: String): StateFlow<ScheduleTask?> {
        return repository.tasks
            .map { tasks -> tasks[taskId] }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    /**
     * Crear una nueva tarea
     */
    fun createTask(task: ScheduleTask) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.createTask(task)
                    .onSuccess {
                        showMessage("Tarea '${task.name}' creada correctamente")
                    }
                    .onFailure { error ->
                        handleError("Error al crear la tarea", error)
                    }
            } catch (e: Exception) {
                handleError("Error inesperado al crear tarea", e)
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Actualizar el progreso de una tarea
     */
    fun updateTaskProgress(taskId: String, progress: Double) {
        viewModelScope.launch {
            try {
                repository.updateTask(taskId) { task ->
                    val newStatus = when {
                        progress >= 1.0 -> TaskStatus.COMPLETED
                        progress > 0.0 && task.status == TaskStatus.NOT_STARTED -> TaskStatus.IN_PROGRESS
                        else -> task.status
                    }
                    task.copy(
                        progress = progress.coerceIn(0.0, 1.0),
                        status = newStatus
                    )
                }
                    .onSuccess {
                        showMessage("Progreso actualizado")
                    }
                    .onFailure { error ->
                        handleError("Error al actualizar progreso", error)
                    }
            } catch (e: Exception) {
                handleError("Error inesperado al actualizar progreso", e)
            }
        }
    }

    /**
     * Actualizar el estado de una tarea
     */
    fun updateTaskStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch {
            try {
                repository.updateTaskStatus(taskId, status)
                    .onSuccess {
                        showMessage("Estado de tarea actualizado")
                    }
                    .onFailure { error ->
                        handleError("Error al actualizar estado", error)
                    }
            } catch (e: Exception) {
                handleError("Error inesperado al actualizar estado", e)
            }
        }
    }

    /**
     * Eliminar una tarea
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.deleteTask(taskId)
                    .onSuccess {
                        showMessage("Tarea eliminada correctamente")
                    }
                    .onFailure { error ->
                        handleError("Error al eliminar tarea", error)
                    }
            } catch (e: Exception) {
                handleError("Error inesperado al eliminar tarea", e)
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Mover tarea a una fase específica del proyecto
     */
    fun moveTaskToPhase(task: ScheduleTask, newPhase: ProjectStatus) {
        viewModelScope.launch {
            try {
                repository.moveTaskToPhase(task.id, newPhase)
                    .onSuccess {
                        showMessage("Tarea movida a ${getProjectStatusDisplayName(newPhase)}")
                    }
                    .onFailure { error ->
                        handleError("Error al mover tarea", error)
                    }
            } catch (e: Exception) {
                handleError("Error inesperado al mover tarea", e)
            }
        }
    }

    // === OPERACIONES DE HITOS ===

    /**
     * Marcar/desmarcar un hito como completado
     */
    fun toggleMilestoneCompletion(milestoneId: String) {
        viewModelScope.launch {
            try {
                val milestone = milestones.value.find { it.id == milestoneId }
                    ?: return@launch

                val updatedMilestone = milestone.copy(
                    isCompleted = !milestone.isCompleted,
                    completedDate = if (!milestone.isCompleted) getCurrentTimestamp() else null
                )

                repository.updateMilestone(updatedMilestone)
                    .onSuccess {
                        val action = if (updatedMilestone.isCompleted) "completado" else "marcado como pendiente"
                        showMessage("Hito $action")
                    }
                    .onFailure { error ->
                        handleError("Error al actualizar hito", error)
                    }
            } catch (e: Exception) {
                handleError("Error inesperado al actualizar hito", e)
            }
        }
    }

    // === CONFIGURACIÓN DE VISTA ===

    /**
     * Cambiar entre vista Cronograma y Kanban
     */
    fun setViewType(viewType: ViewType) {
        _uiState.value = _uiState.value.copy(selectedView = viewType)
    }

    /**
     * Configurar filtros de búsqueda
     */
    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    /**
     * Filtrar tareas por estado
     */
    fun setStatusFilter(status: TaskStatus?) {
        _uiState.value = _uiState.value.copy(statusFilter = status)
    }

    /**
     * Filtrar tareas por prioridad
     */
    fun setPriorityFilter(priority: TaskPriority?) {
        _uiState.value = _uiState.value.copy(priorityFilter = priority)
    }

    // === GESTIÓN DE ESTADO UI ===

    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    private fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    private fun handleError(message: String, error: Throwable) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = message,
            message = message
        )
        // Log del error para debug
        println("CronogramaViewModel Error: $message - ${error.message}")
    }

    /**
     * Limpiar mensajes después de mostrarlos
     */
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    // === FUNCIONES AUXILIARES ===

    private fun getCurrentTimestamp(): String {
        return "2024-01-15T${System.currentTimeMillis() % 86400000 / 1000}:00Z"
    }

    private fun mapTaskCategoryToProjectStatus(category: TaskCategory): ProjectStatus {
        return when (category) {
            TaskCategory.DESIGN -> ProjectStatus.DESIGN
            TaskCategory.PERMITS -> ProjectStatus.PERMITS_REVIEW
            TaskCategory.CONSTRUCTION, TaskCategory.INSPECTION -> ProjectStatus.CONSTRUCTION
            TaskCategory.DELIVERY -> ProjectStatus.DELIVERY
            else -> ProjectStatus.DESIGN
        }
    }

    private fun getProjectStatusDisplayName(status: ProjectStatus): String {
        return when (status) {
            ProjectStatus.DESIGN -> "Diseño"
            ProjectStatus.PERMITS_REVIEW -> "Revisión de Permisos"
            ProjectStatus.CONSTRUCTION -> "Construcción"
            ProjectStatus.DELIVERY -> "Entrega"
        }
    }

    // === CASOS DE USO ADICIONALES ===

    /**
     * Obtener estadísticas del cronograma
     */
    fun getScheduleStatistics(): StateFlow<ScheduleStatistics> {
        return combine(
            allTasks,
            milestones,
            currentSchedule
        ) { tasks, milestones, schedule ->
            ScheduleStatistics(
                totalTasks = tasks.size,
                completedTasks = tasks.count { it.status == TaskStatus.COMPLETED },
                inProgressTasks = tasks.count { it.status == TaskStatus.IN_PROGRESS },
                overdueTasks = tasks.count { it.isOverdue },
                totalMilestones = milestones.size,
                completedMilestones = milestones.count { it.isCompleted },
                overallProgress = schedule?.totalProgress ?: 0.0
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScheduleStatistics()
        )
    }

    /**
     * Obtener tareas próximas a vencer
     */
    fun getUpcomingTasks(): StateFlow<List<ScheduleTask>> {
        return allTasks
            .map { tasks ->
                tasks.filter { task ->
                    task.status != TaskStatus.COMPLETED &&
                    task.status != TaskStatus.CANCELLED
                    // Aquí se podría agregar lógica de fechas
                }.take(5)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Factory para crear el ViewModel con inyección de dependencias
    class Factory(
        private val repository: CronogramaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CronogramaViewModel::class.java)) {
                return CronogramaViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

/**
 * Estado de la UI del módulo de cronogramas
 */
data class CronogramaUiState(
    val isLoading: Boolean = false,
    val selectedView: ViewType = ViewType.CRONOGRAMA,
    val searchQuery: String = "",
    val statusFilter: TaskStatus? = null,
    val priorityFilter: TaskPriority? = null,
    val message: String? = null,
    val error: String? = null
)

/**
 * Estadísticas del cronograma para mostrar en dashboard
 */
data class ScheduleStatistics(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val overdueTasks: Int = 0,
    val totalMilestones: Int = 0,
    val completedMilestones: Int = 0,
    val overallProgress: Double = 0.0
) {
    val completionPercentage: Int get() = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks) * 100).toInt() else 0
    val milestoneCompletionPercentage: Int get() = if (totalMilestones > 0) ((completedMilestones.toFloat() / totalMilestones) * 100).toInt() else 0
}
