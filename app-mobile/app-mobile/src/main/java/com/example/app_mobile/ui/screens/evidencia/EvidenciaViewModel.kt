package com.example.app_mobile.ui.screens.evidencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_mobile.data.repository.EvidenciaRepository
import com.example.shared_domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EvidenciaUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val selectedProject: GalleryProject? = null
)

@HiltViewModel
class EvidenciaViewModel @Inject constructor(
    private val repository: EvidenciaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EvidenciaUiState())
    val uiState: StateFlow<EvidenciaUiState> = _uiState.asStateFlow()
    
    // Búsqueda y filtrado
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedStyle = MutableStateFlow("Estilos")
    val selectedStyle: StateFlow<String> = _selectedStyle.asStateFlow()
    
    // Datos de galería
    val allGalleryProjects: StateFlow<List<GalleryProject>> = repository.galleryProjects
        .map { it.values.toList().sortedByDescending { project -> project.lastUpdated } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Proyectos filtrados
    val filteredProjects: StateFlow<List<GalleryProject>> = combine(
        allGalleryProjects,
        searchQuery,
        selectedStyle
    ) { projects, query, style ->
        projects.filter { project ->
            val matchesSearch = query.isEmpty() || 
                project.name.contains(query, ignoreCase = true) ||
                project.description.contains(query, ignoreCase = true)
            val matchesStyle = style == "Estilos" || project.style == style
            matchesSearch && matchesStyle
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // Evidencia asociada al proyecto actual
    private val _currentProjectEvidence = MutableStateFlow<List<Evidence>>(emptyList())
    val currentProjectEvidence: StateFlow<List<Evidence>> = _currentProjectEvidence.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateSelectedStyle(style: String) {
        _selectedStyle.value = style
    }
    
    fun getProjectById(id: String): StateFlow<GalleryProject?> {
        return repository.galleryProjects
            .map { it[id] }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
    
    fun loadProjectDetail(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val project = repository.getGalleryProjectById(projectId)
                if (project != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedProject = project,
                        isLoading = false
                    )
                    
                    // Cargar evidencia asociada
                    loadProjectEvidence(project.projectId ?: projectId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Proyecto no encontrado",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar proyecto: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    private fun loadProjectEvidence(projectId: String) {
        viewModelScope.launch {
            repository.getEvidenceByProjectId(projectId)
                .collect { evidence ->
                    _currentProjectEvidence.value = evidence
                }
        }
    }
    
    fun toggleProjectFavorite(projectId: String) {
        viewModelScope.launch {
            try {
                val result = repository.toggleGalleryProjectFavorite(projectId)
                result.fold(
                    onSuccess = { updatedProject ->
                        _uiState.value = _uiState.value.copy(
                            message = if (updatedProject.isFavorite) "Agregado a favoritos" else "Eliminado de favoritos",
                            selectedProject = if (_uiState.value.selectedProject?.id == projectId) {
                                updatedProject
                            } else {
                                _uiState.value.selectedProject
                            }
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Error al actualizar favorito: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun createGalleryProject(project: GalleryProject) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = repository.createGalleryProject(project)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            message = "Proyecto agregado a la galería exitosamente",
                            isLoading = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Error al crear proyecto: ${error.message}",
                            isLoading = false
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun updateGalleryProject(project: GalleryProject) {
        viewModelScope.launch {
            try {
                val result = repository.updateGalleryProject(project)
                result.fold(
                    onSuccess = { updatedProject ->
                        _uiState.value = _uiState.value.copy(
                            message = "Proyecto actualizado exitosamente",
                            selectedProject = if (_uiState.value.selectedProject?.id == updatedProject.id) {
                                updatedProject
                            } else {
                                _uiState.value.selectedProject
                            }
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Error al actualizar proyecto: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun deleteGalleryProject(projectId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteGalleryProject(projectId)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            message = "Proyecto eliminado de la galería"
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Error al eliminar proyecto: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    // Funciones para manejo de evidencia
    fun createEvidence(evidence: Evidence) {
        viewModelScope.launch {
            try {
                val result = repository.createEvidence(evidence)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            message = "Evidencia creada exitosamente"
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Error al crear evidencia: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
