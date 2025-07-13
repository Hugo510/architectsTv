package com.example.shared_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Evidence(
    val id: String,
    val projectId: String,
    val title: String,
    val description: String? = null,
    val type: EvidenceType,
    val category: EvidenceCategory,
    val media: EvidenceMedia,
    val location: EvidenceLocation? = null,
    val capturedBy: String,
    val capturedAt: String, // ISO 8601
    val tags: List<String> = emptyList(),
    val status: EvidenceStatus = EvidenceStatus.ACTIVE,
    val metadata: EvidenceMetadata
) {
    init {
        require(title.isNotBlank()) { "Evidence title cannot be blank" }
        require(projectId.isNotBlank()) { "Project ID cannot be blank" }
        require(capturedBy.isNotBlank()) { "Captured by cannot be blank" }
    }
    
    val isImage: Boolean get() = type == EvidenceType.PHOTO
    val isVideo: Boolean get() = type == EvidenceType.VIDEO
    val isDocument: Boolean get() = type == EvidenceType.DOCUMENT
}

@Serializable
enum class EvidenceType {
    PHOTO,
    VIDEO,
    DOCUMENT,
    AUDIO,
    NOTE,
    OTHER
}

@Serializable
enum class EvidenceCategory {
    FOUNDATION,
    STRUCTURE,
    WALLS,
    ROOFING,
    ELECTRICAL,
    PLUMBING,
    HVAC,
    FINISHES,
    EXTERIOR,
    INTERIOR,
    SAFETY,
    QUALITY_CONTROL,
    PROGRESS,
    ISSUE,
    DELIVERY,
    OTHER
}

@Serializable
enum class EvidenceStatus {
    ACTIVE,
    ARCHIVED,
    DELETED
}

@Serializable
data class EvidenceMedia(
    val files: List<MediaFile>,
    val thumbnailUrl: String? = null
) {
    init {
        require(files.isNotEmpty()) { "Evidence must have at least one media file" }
    }
    
    val primaryFile: MediaFile get() = files.first()
    val totalSize: Long get() = files.sumOf { it.size }
    val fileCount: Int get() = files.size
}

@Serializable
data class MediaFile(
    val id: String,
    val fileName: String,
    val url: String,
    val size: Long, // bytes
    val mimeType: String,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null // seconds for video/audio
) {
    init {
        require(fileName.isNotBlank()) { "File name cannot be blank" }
        require(url.isNotBlank()) { "File URL cannot be blank" }
        require(size > 0) { "File size must be positive" }
        require(mimeType.isNotBlank()) { "MIME type cannot be blank" }
        require(width?.let { it > 0 } ?: true) { "Width must be positive" }
        require(height?.let { it > 0 } ?: true) { "Height must be positive" }
        require(duration?.let { it > 0 } ?: true) { "Duration must be positive" }
    }
    
    val isImage: Boolean get() = mimeType.startsWith("image/")
    val isVideo: Boolean get() = mimeType.startsWith("video/")
    val isAudio: Boolean get() = mimeType.startsWith("audio/")
    val isDocument: Boolean get() = mimeType.startsWith("application/") || mimeType.startsWith("text/")
    
    val formattedSize: String get() {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }
}

@Serializable
data class EvidenceLocation(
    val area: String, // e.g., "Kitchen", "Living Room", "Exterior Front"
    val floor: String? = null, // e.g., "Ground Floor", "First Floor"
    val section: String? = null, // e.g., "North Wall", "Corner A"
    val coordinates: Coordinates? = null,
    val reference: String? = null // Additional reference information
) {
    init {
        require(area.isNotBlank()) { "Area cannot be blank" }
    }
    
    val fullLocation: String get() {
        val parts = listOfNotNull(area, floor, section).filter { it.isNotBlank() }
        return parts.joinToString(" - ")
    }
}

@Serializable
data class EvidenceMetadata(
    val createdAt: String,
    val updatedAt: String,
    val version: Int = 1,
    val viewCount: Int = 0,
    val downloadCount: Int = 0,
    val lastViewed: String? = null,
    val weather: WeatherInfo? = null,
    val equipment: EquipmentInfo? = null
)

@Serializable
data class WeatherInfo(
    val temperature: Double? = null, // Celsius
    val humidity: Double? = null, // Percentage
    val conditions: String? = null // e.g., "Sunny", "Rainy", "Cloudy"
)

@Serializable
data class EquipmentInfo(
    val camera: String? = null,
    val lens: String? = null,
    val settings: String? = null
)
