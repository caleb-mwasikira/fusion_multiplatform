package org.example.project.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import kotlin.system.measureTimeMillis

@Serializable
data class FileSnapshot(
    val path: String = "",
    var lastModified: Long? = null,
)

@Serializable
data class Preferences(
    val trackedDirFiles: MutableMap<String, List<FileSnapshot>> = mutableMapOf(),
)

fun Preferences.getTrackedDirs(): Set<String> = this.trackedDirFiles.keys

// Use this to save user preferences and settings persistently across sessions
object CustomPreferences {
    private val json = Json { ignoreUnknownKeys = true }
    private var _file: File? = null
    val file: File
        get() {
            if (_file != null) {
                return _file!!
            }

            val newFile = createNewFile("synced_files.json")
                ?: throw IOException("Error creating internal file")
            _file = newFile
            return newFile
        }

    private var _prefs: Preferences? = null
    val prefs: Preferences
        get() {
            if (_prefs != null) {
                return _prefs!!
            }

            val data = file.readText()
            if (data.isEmpty()) {
                _prefs = Preferences()
                save()
                return _prefs!!
            }

            _prefs = json.decodeFromString<Preferences>(data)
            return _prefs!!
        }

    private fun save() {
        file.writeText(json.encodeToString(prefs))
    }

    fun getTrackedDirs(): Set<String> {
        return prefs.getTrackedDirs().also {
            println("Loaded synced dirs from CustomPreferences; $it")
        }
    }

    suspend fun trackNewDir(dir: String): Boolean = withContext(Dispatchers.IO) {
        val alreadyExists = prefs.getTrackedDirs().contains(dir)
        if (alreadyExists) {
            return@withContext false
        }

        return@withContext try {
            println("Taking dir snapshot of dir $dir...")
            val elapsed = measureTimeMillis {
                val snapshotFiles = takeDirSnapshot(dir)
                prefs.trackedDirFiles[dir] = snapshotFiles
            }
            println("Done taking dir snapshot in $elapsed ms")
            save()
            true

        } catch (e: Exception) {
            println("Error taking dir snapshot; ${e.message}")
            false
        }
    }

    fun removeTrackedDir(dir: String): Boolean {
        prefs.trackedDirFiles.remove(dir)
        save()
        return true
    }

    fun getTrackedDirFiles(dir: String): List<FileSnapshot>? {
        return prefs.trackedDirFiles[dir]
    }

}