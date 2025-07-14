package com.example.shared_domain.repository

import com.example.shared_domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Project operations following Clean Architecture principles.
 * This interface defines the contract for data operations without depending on implementation details.
 */
interface ProjectRepository {
    
    // Project operations
    suspend fun getAllProjects(): Flow<List<Project>>
    suspend fun getProjectById(id: String): Project?
    suspend fun createProject(project: Project): Result<Project>
    suspend fun updateProject(project: Project): Result<Project>
    suspend fun deleteProject(id: String): Result<Unit>
    suspend fun searchProjects(query: String): Flow<List<Project>>
    suspend fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>>
    
    // Schedule operations
    suspend fun getScheduleByProjectId(projectId: String): ProjectSchedule?
    suspend fun createSchedule(schedule: ProjectSchedule): Result<ProjectSchedule>
    suspend fun updateSchedule(schedule: ProjectSchedule): Result<ProjectSchedule>
    suspend fun deleteSchedule(id: String): Result<Unit>
    suspend fun updateTaskProgress(taskId: String, progress: Double): Result<Unit>
    
    // Blueprint operations
    suspend fun getBlueprintsByProjectId(projectId: String): Flow<List<Blueprint>>
    suspend fun getBlueprintById(id: String): Blueprint?
    suspend fun createBlueprint(blueprint: Blueprint): Result<Blueprint>
    suspend fun updateBlueprint(blueprint: Blueprint): Result<Blueprint>
    suspend fun deleteBlueprint(id: String): Result<Unit>
    suspend fun addBlueprintRevision(blueprintId: String, revision: BlueprintRevision): Result<Blueprint>
    
    // Evidence operations
    suspend fun getEvidenceByProjectId(projectId: String): Flow<List<Evidence>>
    suspend fun getEvidenceById(id: String): Evidence?
    suspend fun createEvidence(evidence: Evidence): Result<Evidence>
    suspend fun updateEvidence(evidence: Evidence): Result<Evidence>
    suspend fun deleteEvidence(id: String): Result<Unit>
    suspend fun getEvidenceByCategory(projectId: String, category: EvidenceCategory): Flow<List<Evidence>>
    suspend fun getEvidenceByDateRange(projectId: String, startDate: String, endDate: String): Flow<List<Evidence>>
    
    // Gallery Project operations
    suspend fun getAllGalleryProjects(): Flow<List<GalleryProject>>
    suspend fun getGalleryProjectById(id: String): GalleryProject?
    suspend fun createGalleryProject(project: GalleryProject): Result<GalleryProject>
    suspend fun updateGalleryProject(project: GalleryProject): Result<GalleryProject>
    suspend fun deleteGalleryProject(id: String): Result<Unit>
    suspend fun toggleGalleryProjectFavorite(id: String): Result<GalleryProject>
    suspend fun searchGalleryProjects(query: String): Flow<List<GalleryProject>>
    suspend fun getGalleryProjectsByStyle(style: String): Flow<List<GalleryProject>>
}

/**
 * Use case interfaces for specific business operations
 */
interface ProjectUseCases {
    suspend fun createNewProject(
        name: String,
        description: String?,
        location: ProjectLocation,
        client: Client,
        projectManager: ProjectManager,
        timeline: ProjectTimeline
    ): Result<Project>
    
    suspend fun updateProjectProgress(projectId: String): Result<Double>
    suspend fun getProjectSummary(projectId: String): Result<ProjectSummary>
    suspend fun validateProjectData(project: Project): Result<Unit>
}

interface ScheduleUseCases {
    suspend fun createProjectSchedule(
        projectId: String,
        name: String,
        tasks: List<ScheduleTask>
    ): Result<ProjectSchedule>
    
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Unit>
    suspend fun calculateProjectProgress(scheduleId: String): Result<Double>
    suspend fun getOverdueTasks(scheduleId: String): Result<List<ScheduleTask>>
    suspend fun getUpcomingTasks(scheduleId: String, days: Int): Result<List<ScheduleTask>>
}

interface BlueprintUseCases {
    suspend fun uploadBlueprint(
        projectId: String,
        name: String,
        type: BlueprintType,
        fileData: ByteArray,
        fileName: String
    ): Result<Blueprint>
    
    suspend fun approveBlueprint(blueprintId: String, approvedBy: String): Result<Blueprint>
    suspend fun createRevision(
        blueprintId: String,
        description: String,
        changes: List<RevisionChange>,
        createdBy: String
    ): Result<BlueprintRevision>
}

interface EvidenceUseCases {
    suspend fun captureEvidence(
        projectId: String,
        title: String,
        type: EvidenceType,
        category: EvidenceCategory,
        mediaFiles: List<ByteArray>,
        location: EvidenceLocation?,
        capturedBy: String
    ): Result<Evidence>
    
    suspend fun generateProgressReport(projectId: String): Result<ProgressReport>
    suspend fun getEvidenceForTimeframe(projectId: String, timeframe: TimeFrame): Result<List<Evidence>>
}

/**
 * Data transfer objects for complex operations
 */
@kotlinx.serialization.Serializable
data class ProjectSummary(
    val project: Project,
    val totalTasks: Int,
    val completedTasks: Int,
    val totalBlueprints: Int,
    val approvedBlueprints: Int,
    val totalEvidence: Int,
    val recentEvidence: List<Evidence>,
    val upcomingMilestones: List<Milestone>,
    val overdueTasks: List<ScheduleTask>
)

@kotlinx.serialization.Serializable
data class ProgressReport(
    val projectId: String,
    val generatedAt: String,
    val overallProgress: Double,
    val scheduleProgress: Double,
    val evidenceCount: Int,
    val evidenceByCategory: Map<EvidenceCategory, Int>,
    val timelineStatus: TimelineStatus,
    val recommendations: List<String>
)

@kotlinx.serialization.Serializable
enum class TimelineStatus {
    ON_TRACK,
    BEHIND_SCHEDULE,
    AHEAD_OF_SCHEDULE,
    AT_RISK
}

@kotlinx.serialization.Serializable
enum class TimeFrame {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    LAST_30_DAYS,
    CUSTOM
}

@kotlinx.serialization.Serializable
data class GalleryProject(
    val id: String,
    val name: String,
    val description: String,
    val style: String,
    val location: String,
    val imageUrl: String,
    val rating: Double,
    val reviewCount: Int,
    val completedDate: String, // ISO 8601 format
    val architect: String,
    val area: String,
    val isFavorite: Boolean = false,
    val cardHeight: Int = 280,
    // Campos para integridad con otros modelos
    val projectId: String? = null, // Referencia al proyecto original
    val evidenceIds: List<String> = emptyList(), // Lista de evidencias asociadas
    val lastUpdated: String, // ISO 8601 format
    val category: EvidenceCategory? = null,
    val status: ProjectStatus = ProjectStatus.DELIVERY, // Proyectos en galería están completados
    val metadata: GalleryProjectMetadata? = null
) {
    init {
        require(id.isNotBlank()) { "Gallery project ID cannot be blank" }
        require(name.isNotBlank()) { "Gallery project name cannot be blank" }
        require(description.isNotBlank()) { "Gallery project description cannot be blank" }
        require(style.isNotBlank()) { "Gallery project style cannot be blank" }
        require(location.isNotBlank()) { "Gallery project location cannot be blank" }
        require(architect.isNotBlank()) { "Gallery project architect cannot be blank" }
        require(area.isNotBlank()) { "Gallery project area cannot be blank" }
        require(rating in 0.0..5.0) { "Rating must be between 0.0 and 5.0" }
        require(reviewCount >= 0) { "Review count cannot be negative" }
        require(cardHeight > 0) { "Card height must be positive" }
        require(completedDate.isNotBlank()) { "Completed date cannot be blank" }
        require(lastUpdated.isNotBlank()) { "Last updated cannot be blank" }
    }
    
    val isValidProject: Boolean get() = projectId?.isNotBlank() == true
    val hasEvidence: Boolean get() = evidenceIds.isNotEmpty()
    val averageRating: String get() = String.format("%.1f", rating)
}

@kotlinx.serialization.Serializable
data class GalleryProjectMetadata(
    val createdAt: String, // ISO 8601 format
    val updatedAt: String, // ISO 8601 format
    val version: Int = 1,
    val tags: List<String> = emptyList(),
    val viewCount: Int = 0,
    val shareCount: Int = 0,
    val featured: Boolean = false,
    val verified: Boolean = false
) {
    init {
        require(createdAt.isNotBlank()) { "Created at cannot be blank" }
        require(updatedAt.isNotBlank()) { "Updated at cannot be blank" }
        require(version > 0) { "Version must be positive" }
        require(viewCount >= 0) { "View count cannot be negative" }
        require(shareCount >= 0) { "Share count cannot be negative" }
    }
}
