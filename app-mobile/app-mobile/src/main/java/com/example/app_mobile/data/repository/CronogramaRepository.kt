package com.example.app_mobile.data.repository

import com.example.shared_domain.model.*
import com.example.shared_domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CronogramaRepository : ProjectRepository {
    
    // Estado centralizado para cronogramas
    private val _schedules = MutableStateFlow<Map<String, ProjectSchedule>>(emptyMap())
    val schedules: StateFlow<Map<String, ProjectSchedule>> = _schedules.asStateFlow()
    
    // Estado para tareas
    private val _tasks = MutableStateFlow<Map<String, ScheduleTask>>(emptyMap())
    val tasks: StateFlow<Map<String, ScheduleTask>> = _tasks.asStateFlow()
    
    // Estado para hitos
    private val _milestones = MutableStateFlow<Map<String, Milestone>>(emptyMap())
    val milestones: StateFlow<Map<String, Milestone>> = _milestones.asStateFlow()
    
    init {
        // Inicializar con datos mock
        initializeMockData()
    }
    
    // Project operations (implementación básica)
    override suspend fun getAllProjects(): Flow<List<Project>> = TODO("Not implemented in this scope")
    override suspend fun getProjectById(id: String): Project? = TODO("Not implemented in this scope")
    override suspend fun createProject(project: Project): Result<Project> = TODO("Not implemented in this scope")
    override suspend fun updateProject(project: Project): Result<Project> = TODO("Not implemented in this scope")
    override suspend fun deleteProject(id: String): Result<Unit> = TODO("Not implemented in this scope")
    override suspend fun searchProjects(query: String): Flow<List<Project>> = TODO("Not implemented in this scope")
    override suspend fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> = TODO("Not implemented in this scope")
    
    // Schedule operations
    override suspend fun getScheduleByProjectId(projectId: String): ProjectSchedule? {
        return _schedules.value.values.find { it.projectId == projectId }
    }
    
    override suspend fun createSchedule(schedule: ProjectSchedule): Result<ProjectSchedule> {
        return try {
            val updatedSchedules = _schedules.value.toMutableMap()
            updatedSchedules[schedule.id] = schedule
            _schedules.value = updatedSchedules
            
            // Actualizar también las tareas
            updateTasksFromSchedule(schedule)
            
            Result.success(schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSchedule(schedule: ProjectSchedule): Result<ProjectSchedule> {
        return try {
            val updatedSchedules = _schedules.value.toMutableMap()
            updatedSchedules[schedule.id] = schedule
            _schedules.value = updatedSchedules
            
            // Actualizar también las tareas
            updateTasksFromSchedule(schedule)
            
            Result.success(schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteSchedule(id: String): Result<Unit> {
        return try {
            val updatedSchedules = _schedules.value.toMutableMap()
            val schedule = updatedSchedules.remove(id)
            _schedules.value = updatedSchedules
            
            // Remover tareas asociadas
            if (schedule != null) {
                removeTasksFromSchedule(schedule)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTaskProgress(taskId: String, progress: Double): Result<Unit> {
        return try {
            updateTask(taskId) { task ->
                task.copy(progress = progress.coerceIn(0.0, 1.0))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Task operations
    suspend fun getTaskById(taskId: String): ScheduleTask? {
        return _tasks.value[taskId]
    }
    
    suspend fun createTask(task: ScheduleTask): Result<ScheduleTask> {
        return try {
            val updatedTasks = _tasks.value.toMutableMap()
            updatedTasks[task.id] = task
            _tasks.value = updatedTasks
            
            // Actualizar el cronograma correspondiente
            updateScheduleWithNewTask(task)
            
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTask(taskId: String, updateFn: (ScheduleTask) -> ScheduleTask): Result<ScheduleTask> {
        return try {
            val currentTask = _tasks.value[taskId] 
                ?: return Result.failure(IllegalArgumentException("Task not found"))
            
            val updatedTask = updateFn(currentTask)
            val updatedTasks = _tasks.value.toMutableMap()
            updatedTasks[taskId] = updatedTask
            _tasks.value = updatedTasks
            
            // Actualizar el cronograma correspondiente
            updateScheduleWithUpdatedTask(updatedTask)
            
            Result.success(updatedTask)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val task = _tasks.value[taskId]
            val updatedTasks = _tasks.value.toMutableMap()
            updatedTasks.remove(taskId)
            _tasks.value = updatedTasks
            
            // Actualizar el cronograma correspondiente
            if (task != null) {
                removeTaskFromSchedule(task)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun moveTaskToPhase(taskId: String, newPhase: ProjectStatus): Result<ScheduleTask> {
        return updateTask(taskId) { task ->
            task.copy(category = mapProjectStatusToTaskCategory(newPhase))
        }
    }
    
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<ScheduleTask> {
        return updateTask(taskId) { task ->
            val progress = when (status) {
                TaskStatus.COMPLETED -> 1.0
                TaskStatus.NOT_STARTED -> 0.0
                else -> task.progress
            }
            task.copy(status = status, progress = progress)
        }
    }
    
    // Milestone operations
    suspend fun getMilestoneById(milestoneId: String): Milestone? {
        return _milestones.value[milestoneId]
    }
    
    suspend fun updateMilestone(milestone: Milestone): Result<Milestone> {
        return try {
            val updatedMilestones = _milestones.value.toMutableMap()
            updatedMilestones[milestone.id] = milestone
            _milestones.value = updatedMilestones
            
            // Actualizar el cronograma correspondiente
            updateScheduleWithUpdatedMilestone(milestone)
            
            Result.success(milestone)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper methods
    private fun updateTasksFromSchedule(schedule: ProjectSchedule) {
        val updatedTasks = _tasks.value.toMutableMap()
        schedule.tasks.forEach { task ->
            updatedTasks[task.id] = task
        }
        _tasks.value = updatedTasks
        
        val updatedMilestones = _milestones.value.toMutableMap()
        schedule.milestones.forEach { milestone ->
            updatedMilestones[milestone.id] = milestone
        }
        _milestones.value = updatedMilestones
    }
    
    private fun removeTasksFromSchedule(schedule: ProjectSchedule) {
        val updatedTasks = _tasks.value.toMutableMap()
        schedule.tasks.forEach { task ->
            updatedTasks.remove(task.id)
        }
        _tasks.value = updatedTasks
        
        val updatedMilestones = _milestones.value.toMutableMap()
        schedule.milestones.forEach { milestone ->
            updatedMilestones.remove(milestone.id)
        }
        _milestones.value = updatedMilestones
    }
    
    private suspend fun updateScheduleWithNewTask(task: ScheduleTask) {
        val schedule = findScheduleContainingTask(task.id)
        if (schedule != null) {
            val updatedTasks = schedule.tasks + task
            val updatedSchedule = schedule.copy(tasks = updatedTasks)
            updateSchedule(updatedSchedule)
        }
    }
    
    private suspend fun updateScheduleWithUpdatedTask(task: ScheduleTask) {
        val schedule = findScheduleContainingTask(task.id)
        if (schedule != null) {
            val updatedTasks = schedule.tasks.map { if (it.id == task.id) task else it }
            val updatedSchedule = schedule.copy(
                tasks = updatedTasks,
                metadata = schedule.metadata.copy(
                    updatedAt = getCurrentTimestamp(),
                    version = schedule.metadata.version + 1
                )
            )
            updateSchedule(updatedSchedule)
        }
    }
    
    private suspend fun removeTaskFromSchedule(task: ScheduleTask) {
        val schedule = findScheduleContainingTask(task.id)
        if (schedule != null) {
            val updatedTasks = schedule.tasks.filter { it.id != task.id }
            val updatedSchedule = schedule.copy(tasks = updatedTasks)
            updateSchedule(updatedSchedule)
        }
    }
    
    private suspend fun updateScheduleWithUpdatedMilestone(milestone: Milestone) {
        val schedule = findScheduleContainingMilestone(milestone.id)
        if (schedule != null) {
            val updatedMilestones = schedule.milestones.map { 
                if (it.id == milestone.id) milestone else it 
            }
            val updatedSchedule = schedule.copy(milestones = updatedMilestones)
            updateSchedule(updatedSchedule)
        }
    }
    
    private fun findScheduleContainingTask(taskId: String): ProjectSchedule? {
        return _schedules.value.values.find { schedule ->
            schedule.tasks.any { it.id == taskId }
        }
    }
    
    private fun findScheduleContainingMilestone(milestoneId: String): ProjectSchedule? {
        return _schedules.value.values.find { schedule ->
            schedule.milestones.any { it.id == milestoneId }
        }
    }
    
    private fun getCurrentTimestamp(): String {
        return "2024-01-15T${System.currentTimeMillis() % 86400000 / 1000}:00Z"
    }
    
    private fun mapProjectStatusToTaskCategory(status: ProjectStatus): TaskCategory {
        return when (status) {
            ProjectStatus.DESIGN -> TaskCategory.DESIGN
            ProjectStatus.PERMITS_REVIEW -> TaskCategory.PERMITS
            ProjectStatus.CONSTRUCTION -> TaskCategory.CONSTRUCTION
            ProjectStatus.DELIVERY -> TaskCategory.DELIVERY
        }
    }
    
    private fun initializeMockData() {
        val mockSchedule = ProjectSchedule(
            id = "schedule_1",
            projectId = "project_1",
            name = "Cronograma Principal - Casa Residencial",
            description = "Cronograma completo del proyecto de casa residencial moderna",
            tasks = listOf(
                ScheduleTask(
                    id = "task_1",
                    name = "Diseño Arquitectónico",
                    description = "Elaboración de planos y diseños iniciales",
                    startDate = "2024-01-15",
                    endDate = "2024-02-15",
                    progress = 1.0,
                    status = TaskStatus.COMPLETED,
                    priority = TaskPriority.HIGH,
                    assignedTo = listOf("arq_steve"),
                    category = TaskCategory.DESIGN,
                    estimatedHours = 160,
                    actualHours = 150
                ),
                ScheduleTask(
                    id = "task_2",
                    name = "Trámites y Permisos",
                    description = "Gestión de licencias de construcción",
                    startDate = "2024-02-01",
                    endDate = "2024-03-15",
                    progress = 0.75,
                    status = TaskStatus.IN_PROGRESS,
                    priority = TaskPriority.CRITICAL,
                    assignedTo = listOf("ing_maria"),
                    category = TaskCategory.PERMITS,
                    estimatedHours = 80,
                    actualHours = 60
                ),
                ScheduleTask(
                    id = "task_3",
                    name = "Excavación y Cimentación",
                    description = "Preparación del terreno y bases",
                    startDate = "2024-03-01",
                    endDate = "2024-04-15",
                    progress = 0.25,
                    status = TaskStatus.NOT_STARTED,
                    priority = TaskPriority.HIGH,
                    assignedTo = listOf("ing_carlos"),
                    category = TaskCategory.CONSTRUCTION,
                    estimatedHours = 200,
                    actualHours = 50
                ),
                ScheduleTask(
                    id = "task_4",
                    name = "Estructura Principal",
                    description = "Construcción de columnas y vigas",
                    startDate = "2024-04-01",
                    endDate = "2024-06-30",
                    progress = 0.0,
                    status = TaskStatus.ON_HOLD,
                    priority = TaskPriority.HIGH,
                    assignedTo = listOf("ing_luis"),
                    category = TaskCategory.CONSTRUCTION,
                    estimatedHours = 400,
                    actualHours = 0
                )
            ),
            milestones = listOf(
                Milestone(
                    id = "milestone_1",
                    name = "Aprobación de Diseño",
                    description = "Diseño arquitectónico aprobado por el cliente",
                    targetDate = "2024-02-15",
                    isCompleted = true,
                    completedDate = "2024-02-14",
                    importance = MilestoneImportance.HIGH
                ),
                Milestone(
                    id = "milestone_2",
                    name = "Permisos Obtenidos",
                    description = "Todas las licencias y permisos aprobados",
                    targetDate = "2024-03-15",
                    isCompleted = false,
                    importance = MilestoneImportance.CRITICAL
                )
            ),
            metadata = ScheduleMetadata(
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-15T10:30:00Z",
                version = 1,
                lastModifiedBy = "arq_steve"
            )
        )
        
        _schedules.value = mapOf(mockSchedule.id to mockSchedule)
        updateTasksFromSchedule(mockSchedule)
    }
    
    // Blueprint operations (not implemented in this scope)
    override suspend fun getBlueprintsByProjectId(projectId: String): Flow<List<Blueprint>> = TODO()
    override suspend fun getBlueprintById(id: String): Blueprint? = TODO()
    override suspend fun createBlueprint(blueprint: Blueprint): Result<Blueprint> = TODO()
    override suspend fun updateBlueprint(blueprint: Blueprint): Result<Blueprint> = TODO()
    override suspend fun deleteBlueprint(id: String): Result<Unit> = TODO()
    override suspend fun addBlueprintRevision(blueprintId: String, revision: BlueprintRevision): Result<Blueprint> = TODO()
    
    // Evidence operations (not implemented in this scope)
    override suspend fun getEvidenceByProjectId(projectId: String): Flow<List<Evidence>> = TODO()
    override suspend fun getEvidenceById(id: String): Evidence? = TODO()
    override suspend fun createEvidence(evidence: Evidence): Result<Evidence> = TODO()
    override suspend fun updateEvidence(evidence: Evidence): Result<Evidence> = TODO()
    override suspend fun deleteEvidence(id: String): Result<Unit> = TODO()
    override suspend fun getEvidenceByCategory(projectId: String, category: EvidenceCategory): Flow<List<Evidence>> = TODO()
    override suspend fun getEvidenceByDateRange(projectId: String, startDate: String, endDate: String): Flow<List<Evidence>> = TODO()
}
