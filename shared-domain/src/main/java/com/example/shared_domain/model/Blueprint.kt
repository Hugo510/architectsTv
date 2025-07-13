package com.example.shared_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    val id: String,
    val projectId: String,
    val name: String,
    val description: String? = null,
    val type: BlueprintType,
    val category: BlueprintCategory,
    val file: BlueprintFile,
    val specifications: BlueprintSpecifications,
    val revisions: List<BlueprintRevision> = emptyList(),
    val status: BlueprintStatus = BlueprintStatus.DRAFT,
    val metadata: BlueprintMetadata
) {
    init {
        require(name.isNotBlank()) { "Blueprint name cannot be blank" }
        require(projectId.isNotBlank()) { "Project ID cannot be blank" }
    }
    
    val currentRevision: BlueprintRevision? get() = revisions.maxByOrNull { it.version }
    val isApproved: Boolean get() = status == BlueprintStatus.APPROVED
    val totalRevisions: Int get() = revisions.size
}

@Serializable
enum class BlueprintType {
    FLOOR_PLAN,
    ELEVATION,
    SECTION,
    DETAIL,
    SITE_PLAN,
    STRUCTURAL,
    ELECTRICAL,
    PLUMBING,
    HVAC,
    OTHER
}

@Serializable
enum class BlueprintCategory {
    ARCHITECTURAL,
    STRUCTURAL,
    ELECTRICAL,
    MECHANICAL,
    PLUMBING,
    LANDSCAPE,
    INTERIOR,
    OTHER
}

@Serializable
enum class BlueprintStatus {
    DRAFT,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    OBSOLETE
}

@Serializable
data class BlueprintFile(
    val fileName: String,
    val fileUrl: String,
    val thumbnailUrl: String? = null,
    val fileSize: Long, // bytes
    val format: FileFormat,
    val dimensions: FileDimensions? = null
) {
    init {
        require(fileName.isNotBlank()) { "File name cannot be blank" }
        require(fileUrl.isNotBlank()) { "File URL cannot be blank" }
        require(fileSize > 0) { "File size must be positive" }
    }
    
    val formattedSize: String get() {
        return when {
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            else -> "${fileSize / (1024 * 1024)} MB"
        }
    }
}

@Serializable
enum class FileFormat {
    PDF,
    DWG,
    DXF,
    PNG,
    JPG,
    JPEG,
    SVG,
    OTHER
}

@Serializable
data class FileDimensions(
    val width: Int,
    val height: Int,
    val unit: DimensionUnit = DimensionUnit.PIXELS
) {
    init {
        require(width > 0) { "Width must be positive" }
        require(height > 0) { "Height must be positive" }
    }
}

@Serializable
enum class DimensionUnit {
    PIXELS,
    MILLIMETERS,
    CENTIMETERS,
    METERS,
    INCHES,
    FEET
}

@Serializable
data class BlueprintSpecifications(
    val scale: String, // e.g., "1:100"
    val builtArea: AreaMeasurement? = null,
    val totalArea: AreaMeasurement? = null,
    val floors: Int? = null,
    val rooms: Int? = null,
    val bathrooms: Int? = null,
    val notes: String? = null
) {
    init {
        require(scale.isNotBlank()) { "Scale cannot be blank" }
        require(floors?.let { it > 0 } ?: true) { "Floors must be positive" }
        require(rooms?.let { it >= 0 } ?: true) { "Rooms cannot be negative" }
        require(bathrooms?.let { it >= 0 } ?: true) { "Bathrooms cannot be negative" }
    }
}

@Serializable
data class AreaMeasurement(
    val value: Double,
    val unit: AreaUnit = AreaUnit.SQUARE_METERS
) {
    init {
        require(value > 0) { "Area value must be positive" }
    }
    
    fun formatted(): String = "${String.format("%.2f", value)} ${unit.symbol}"
}

@Serializable
enum class AreaUnit(val symbol: String) {
    SQUARE_METERS("m²"),
    SQUARE_FEET("ft²"),
    SQUARE_CENTIMETERS("cm²")
}

@Serializable
data class BlueprintRevision(
    val id: String,
    val version: String,
    val description: String,
    val createdAt: String, // ISO 8601
    val createdBy: String,
    val changes: List<RevisionChange> = emptyList(),
    val approvedBy: String? = null,
    val approvedAt: String? = null
) {
    init {
        require(version.isNotBlank()) { "Version cannot be blank" }
        require(description.isNotBlank()) { "Description cannot be blank" }
        require(createdBy.isNotBlank()) { "Created by cannot be blank" }
    }
    
    val isApproved: Boolean get() = approvedBy != null && approvedAt != null
}

@Serializable
data class RevisionChange(
    val description: String,
    val type: ChangeType,
    val section: String? = null
) {
    init {
        require(description.isNotBlank()) { "Change description cannot be blank" }
    }
}

@Serializable
enum class ChangeType {
    ADDITION,
    MODIFICATION,
    DELETION,
    CORRECTION,
    OTHER
}

@Serializable
data class BlueprintMetadata(
    val createdAt: String,
    val updatedAt: String,
    val version: Int = 1,
    val tags: List<String> = emptyList(),
    val downloadCount: Int = 0,
    val lastViewed: String? = null
)
