package org.example.project.data

import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

enum class ClipboardAction {
    Copy, Cut
}

data class FileError(
    val file: DirEntry? = null,
    val exception: Throwable?
)

expect suspend fun copyFiles(
    files: List<DirEntry>, destination: DirEntry, overwrite: Boolean
): List<FileError>

expect suspend fun moveFiles(
    files: List<DirEntry>, destination: DirEntry, overwrite: Boolean
): List<FileError>

expect suspend fun deleteFiles(files: List<DirEntry>): List<FileError>

expect fun createNewFile(filename: String): File?

/**
 * Opens a document for viewing within the running platform
 */
expect fun openDocument(doc: DirEntry)

fun formatLastModified(lastModified: Long): String {
    val zone = TimeZone.getDefault()
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), zone.toZoneId())
    val formattedDate = dateTime.toString().substringBefore("T")
    return formattedDate
}

fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0

    while (size >= 1024 && unitIndex < units.lastIndex) {
        size /= 1024
        unitIndex++
    }
    return String.format("%.2f %s", size, units[unitIndex])
}
