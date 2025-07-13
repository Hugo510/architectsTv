package com.example.app_mobile.ui.screens.cronograma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_mobile.data.repository.CronogramaRepository
import com.example.shared_domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CronogramaViewModel(
    private val repository: CronogramaRepository = CronogramaRepository()
) : ViewModel() {
    
    // Estados observables
    private val _uiState = MutableStateFlow(CronogramaUiState())
    val uiState: StateFlow<CronogramaUiState> = _uiState.asStateFlow()
    
    // Cronograma actual
    val currentSchedule: StateFlow<ProjectSchedule?> = repository.schedules
        .map { schedules -> schedules.values.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    // Tareas agrupadas por fase
    val tasksByPhase: StateFlow<Map<ProjectStatus, List<ScheduleTask>>> = repository.tasks
        .map { tasks ->
            tasks.values.groupBy { mapTaskCategoryToProjectStatus(it.category) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    
    // Lista de todas las tareas
    val allTasks: StateFlow<List<ScheduleTask>> = repository.tasks
        .map { it.values.toList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Hitos
    val milestones: StateFlow<List<Milestone>> = repository.milestones
        .map { it.values.toList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun setViewType(viewType: ViewType) {
        _uiState.value = _uiState.value.copy(selectedView = viewType)
    }
    
    fun moveTaskToPhase(task: ScheduleTask, newPhase: ProjectStatus) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.moveTaskToPhase(task.id, newPhase)
                showMessage("Tarea movida a ${getProjectStatusDisplayName(newPhase)} correctamente")
            } catch (e: Exception) {
                showError("Error al mover la tarea: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun updateTaskProgress(taskId: String, progress: Double) {
        viewModelScope.launch {
            try {
                repository.updateTaskProgress(taskId, progress)
            } catch (e: Exception) {
                showError("Error al actualizar progreso: ${e.message}")
            }
        }
    }
    
    fun updateTaskStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.updateTaskStatus(taskId, status)
                showMessage("Estado de tarea actualizado correctamente")
            } catch (e: Exception) {
                showError("Error al actualizar estado: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun createTask(task: ScheduleTask) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.createTask(task)
                showMessage("Tarea creada correctamente")
            } catch (e: Exception) {
                showError("Error al crear tarea: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun updateTask(taskId: String, updatedTask: ScheduleTask) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.updateTask(taskId) { updatedTask }
                showMessage("Tarea actualizada correctamente")
            } catch (e: Exception) {
                showError("Error al actualizar tarea: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.deleteTask(taskId)
                showMessage("Tarea eliminada correctamente")
            } catch (e: Exception) {
                showError("Error al eliminar tarea: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
    
    fun getTaskById(taskId: String): StateFlow<ScheduleTask?> {
        return repository.tasks
            .map { tasks -> tasks[taskId] }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
    
    fun getMilestoneById(milestoneId: String): StateFlow<Milestone?> {
        return repository.milestones
            .map { milestones -> milestones[milestoneId] }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
    
    fun completeMilestone(milestoneId: String) {
        viewModelScope.launch {
            try {
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
            }
        }
    }
    
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
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, isError = false)
    }
    
    private fun getCurrentDate(): String {
        return "2024-01-15" // En producción usar fecha real
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

data class CronogramaUiState(
    val selectedView: ViewType = ViewType.CRONOGRAMA,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)

enum class ViewType {
    CRONOGRAMA, KANBAN
}
