package org.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: Long,
    val name: String
)
