package com.example.shared_domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val assignedTo: String = "",
    val status: String = "DISEÑO", // DISEÑO, REVISION_PERMISOS, CONSTRUCCION, ENTREGA
    val progress: Int = 0, // 0-100
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    constructor() : this(
        id = "",
        name = "",
        description = "",
        assignedTo = "",
        status = "DISEÑO",
        progress = 0,
        createdAt = "",
        updatedAt = ""
    )
} 