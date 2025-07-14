package com.example.shared_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val name: String,
    val description: String? = null,
    val status: ProjectStatus,
    val location: ProjectLocation,
    val budget: Money?,
    val client: Client,
    val projectManager: ProjectManager,
    val team: List<TeamMember> = emptyList(),
    val timeline: ProjectTimeline,
    val metadata: ProjectMetadata,
    val progress: Double = 0.0 // 0.0 - 1.0
) {
    init {
        require(name.isNotBlank()) { "Project name cannot be blank" }
        require(progress in 0.0..1.0) { "Progress must be between 0.0 and 1.0" }
    }
    
    val isCompleted: Boolean get() = status == ProjectStatus.DELIVERY
    val isActive: Boolean get() = status == ProjectStatus.CONSTRUCTION
    val progressPercentage: Int get() = (progress * 100).toInt()
}

@Serializable
enum class ProjectStatus {
    DESIGN,
    PERMITS_REVIEW,
    CONSTRUCTION,
    DELIVERY
}

@Serializable
data class ProjectLocation(
    val address: String,
    val city: String,
    val state: String,
    val country: String = "México",
    val postalCode: String? = null,
    val coordinates: Coordinates? = null
) {
    init {
        require(address.isNotBlank()) { "Address cannot be blank" }
        require(city.isNotBlank()) { "City cannot be blank" }
        require(state.isNotBlank()) { "State cannot be blank" }
    }
    
    val fullAddress: String get() = "$address, $city, $state, $country"
}

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
) {
    init {
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
    }
}

@Serializable
data class Money(
    val amount: Double,
    val currency: String = "MXN"
) {
    init {
        require(amount >= 0) { "Amount cannot be negative" }
        require(currency.isNotBlank()) { "Currency cannot be blank" }
    }
    
    fun formatted(): String = "$${String.format("%,.2f", amount)} $currency"
}

@Serializable
data class Client(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val company: String? = null
) {
    init {
        require(name.isNotBlank()) { "Client name cannot be blank" }
    }
}

@Serializable
data class ProjectManager(
    val id: String,
    val name: String,
    val title: String,
    val email: String? = null,
    val phone: String? = null
) {
    init {
        require(name.isNotBlank()) { "Project manager name cannot be blank" }
        require(title.isNotBlank()) { "Project manager title cannot be blank" }
    }
}

@Serializable
data class TeamMember(
    val id: String,
    val name: String,
    val role: String,
    val email: String? = null
) {
    init {
        require(name.isNotBlank()) { "Team member name cannot be blank" }
        require(role.isNotBlank()) { "Team member role cannot be blank" }
    }
}

@Serializable
data class ProjectTimeline(
    val startDate: String, // ISO 8601 format
    val endDate: String,
    val estimatedDuration: Int // days
) {
    init {
        require(startDate.isNotBlank()) { "Start date cannot be blank" }
        require(endDate.isNotBlank()) { "End date cannot be blank" }
        require(estimatedDuration > 0) { "Estimated duration must be positive" }
    }
}

@Serializable
data class ProjectMetadata(
    val createdAt: String, // ISO 8601 format
    val updatedAt: String,
    val version: Int = 1,
    val tags: List<String> = emptyList()
) {
    init {
        require(version > 0) { "Version must be positive" }
    }
}
