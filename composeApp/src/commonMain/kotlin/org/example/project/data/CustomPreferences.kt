package org.example.project.data

import kotlinx.serialization.Serializable
import java.io.File

const val WORKING_DIR = "WORKING_DIR"

@Serializable
data class Preferences(val data: MutableMap<String, String> = mutableMapOf())

// Use this to save user preferences and settings persistently across sessions
expect object CustomPreferences {
    val file: File
    val values: Preferences

    fun save()
    fun putString(key: String, value: String)
    fun getString(key: String): String?
}