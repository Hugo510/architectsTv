package com.example.app_mobile.ui.screens.evidencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_mobile.data.repository.EvidenciaRepository
import com.example.shared_domain.model.*
import com.example.shared_domain.repository.GalleryProject
import com.example.shared_domain.repository.GalleryProjectMetadata
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

    fun createGalleryProject(
        name: String,
        description: String,
        style: String,
        location: String,
        architect: String,
        area: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Validar entrada antes de crear el proyecto
                validateProjectInput(name, description, style, location, architect, area)

                val newProject = GalleryProject(
                    id = generateProjectId(),
                    name = name.trim(),
                    description = description.trim(),
                    style = style,
                    location = location.trim(),
                    imageUrl = generatePlaceholderImageUrl(style),
                    rating = generateRandomRating(),
                    reviewCount = generateRandomReviewCount(),
                    completedDate = getCurrentDate(),
                    architect = architect.trim(),
                    area = formatArea(area),
                    isFavorite = false,
                    cardHeight = generateRandomCardHeight(),
                    projectId = "project_${generateProjectId()}",
                    evidenceIds = emptyList(),
                    lastUpdated = getCurrentDateTime(),
                    category = EvidenceCategory.DELIVERY,
                    metadata = GalleryProjectMetadata(
                        createdAt = getCurrentDateTime(),
                        updatedAt = getCurrentDateTime(),
                        version = 1,
                        tags = generateProjectTags(style, location),
                        viewCount = 0,
                        shareCount = 0,
                        featured = false,
                        verified = true
                    )
                )

                val result = repository.createGalleryProject(newProject)

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Proyecto creado exitosamente"
                    )
                    // Recargar la lista de proyectos
                    loadGalleryProjects()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al crear el proyecto: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    private fun validateProjectInput(
        name: String,
        description: String,
        style: String,
        location: String,
        architect: String,
        area: String
    ) {
        require(name.trim().length >= 3) { "El nombre del proyecto debe tener al menos 3 caracteres" }
        require(description.trim().length >= 10) { "La descripción debe tener al menos 10 caracteres" }
        require(location.trim().length >= 3) { "La ubicación debe tener al menos 3 caracteres" }
        require(architect.trim().length >= 3) { "El nombre del arquitecto debe tener al menos 3 caracteres" }

        val areaNumber = area.trim().toDoubleOrNull()
        require(areaNumber != null && areaNumber > 0) { "El área debe ser un número positivo" }
        require(areaNumber <= 10000) { "El área no puede ser mayor a 10,000 m²" }

        val validStyles = listOf("Contemporáneo", "Minimalista", "Industrial", "Moderno", "Clásico", "Rústico", "Colonial")
        require(style in validStyles) { "Estilo arquitectónico no válido" }
    }

    private fun formatArea(area: String): String {
        val areaNumber = area.trim().toDoubleOrNull() ?: 0.0
        return "${String.format("%.0f", areaNumber)} m²"
    }

    private fun generateProjectTags(style: String, location: String): List<String> {
        val baseTags = listOf(style.lowercase(), "proyecto", "completado")
        val locationTag = location.split(",").firstOrNull()?.trim()?.lowercase()
        return if (locationTag != null) {
            baseTags + locationTag
        } else {
            baseTags
        }
    }

    private fun generatePlaceholderImageUrl(style: String): String {
        // Generar URL de placeholder basada en el estilo
        val styleParam = style.lowercase().replace(" ", "-")
        return "https://picsum.photos/800/600?random=${System.currentTimeMillis()}&style=$styleParam"
    }

    private fun generateRandomRating(): Double {
        return (4.0 + Math.random() * 1.0).let {
            String.format("%.1f", it).toDouble()
        }
    }

    private fun generateRandomReviewCount(): Int {
        return (10 + Math.random() * 90).toInt()
    }

    private fun getCurrentDate(): String {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        return "${month.toString().padStart(2, '0')}/$year"
    }

    private fun getCurrentDateTime(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
    }

    private fun generateProjectId(): String {
        return "proj_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private fun generateRandomCardHeight(): Int {
        val heights = listOf(250, 280, 300, 320, 350, 380)
        return heights.random()
    }

    private fun loadGalleryProjects() {
        // Método para recargar proyectos de galería
        viewModelScope.launch {
            try {
                // La recarga se maneja automáticamente por el Flow del repositorio
                _uiState.value = _uiState.value.copy(
                    message = "Proyectos actualizados"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar proyectos: ${e.message}"
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
