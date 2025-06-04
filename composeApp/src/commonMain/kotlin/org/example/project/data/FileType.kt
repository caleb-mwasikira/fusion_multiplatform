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