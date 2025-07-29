package org.example.project.dto

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: Long,
    val name: String
)
