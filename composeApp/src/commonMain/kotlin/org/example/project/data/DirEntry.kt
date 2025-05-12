package org.example.project.data

import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.audio_file
import minio_multiplatform.composeapp.generated.resources.doc_file
import minio_multiplatform.composeapp.generated.resources.excel_file
import minio_multiplatform.composeapp.generated.resources.folder
import minio_multiplatform.composeapp.generated.resources.image_file
import minio_multiplatform.composeapp.generated.resources.pdf_file
import minio_multiplatform.composeapp.generated.resources.ppt_file
import minio_multiplatform.composeapp.generated.resources.text_file
import minio_multiplatform.composeapp.generated.resources.unknown_file
import minio_multiplatform.composeapp.generated.resources.video_file
import minio_multiplatform.composeapp.generated.resources.zip_folder
import org.jetbrains.compose.resources.DrawableResource
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

data class DirEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0L,                    // File size in bytes
    val lastModified: Long = 0L,            // Epoch millis
    val permissions: FilePermissions = FilePermissions(),
    val fileType: FileType,
    val mime: String
)

data class FilePermissions(
    val readable: Boolean = true,
    val writable: Boolean = true,
    val executable: Boolean = false
)

fun File.toDirEntry(): DirEntry {
    return DirEntry(
        name = this.name,
        path = this.path,
        isDirectory = this.isDirectory,
        size = this.length(),
        lastModified = this.lastModified(),
        permissions = FilePermissions(
            readable = this.canRead(),
            writable = this.canWrite(),
            executable = this.canExecute()
        ),
        fileType = getFileType(isDirectory, this.extension),
        mime = this.extension
    )
}

enum class FileType(val icon: DrawableResource) {
    IMAGE(Res.drawable.image_file),
    VIDEO(Res.drawable.video_file),
    AUDIO(Res.drawable.audio_file),
    PDF(Res.drawable.pdf_file),
    DOCUMENT(Res.drawable.doc_file),
    POWERPOINT(Res.drawable.ppt_file),
    EXCEL(Res.drawable.excel_file),
    TEXT(Res.drawable.text_file),
    FOLDER(Res.drawable.folder),
    ZIP(Res.drawable.zip_folder),
    UNKNOWN(Res.drawable.unknown_file)
}

fun getFileType(isDirectory: Boolean, extension: String): FileType {
    if (isDirectory) return FileType.FOLDER

    return when (extension.lowercase()) {
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "tif", "svg", "ico", "heic" -> FileType.IMAGE
        "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "3gp" -> FileType.VIDEO
        "mp3", "wav", "aac", "flac", "ogg", "m4a", "mpeg" -> FileType.AUDIO
        "pdf" -> FileType.PDF
        "doc", "docx" -> FileType.DOCUMENT
        "xls", "xlsx" -> FileType.EXCEL
        "ppt", "pptx" -> FileType.POWERPOINT
        "txt", "csv", "rtf", "odt" -> FileType.TEXT
        "zip", "rar", "7z", "tar", "gz" -> FileType.ZIP
        else -> FileType.UNKNOWN
    }
}

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

expect fun openDocument(doc: DirEntry)

// List all files and folders in a given path
expect fun listDirEntries(path: String): List<DirEntry>
