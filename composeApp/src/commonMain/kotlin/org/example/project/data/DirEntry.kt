package org.example.project.data

import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.doc_file
import minio_multiplatform.composeapp.generated.resources.folder
import minio_multiplatform.composeapp.generated.resources.image_file
import minio_multiplatform.composeapp.generated.resources.pdf_file
import minio_multiplatform.composeapp.generated.resources.unknown_file
import minio_multiplatform.composeapp.generated.resources.vlc
import minio_multiplatform.composeapp.generated.resources.zip_folder
import org.jetbrains.compose.resources.DrawableResource
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

data class DirEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0L,                    // File size in bytes
    val lastModified: Long = 0L,            // Epoch millis
    val mimeType: String? = null,           // e.g. "image/png", "application/pdf"
    val permissions: FilePermissions = FilePermissions(),
    val iconHint: String? = null            // e.g. "folder", "image", "pdf"
)

data class FilePermissions(
    val readable: Boolean = true,
    val writable: Boolean = true,
    val executable: Boolean = false
)

fun formatLastModified(lastModified: Long): String {
    val zone = TimeZone.getDefault()
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), zone.toZoneId())
    val formattedDate = dateTime.toString().substringBefore("T")
    return formattedDate
}

fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"

    val units = arrayOf("KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0

    while (size >= 1024 && unitIndex < units.lastIndex) {
        size /= 1024
        unitIndex++
    }

    return String.format("%.2f %s", size, units[unitIndex])
}

fun getFileIcon(iconHint: String): DrawableResource {
    return when (iconHint) {
        "folder" -> Res.drawable.folder
        "image" -> Res.drawable.image_file
        "video" -> Res.drawable.vlc
        "pdf" -> Res.drawable.pdf_file
        "text" -> Res.drawable.doc_file
        "zip" -> Res.drawable.zip_folder
        else -> Res.drawable.unknown_file
    }
}

val dirEntries = listOf(
    DirEntry(
        name = "Documents",
        path = "/home/user/Documents",
        isDirectory = true,
        lastModified = 1714891200000,
        iconHint = "folder"
    ),
    DirEntry(
        name = "Music",
        path = "/home/user/Music",
        isDirectory = true,
        lastModified = 1714791200000,
        iconHint = "folder"
    ),
    DirEntry(
        name = "photo.jpg",
        path = "/home/user/Pictures/photo.jpg",
        isDirectory = false,
        size = 2_048_576,
        lastModified = 1714888200000,
        mimeType = "image/jpeg",
        permissions = FilePermissions(readable = true, writable = true),
        iconHint = "image"
    ),
    DirEntry(
        name = "video.mp4",
        path = "/home/user/Videos/video.mp4",
        isDirectory = false,
        size = 52_428_800,
        lastModified = 1714880000000,
        mimeType = "video/mp4",
        permissions = FilePermissions(readable = true),
        iconHint = "video"
    ),
    DirEntry(
        name = "Resume.pdf",
        path = "/home/user/Documents/Resume.pdf",
        isDirectory = false,
        size = 384_000,
        lastModified = 1714870000000,
        mimeType = "application/pdf",
        iconHint = "pdf"
    ),
    DirEntry(
        name = "Projects",
        path = "/home/user/Projects",
        isDirectory = true,
        lastModified = 1714750000000,
        iconHint = "folder"
    ),
    DirEntry(
        name = "script.sh",
        path = "/home/user/Projects/script.sh",
        isDirectory = false,
        size = 2048,
        lastModified = 1714840000000,
        mimeType = "text/x-shellscript",
        permissions = FilePermissions(readable = true, writable = true, executable = true),
        iconHint = "text"
    ),
    DirEntry(
        name = "todo.txt",
        path = "/home/user/todo.txt",
        isDirectory = false,
        size = 1024,
        lastModified = 1714830000000,
        mimeType = "text/plain",
        iconHint = "text"
    ),
    DirEntry(
        name = "Archive.zip",
        path = "/home/user/Downloads/Archive.zip",
        isDirectory = false,
        size = 10_485_760,
        lastModified = 1714800000000,
        mimeType = "application/zip",
        iconHint = "zip"
    ),
    DirEntry(
        name = "Notes",
        path = "/home/user/Notes",
        isDirectory = true,
        lastModified = 1714700000000,
        iconHint = "folder"
    )
)
