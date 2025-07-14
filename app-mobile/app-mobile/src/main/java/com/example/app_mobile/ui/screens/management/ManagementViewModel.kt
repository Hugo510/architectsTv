package com.example.app_mobile.ui.screens.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_mobile.data.repository.ManagementRepository
import com.example.shared_domain.model.*
import com.example.shared_domain.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ManagementUiState(
    val projects: List<Project> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedStatus: String = "Estado",
    val isCreatingProject: Boolean = false,
    val createProjectSuccess: Boolean = false,
    val createProjectError: String? = null
)

class ManagementViewModel(
    private val repository: ProjectRepository = ManagementRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ManagementUiState())
    val uiState: StateFlow<ManagementUiState> = _uiState.asStateFlow()
    
    init {
        loadProjects()
    }
    
    fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                repository.getAllProjects().collect { projects ->
                    _uiState.value = _uiState.value.copy(
                        projects = projects,
                        isLoading = false
                    )
                    applyFilters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar proyectos: ${e.message}"
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }
    
    fun updateSelectedStatus(status: String) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        applyFilters()
    }
    
    fun createProject(project: Project) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCreatingProject = true,
                createProjectSuccess = false,
                createProjectError = null
            )
            
            try {
                val result = repository.createProject(project)
                
                result.fold(
                    onSuccess = { createdProject ->
                        _uiState.value = _uiState.value.copy(
                            isCreatingProject = false,
                            createProjectSuccess = true,
                            createProjectError = null
                        )
                        // Los proyectos se actualizan automáticamente por el Flow del repository
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isCreatingProject = false,
                            createProjectSuccess = false,
                            createProjectError = "Error al crear proyecto: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreatingProject = false,
                    createProjectSuccess = false,
                    createProjectError = "Error inesperado: ${e.message}"
                )
            }
        }
    }
    
    fun updateProject(project: Project) {
        viewModelScope.launch {
            try {
                val result = repository.updateProject(project)
                
                result.fold(
                    onSuccess = { updatedProject ->
                        // Los proyectos se actualizan automáticamente por el Flow del repository
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Error al actualizar proyecto: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error inesperado al actualizar: ${e.message}"
                )
            }
        }
    }
    
    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteProject(projectId)
                
                result.fold(
                    onSuccess = {
                        // Los proyectos se actualizan automáticamente por el Flow del repository
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Error al eliminar proyecto: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error inesperado al eliminar: ${e.message}"
                )
            }
        }
    }
    
    fun searchProjects(query: String) {
        viewModelScope.launch {
            try {
                repository.searchProjects(query).collect { searchResults ->
                    _uiState.value = _uiState.value.copy(
                        filteredProjects = searchResults
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error en búsqueda: ${e.message}"
                )
            }
        }
    }
    
    fun getProjectsByStatus(status: ProjectStatus) {
        viewModelScope.launch {
            try {
                repository.getProjectsByStatus(status).collect { filteredProjects ->
                    _uiState.value = _uiState.value.copy(
                        filteredProjects = filteredProjects
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al filtrar por estado: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearCreateProjectState() {
        _uiState.value = _uiState.value.copy(
            createProjectSuccess = false,
            createProjectError = null
        )
    }
    
    private fun applyFilters() {
        val currentState = _uiState.value
        val allProjects = currentState.projects
        
        val filtered = allProjects.filter { project ->
            val matchesSearch = currentState.searchQuery.isEmpty() || 
                project.name.contains(currentState.searchQuery, ignoreCase = true) ||
                project.description?.contains(currentState.searchQuery, ignoreCase = true) == true
            
            val matchesStatus = currentState.selectedStatus == "Estado" || 
                mapProjectStatusToDisplayName(project.status) == currentState.selectedStatus
            
            matchesSearch && matchesStatus
        }
        
        _uiState.value = currentState.copy(filteredProjects = filtered)
    }
    
    // Usar función de mapeo compatible con shared_domain
    private fun mapProjectStatusToDisplayName(status: ProjectStatus): String {
        return when (status) {
            ProjectStatus.DESIGN -> "Diseño"
            ProjectStatus.PERMITS_REVIEW -> "Revisión de Permisos"
            ProjectStatus.CONSTRUCTION -> "Construcción"
            ProjectStatus.DELIVERY -> "Entrega"
        }
    }
}
