package com.example.app_mobile.data.repository

import com.example.shared_domain.model.*
import com.example.shared_domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class EvidenciaRepository : ProjectRepository {
    
    // Estado centralizado para evidencia
    private val _evidence = MutableStateFlow<Map<String, Evidence>>(emptyMap())
    val evidence: StateFlow<Map<String, Evidence>> = _evidence.asStateFlow()
    
    // Estado centralizado para proyectos de galería
    private val _galleryProjects = MutableStateFlow<Map<String, GalleryProject>>(emptyMap())
    val galleryProjects: StateFlow<Map<String, GalleryProject>> = _galleryProjects.asStateFlow()
    
    // Estado para proyectos base
    private val _projects = MutableStateFlow<Map<String, Project>>(emptyMap())
    val projects: StateFlow<Map<String, Project>> = _projects.asStateFlow()
    
    init {
        initializeMockData()
    }
    
    // Gallery Project operations
    override suspend fun getAllGalleryProjects(): Flow<List<GalleryProject>> {
        return _galleryProjects.map { it.values.toList() }
    }
    
    override suspend fun getGalleryProjectById(id: String): GalleryProject? {
        return _galleryProjects.value[id]
    }
    
    override suspend fun createGalleryProject(project: GalleryProject): Result<GalleryProject> {
        return try {
            val updatedProjects = _galleryProjects.value.toMutableMap()
            updatedProjects[project.id] = project
            _galleryProjects.value = updatedProjects
            
            // Crear evidencia asociada si no existe
            createAssociatedEvidence(project)
            
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateGalleryProject(project: GalleryProject): Result<GalleryProject> {
        return try {
            val updatedProjects = _galleryProjects.value.toMutableMap()
            updatedProjects[project.id] = project
            _galleryProjects.value = updatedProjects
            
            // Actualizar evidencia relacionada
            updateAssociatedEvidence(project)
            
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteGalleryProject(id: String): Result<Unit> {
        return try {
            val updatedProjects = _galleryProjects.value.toMutableMap()
            val project = updatedProjects.remove(id)
            _galleryProjects.value = updatedProjects
            
            // Eliminar evidencia asociada
            if (project != null) {
                deleteAssociatedEvidence(project)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun toggleGalleryProjectFavorite(id: String): Result<GalleryProject> {
        return try {
            val currentProjects = _galleryProjects.value.toMutableMap()
            val project = currentProjects[id] ?: return Result.failure(IllegalArgumentException("Project not found"))
            
            val updatedProject = project.copy(isFavorite = !project.isFavorite)
            currentProjects[id] = updatedProject
            _galleryProjects.value = currentProjects
            
            Result.success(updatedProject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchGalleryProjects(query: String): Flow<List<GalleryProject>> {
        return _galleryProjects.map { projectsMap ->
            projectsMap.values.filter { project ->
                project.name.contains(query, ignoreCase = true) ||
                project.description.contains(query, ignoreCase = true) ||
                project.style.contains(query, ignoreCase = true)
            }
        }
    }
    
    override suspend fun getGalleryProjectsByStyle(style: String): Flow<List<GalleryProject>> {
        return _galleryProjects.map { projectsMap ->
            if (style == "Estilos") {
                projectsMap.values.toList()
            } else {
                projectsMap.values.filter { it.style == style }
            }
        }
    }
    
    // Evidence operations
    override suspend fun getEvidenceByProjectId(projectId: String): Flow<List<Evidence>> {
        return _evidence.map { evidenceMap ->
            evidenceMap.values.filter { it.projectId == projectId }
        }
    }
    
    override suspend fun getEvidenceById(id: String): Evidence? {
        return _evidence.value[id]
    }
    
    override suspend fun createEvidence(evidence: Evidence): Result<Evidence> {
        return try {
            val updatedEvidence = _evidence.value.toMutableMap()
            updatedEvidence[evidence.id] = evidence
            _evidence.value = updatedEvidence
            
            // Actualizar proyecto de galería relacionado
            updateGalleryProjectFromEvidence(evidence)
            
            Result.success(evidence)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateEvidence(evidence: Evidence): Result<Evidence> {
        return try {
            val updatedEvidence = _evidence.value.toMutableMap()
            updatedEvidence[evidence.id] = evidence
            _evidence.value = updatedEvidence
            
            // Mantener sincronización con galería
            updateGalleryProjectFromEvidence(evidence)
            
            Result.success(evidence)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteEvidence(id: String): Result<Unit> {
        return try {
            val updatedEvidence = _evidence.value.toMutableMap()
            val evidence = updatedEvidence.remove(id)
            _evidence.value = updatedEvidence
            
            // Actualizar proyecto de galería
            if (evidence != null) {
                removeEvidenceFromGalleryProject(evidence)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEvidenceByCategory(projectId: String, category: EvidenceCategory): Flow<List<Evidence>> {
        return _evidence.map { evidenceMap ->
            evidenceMap.values.filter { 
                it.projectId == projectId && it.category == category 
            }
        }
    }
    
    override suspend fun getEvidenceByDateRange(projectId: String, startDate: String, endDate: String): Flow<List<Evidence>> {
        return _evidence.map { evidenceMap ->
            evidenceMap.values.filter { evidence ->
                evidence.projectId == projectId &&
                evidence.capturedAt >= startDate &&
                evidence.capturedAt <= endDate
            }
        }
    }
    
    // Helper methods para mantener integridad de datos
    private suspend fun createAssociatedEvidence(project: GalleryProject) {
        // Crear evidencia base para el proyecto de galería
        val baseEvidence = Evidence(
            id = "evidence_${project.id}",
            projectId = project.projectId ?: project.id,
            title = "Proyecto Completado: ${project.name}",
            description = project.description,
            type = EvidenceType.PHOTO,
            category = project.category ?: EvidenceCategory.DELIVERY,
            media = EvidenceMedia(
                files = listOf(
                    MediaFile(
                        id = "file_${project.id}",
                        fileName = "${project.name}.jpg",
                        url = project.imageUrl,
                        size = 2048000,
                        mimeType = "image/jpeg",
                        width = 1920,
                        height = 1080
                    )
                ),
                thumbnailUrl = project.imageUrl
            ),
            location = EvidenceLocation(
                area = "Proyecto General",
                reference = project.location
            ),
            capturedBy = project.architect,
            capturedAt = project.completedDate,
            tags = listOf(project.style, "completado", "galería"),
            status = EvidenceStatus.ACTIVE,
            metadata = EvidenceMetadata(
                createdAt = project.lastUpdated,
                updatedAt = getCurrentTimestamp(),
                version = 1
            )
        )
        
        val updatedEvidence = _evidence.value.toMutableMap()
        updatedEvidence[baseEvidence.id] = baseEvidence
        _evidence.value = updatedEvidence
    }
    
    private suspend fun updateAssociatedEvidence(project: GalleryProject) {
        val evidenceId = "evidence_${project.id}"
        val currentEvidence = _evidence.value[evidenceId]
        
        if (currentEvidence != null) {
            val updatedEvidence = currentEvidence.copy(
                title = "Proyecto Completado: ${project.name}",
                description = project.description,
                tags = listOf(project.style, "completado", "galería"),
                metadata = currentEvidence.metadata.copy(
                    updatedAt = getCurrentTimestamp(),
                    version = currentEvidence.metadata.version + 1
                )
            )
            
            val evidenceMap = _evidence.value.toMutableMap()
            evidenceMap[evidenceId] = updatedEvidence
            _evidence.value = evidenceMap
        }
    }
    
    private suspend fun deleteAssociatedEvidence(project: GalleryProject) {
        val evidenceId = "evidence_${project.id}"
        val updatedEvidence = _evidence.value.toMutableMap()
        updatedEvidence.remove(evidenceId)
        _evidence.value = updatedEvidence
    }
    
    private suspend fun updateGalleryProjectFromEvidence(evidence: Evidence) {
        // Buscar si existe un proyecto de galería asociado
        val relatedProject = _galleryProjects.value.values.find { 
            it.projectId == evidence.projectId || it.evidenceIds.contains(evidence.id)
        }
        
        if (relatedProject != null) {
            val updatedProject = relatedProject.copy(
                description = evidence.description ?: relatedProject.description,
                lastUpdated = evidence.metadata.updatedAt,
                evidenceIds = if (evidence.id !in relatedProject.evidenceIds) {
                    relatedProject.evidenceIds + evidence.id
                } else {
                    relatedProject.evidenceIds
                }
            )
            
            val projectsMap = _galleryProjects.value.toMutableMap()
            projectsMap[relatedProject.id] = updatedProject
            _galleryProjects.value = projectsMap
        }
    }
    
    private suspend fun removeEvidenceFromGalleryProject(evidence: Evidence) {
        val relatedProject = _galleryProjects.value.values.find { 
            it.evidenceIds.contains(evidence.id)
        }
        
        if (relatedProject != null) {
            val updatedProject = relatedProject.copy(
                evidenceIds = relatedProject.evidenceIds - evidence.id,
                lastUpdated = getCurrentTimestamp()
            )
            
            val projectsMap = _galleryProjects.value.toMutableMap()
            projectsMap[relatedProject.id] = updatedProject
            _galleryProjects.value = projectsMap
        }
    }
    
    private fun getCurrentTimestamp(): String {
        return "2024-01-15T${System.currentTimeMillis() % 86400000 / 1000}:00Z"
    }
    
    private fun initializeMockData() {
        // Inicializar con datos consistentes entre evidencia y galería
        val mockGalleryProjects = mapOf(
            "1" to GalleryProject(
                id = "1",
                name = "Casa Contemporánea Tropical",
                description = "Residencia moderna con elementos naturales y gran iluminación que se integra perfectamente con el entorno tropical",
                style = "Contemporáneo",
                location = "Durango, México",
                imageUrl = "https://example.com/house1.jpg",
                rating = 4.8,
                reviewCount = 24,
                completedDate = "2023-12-15T00:00:00Z",
                architect = "Arq. Steve Johnson",
                area = "320 m²",
                cardHeight = 320,
                projectId = "project_1",
                evidenceIds = listOf("evidence_1"),
                lastUpdated = "2023-12-15T10:30:00Z",
                category = EvidenceCategory.DELIVERY
            ),
            "2" to GalleryProject(
                id = "2",
                name = "Villa Minimalista",
                description = "Diseño limpio y funcional",
                style = "Minimalista",
                location = "Durango, México",
                imageUrl = "https://example.com/house2.jpg",
                rating = 4.9,
                reviewCount = 18,
                completedDate = "2023-11-20T00:00:00Z",
                architect = "Arq. María López",
                area = "280 m²",
                cardHeight = 260,
                projectId = "project_2",
                evidenceIds = listOf("evidence_2"),
                lastUpdated = "2023-11-20T14:15:00Z",
                category = EvidenceCategory.DELIVERY
            ),
            // Agregar más proyectos...
        )
        
        _galleryProjects.value = mockGalleryProjects
        
        // Crear evidencia asociada
        mockGalleryProjects.values.forEach { project ->
            // Llamar suspend function de manera síncrona en init (no recomendado en producción)
            kotlinx.coroutines.runBlocking {
                createAssociatedEvidence(project)
            }
        }
    }
    
    // Implementaciones básicas para otras operaciones (no utilizadas en evidencia)
    override suspend fun getAllProjects(): Flow<List<Project>> = _projects.map { it.values.toList() }
    override suspend fun getProjectById(id: String): Project? = _projects.value[id]
    override suspend fun createProject(project: Project): Result<Project> = Result.success(project)
    override suspend fun updateProject(project: Project): Result<Project> = Result.success(project)
    override suspend fun deleteProject(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun searchProjects(query: String): Flow<List<Project>> = _projects.map { it.values.toList() }
    override suspend fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> = _projects.map { it.values.toList() }
    
    // Schedule operations (no implementadas en este scope)
    override suspend fun getScheduleByProjectId(projectId: String): ProjectSchedule? = null
    override suspend fun createSchedule(schedule: ProjectSchedule): Result<ProjectSchedule> = Result.failure(NotImplementedError())
    override suspend fun updateSchedule(schedule: ProjectSchedule): Result<ProjectSchedule> = Result.failure(NotImplementedError())
    override suspend fun deleteSchedule(id: String): Result<Unit> = Result.failure(NotImplementedError())
    override suspend fun updateTaskProgress(taskId: String, progress: Double): Result<Unit> = Result.failure(NotImplementedError())
    
    // Blueprint operations (no implementadas en este scope)
    override suspend fun getBlueprintsByProjectId(projectId: String): Flow<List<Blueprint>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override suspend fun getBlueprintById(id: String): Blueprint? = null
    override suspend fun createBlueprint(blueprint: Blueprint): Result<Blueprint> = Result.failure(NotImplementedError())
    override suspend fun updateBlueprint(blueprint: Blueprint): Result<Blueprint> = Result.failure(NotImplementedError())
    override suspend fun deleteBlueprint(id: String): Result<Unit> = Result.failure(NotImplementedError())
    override suspend fun addBlueprintRevision(blueprintId: String, revision: BlueprintRevision): Result<Blueprint> = Result.failure(NotImplementedError())
}
