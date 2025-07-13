package com.example.shared_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectSchedule(
    val id: String,
    val projectId: String,
    val name: String,
    val description: String? = null,
    val tasks: List<ScheduleTask>,
    val milestones: List<Milestone> = emptyList(),
    val metadata: ScheduleMetadata
) {
    init {
        require(name.isNotBlank()) { "Schedule name cannot be blank" }
        require(projectId.isNotBlank()) { "Project ID cannot be blank" }
    }
    
    val totalProgress: Double get() {
        if (tasks.isEmpty()) return 0.0
        return tasks.sumOf { it.progress } / tasks.size
    }
    
    val completedTasks: Int get() = tasks.count { it.isCompleted }
    val totalTasks: Int get() = tasks.size
}

@Serializable
data class ScheduleTask(
    val id: String,
    val name: String,
    val description: String? = null,
    val startDate: String, // ISO 8601
    val endDate: String,
    val progress: Double = 0.0, // 0.0 - 1.0
    val status: TaskStatus,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val assignedTo: List<String> = emptyList(), // Team member IDs
    val dependencies: List<String> = emptyList(), // Task IDs
    val category: TaskCategory,
    val estimatedHours: Int? = null,
    val actualHours: Int? = null
) {
    init {
        require(name.isNotBlank()) { "Task name cannot be blank" }
        require(progress in 0.0..1.0) { "Progress must be between 0.0 and 1.0" }
        require(estimatedHours?.let { it > 0 } ?: true) { "Estimated hours must be positive" }
        require(actualHours?.let { it >= 0 } ?: true) { "Actual hours cannot be negative" }
    }
    
    val isCompleted: Boolean get() = status == TaskStatus.COMPLETED
    val isOverdue: Boolean get() = status != TaskStatus.COMPLETED && 
            // Aquí iría lógica para comparar con fecha actual
            false // Placeholder
    val progressPercentage: Int get() = (progress * 100).toInt()
}

@Serializable
enum class TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
    CANCELLED
}

@Serializable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Serializable
enum class TaskCategory {
    DESIGN,
    PERMITS,
    CONSTRUCTION,
    DELIVERY,
    INSPECTION,
    DOCUMENTATION,
    OTHER
}

@Serializable
data class Milestone(
    val id: String,
    val name: String,
    val description: String? = null,
    val targetDate: String, // ISO 8601
    val isCompleted: Boolean = false,
    val completedDate: String? = null,
    val importance: MilestoneImportance = MilestoneImportance.MEDIUM
) {
    init {
        require(name.isNotBlank()) { "Milestone name cannot be blank" }
    }
}

@Serializable
enum class MilestoneImportance {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Serializable
data class ScheduleMetadata(
    val createdAt: String,
    val updatedAt: String,
    val version: Int = 1,
    val lastModifiedBy: String? = null
)
