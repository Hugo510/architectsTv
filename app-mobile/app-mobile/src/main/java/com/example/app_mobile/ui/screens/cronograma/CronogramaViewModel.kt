package com.example.app_mobile.ui.screens.cronograma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_mobile.data.repository.CronogramaRepository
import com.example.shared_domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CronogramaUiState(
    val selectedView: ViewType = ViewType.CRONOGRAMA,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)

enum class ViewType {
    CRONOGRAMA, KANBAN
}

class CronogramaViewModel(
    private val repository: CronogramaRepository = CronogramaRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CronogramaUiState())
    val uiState: StateFlow<CronogramaUiState> = _uiState.asStateFlow()
    
    // Estado del cronograma actual
    val currentSchedule: StateFlow<ProjectSchedule?> = repository.schedules
        .map { schedules -> schedules.values.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    // Todas las tareas
    val allTasks: StateFlow<List<ScheduleTask>> = repository.tasks
        .map { tasks -> tasks.values.toList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Hitos del proyecto
    val milestones: StateFlow<List<Milestone>> = repository.milestones
        .map { milestones -> milestones.values.toList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // === UI STATE MANAGEMENT ===
    
    fun setViewType(viewType: ViewType) {
        _uiState.value = _uiState.value.copy(selectedView = viewType)
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, isError = false)
    }
    
    // === TASK OPERATIONS ===
    
    fun getTaskById(taskId: String): StateFlow<ScheduleTask?> {
        return repository.tasks
            .map { tasks -> tasks[taskId] }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
    
    fun updateTaskProgress(taskId: String, progress: Double) {
        viewModelScope.launch {
            setLoading(true)
            
            repository.updateTaskProgress(taskId, progress)
                .onSuccess {
                    showMessage("Progreso actualizado correctamente")
                }
                .onFailure { error ->
                    showError("Error al actualizar progreso: ${error.message}")
                }
                .also { setLoading(false) }
        }
    }
    
    fun updateTaskStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch {
            setLoading(true)
            
            repository.updateTaskStatus(taskId, status)
                .onSuccess {
                    showMessage("Estado de tarea actualizado")
                }
                .onFailure { error ->
                    showError("Error al actualizar estado: ${error.message}")
                }
                .also { setLoading(false) }
        }
    }
    
    fun createTask(task: ScheduleTask) {
        viewModelScope.launch {
            setLoading(true)
            
            repository.createTask(task)
                .onSuccess {
                    showMessage("Tarea creada correctamente")
                }
                .onFailure { error ->
                    showError("Error al crear tarea: ${error.message}")
                }
                .also { setLoading(false) }
        }
    }
    
    fun moveTaskToPhase(task: ScheduleTask, newPhase: ProjectStatus) {
        viewModelScope.launch {
            setLoading(true)
            
            repository.moveTaskToPhase(task.id, newPhase)
                .onSuccess {
                    showMessage("Tarea movida a ${getProjectStatusDisplayName(newPhase)}")
                }
                .onFailure { error ->
                    showError("Error al mover tarea: ${error.message}")
                }
                .also { setLoading(false) }
        }
    }
    
    // === MILESTONE OPERATIONS ===
    
    fun completeMilestone(milestoneId: String) {
        viewModelScope.launch {
            try {
                setLoading(true)
                val milestone = repository.getMilestoneById(milestoneId)
                if (milestone != null && !milestone.isCompleted) {
                    val updatedMilestone = milestone.copy(
                        isCompleted = true,
                        completedDate = getCurrentDate()
                    )
                    repository.updateMilestone(updatedMilestone)
                    showMessage("Hito completado correctamente")
                }
            } catch (e: Exception) {
                showError("Error al completar hito: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
    
    // === PRIVATE HELPER METHODS ===
    
    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
    
    private fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(
            message = message,
            isError = false
        )
    }
    
    private fun showError(error: String) {
        _uiState.value = _uiState.value.copy(
            message = error,
            isError = true
        )
    }
    
    private fun getCurrentDate(): String {
        return "2025-07-14" // En producción usar fecha real
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
}
