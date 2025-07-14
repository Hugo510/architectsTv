package com.example.app_mobile.data.repository

import com.example.shared_domain.model.*
import com.example.shared_domain.repository.GalleryProject
import com.example.shared_domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf

class ManagementRepository : ProjectRepository {
    
    // Estado centralizado para proyectos
    private val _projects = MutableStateFlow<Map<String, Project>>(emptyMap())
    val projects: StateFlow<Map<String, Project>> = _projects.asStateFlow()
    
    init {
        initializeMockData()
    }
    
    // Project operations - implementación completa según shared_domain
    override suspend fun getAllProjects(): Flow<List<Project>> {
        return _projects.map { it.values.toList().sortedByDescending { project -> project.metadata.updatedAt } }
    }
    
    override suspend fun getProjectById(id: String): Project? {
        return _projects.value[id]
    }
    
    override suspend fun createProject(project: Project): Result<Project> {
        return try {
            validateProjectWithSharedDomain(project)
            
            val enhancedProject = project.copy(
                metadata = project.metadata.copy(
                    createdAt = getCurrentTimestamp(),
                    updatedAt = getCurrentTimestamp(),
                    version = 1
                )
            )
            
            val updatedProjects = _projects.value.toMutableMap()
            updatedProjects[enhancedProject.id] = enhancedProject
            _projects.value = updatedProjects
            
            Result.success(enhancedProject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProject(project: Project): Result<Project> {
        return try {
            validateProjectWithSharedDomain(project)
            
            val currentProject = _projects.value[project.id]
                ?: return Result.failure(IllegalArgumentException("Project not found"))
            
            val updatedProject = project.copy(
                metadata = project.metadata.copy(
                    updatedAt = getCurrentTimestamp(),
                    version = currentProject.metadata.version + 1
                )
            )
            
            val updatedProjects = _projects.value.toMutableMap()
            updatedProjects[project.id] = updatedProject
            _projects.value = updatedProjects
            
            Result.success(updatedProject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteProject(id: String): Result<Unit> {
        return try {
            val updatedProjects = _projects.value.toMutableMap()
            val removedProject = updatedProjects.remove(id)
            _projects.value = updatedProjects
            
            if (removedProject != null) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Project not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchProjects(query: String): Flow<List<Project>> {
        return _projects.map { projectsMap ->
            projectsMap.values.filter { project ->
                project.name.contains(query, ignoreCase = true) ||
                project.description?.contains(query, ignoreCase = true) == true ||
                project.client.name.contains(query, ignoreCase = true) ||
                project.projectManager.name.contains(query, ignoreCase = true)
            }.sortedByDescending { it.metadata.updatedAt }
        }
    }
    
    override suspend fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> {
        return _projects.map { projectsMap ->
            projectsMap.values.filter { it.status == status }
                .sortedByDescending { it.metadata.updatedAt }
        }
    }
    
    // Validación usando las reglas del dominio compartido
    private fun validateProjectWithSharedDomain(project: Project) {
        // Usar las validaciones integradas en el modelo del dominio compartido
        // Las validaciones init{} de Project ya validan los campos requeridos
        
        // Validaciones adicionales específicas del repository
        require(project.name.length >= 3) { "Project name must be at least 3 characters" }
        
        // Validar ubicación usando las validaciones del modelo
        require(project.location.address.isNotBlank()) { "Address cannot be blank" }
        require(project.location.city.isNotBlank()) { "City cannot be blank" }
        require(project.location.state.isNotBlank()) { "State cannot be blank" }
        
        // Validar fechas según el formato esperado en shared_domain
        validateDateFormat(project.timeline.startDate, "Start date")
        validateDateFormat(project.timeline.endDate, "End date")
        
        // Las demás validaciones ya están en los modelos del dominio compartido
    }
    
    private fun validateDateFormat(dateString: String, fieldName: String) {
        require(dateString.isNotBlank()) { "$fieldName cannot be blank" }
        // Validación básica para YYYY-MM-DD o ISO 8601
        require(
            dateString.matches(Regex("\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}:\\d{2}.*)?"))
        ) {
            "$fieldName must be in YYYY-MM-DD or ISO 8601 format"
        }
    }
    
    private fun getCurrentTimestamp(): String {
        val now = System.currentTimeMillis()
        // Formato ISO 8601 como esperado por shared_domain
        return "2024-01-15T${String.format("%02d", (now % 86400000) / 3600000)}:${String.format("%02d", (now % 3600000) / 60000)}:${String.format("%02d", (now % 60000) / 1000)}Z"
    }
    
    private fun initializeMockData() {
        val mockProjects = mapOf(
            "1" to Project(
                id = "1",
                name = "Proyecto 1",
                description = "Casa residencial moderna",
                status = ProjectStatus.DESIGN,
                location = ProjectLocation(
                    address = "Calle Ejemplo #123",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(2250000.0),
                client = Client(
                    id = "client1",
                    name = "Juan Pérez",
                    email = "juan@example.com"
                ),
                projectManager = ProjectManager(
                    id = "pm1",
                    name = "Arq. Steve",
                    title = "Arquitecto Senior"
                ),
                timeline = ProjectTimeline(
                    startDate = "2024-01-15",
                    endDate = "2024-12-15",
                    estimatedDuration = 300
                ),
                metadata = ProjectMetadata(
                    createdAt = "2024-01-01T00:00:00Z",
                    updatedAt = "2024-01-10T12:00:00Z"
                ),
                progress = 0.15
            ),
            "2" to Project(
                id = "2",
                name = "Proyecto 2",
                description = "Edificio comercial",
                status = ProjectStatus.CONSTRUCTION,
                location = ProjectLocation(
                    address = "Av. Principal #456",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(5500000.0),
                client = Client(
                    id = "client2",
                    name = "María González",
                    company = "Empresa ABC"
                ),
                projectManager = ProjectManager(
                    id = "pm2",
                    name = "Arq. Alex",
                    title = "Director de Proyecto"
                ),
                timeline = ProjectTimeline(
                    startDate = "2023-08-01",
                    endDate = "2024-08-01",
                    estimatedDuration = 365
                ),
                metadata = ProjectMetadata(
                    createdAt = "2023-07-15T00:00:00Z",
                    updatedAt = "2024-01-09T10:30:00Z"
                ),
                progress = 0.65
            ),
            "3" to Project(
                id = "3",
                name = "Proyecto 3",
                description = "Complejo habitacional",
                status = ProjectStatus.DELIVERY,
                location = ProjectLocation(
                    address = "Blvd. Norte #789",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(8750000.0),
                client = Client(
                    id = "client3",
                    name = "Carlos López",
                    company = "Constructora XYZ"
                ),
                projectManager = ProjectManager(
                    id = "pm3",
                    name = "Arq. Carlos",
                    title = "Gerente de Proyecto"
                ),
                timeline = ProjectTimeline(
                    startDate = "2022-06-01",
                    endDate = "2023-12-01",
                    estimatedDuration = 550
                ),
                metadata = ProjectMetadata(
                    createdAt = "2022-05-15T00:00:00Z",
                    updatedAt = "2023-12-05T16:30:00Z"
                ),
                progress = 0.95
            ),
            "4" to Project(
                id = "4",
                name = "Proyecto 4",
                description = "Centro comercial",
                status = ProjectStatus.PERMITS_REVIEW,
                location = ProjectLocation(
                    address = "Zona Industrial #101",
                    city = "Durango",
                    state = "Durango"
                ),
                budget = Money(12000000.0),
                client = Client(
                    id = "client4",
                    name = "Ana Martínez",
                    company = "Inversiones del Norte"
                ),
                projectManager = ProjectManager(
                    id = "pm4",
                    name = "Arq. María",
                    title = "Coordinadora General"
                ),
                timeline = ProjectTimeline(
                    startDate = "2023-10-01",
                    endDate = "2025-03-01",
                    estimatedDuration = 520
                ),
                metadata = ProjectMetadata(
                    createdAt = "2023-09-01T00:00:00Z",
                    updatedAt = "2024-01-05T09:15:00Z"
                ),
                progress = 0.25
            )
        )
        
        _projects.value = mockProjects
    }
    
    // Implementaciones requeridas por ProjectRepository interface
    // Schedule operations - delegadas a repositorio especializado
    override suspend fun getScheduleByProjectId(projectId: String): ProjectSchedule? = null
    override suspend fun createSchedule(schedule: ProjectSchedule): Result<ProjectSchedule> = 
        Result.failure(UnsupportedOperationException("Use CronogramaRepository for schedule operations"))
    override suspend fun updateSchedule(schedule: ProjectSchedule): Result<ProjectSchedule> = 
        Result.failure(UnsupportedOperationException("Use CronogramaRepository for schedule operations"))
    override suspend fun deleteSchedule(id: String): Result<Unit> = 
        Result.failure(UnsupportedOperationException("Use CronogramaRepository for schedule operations"))
    override suspend fun updateTaskProgress(taskId: String, progress: Double): Result<Unit> = 
        Result.failure(UnsupportedOperationException("Use CronogramaRepository for task operations"))
    
    // Blueprint operations - delegadas a repositorio especializado
    override suspend fun getBlueprintsByProjectId(projectId: String): Flow<List<Blueprint>> = flowOf(emptyList())
    override suspend fun getBlueprintById(id: String): Blueprint? = null
    override suspend fun createBlueprint(blueprint: Blueprint): Result<Blueprint> = 
        Result.failure(UnsupportedOperationException("Use BlueprintRepository for blueprint operations"))
    override suspend fun updateBlueprint(blueprint: Blueprint): Result<Blueprint> = 
        Result.failure(UnsupportedOperationException("Use BlueprintRepository for blueprint operations"))
    override suspend fun deleteBlueprint(id: String): Result<Unit> = 
        Result.failure(UnsupportedOperationException("Use BlueprintRepository for blueprint operations"))
    override suspend fun addBlueprintRevision(blueprintId: String, revision: BlueprintRevision): Result<Blueprint> = 
        Result.failure(UnsupportedOperationException("Use BlueprintRepository for blueprint operations"))
    
    // Evidence operations - delegadas a repositorio especializado
    override suspend fun getEvidenceByProjectId(projectId: String): Flow<List<Evidence>> = flowOf(emptyList())
    override suspend fun getEvidenceById(id: String): Evidence? = null
    override suspend fun createEvidence(evidence: Evidence): Result<Evidence> = 
        Result.failure(UnsupportedOperationException("Use EvidenciaRepository for evidence operations"))
    override suspend fun updateEvidence(evidence: Evidence): Result<Evidence> = 
        Result.failure(UnsupportedOperationException("Use EvidenciaRepository for evidence operations"))
    override suspend fun deleteEvidence(id: String): Result<Unit> = 
        Result.failure(UnsupportedOperationException("Use EvidenciaRepository for evidence operations"))
    override suspend fun getEvidenceByCategory(projectId: String, category: EvidenceCategory): Flow<List<Evidence>> = 
        flowOf(emptyList())
    override suspend fun getEvidenceByDateRange(projectId: String, startDate: String, endDate: String): Flow<List<Evidence>> = 
        flowOf(emptyList())
    
    // Gallery operations - delegadas a repositorio especializado
    override suspend fun getAllGalleryProjects(): Flow<List<GalleryProject>> = flowOf(emptyList())
    override suspend fun getGalleryProjectById(id: String): GalleryProject? = null

    override suspend fun createGalleryProject(project: GalleryProject): Result<GalleryProject> =
        Result.failure(UnsupportedOperationException("Use EvidenciaRepository for gallery operations"))
    override suspend fun updateGalleryProject(project: GalleryProject): Result<GalleryProject> = 
        Result.failure(UnsupportedOperationException("Use EvidenciaRepository for gallery operations"))
    override suspend fun deleteGalleryProject(id: String): Result<Unit> = 
        Result.failure(UnsupportedOperationException("Use EvidenciaRepository for gallery operations"))
    override suspend fun toggleGalleryProjectFavorite(id: String): Result<GalleryProject> = 
        Result.failure(UnsupportedOperationException("Use EvidenciaRepository for gallery operations"))
    override suspend fun searchGalleryProjects(query: String): Flow<List<GalleryProject>> = flowOf(emptyList())
    override suspend fun getGalleryProjectsByStyle(style: String): Flow<List<GalleryProject>> = flowOf(emptyList())
}
