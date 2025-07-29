package org.example.project.dto

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

enum class FileType(val mime: String) {
    IMAGE("image/*"),
    VIDEO("video/*"),
    AUDIO("audio/*"),
    PDF("application/pdf"),
    DOCUMENT("application/msword"),
    POWERPOINT("application/vnd.ms-powerpoint"),
    EXCEL("application/vnd.ms-excel"),
    TEXT("text/plain"),
    FOLDER(""),
    ZIP("application/zip"),
    UNKNOWN("*/*")
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

fun getFileIcon(fileType: FileType): DrawableResource {
    return when (fileType) {
        FileType.IMAGE -> Res.drawable.image_file
        FileType.VIDEO -> Res.drawable.video_file
        FileType.AUDIO -> Res.drawable.audio_file
        FileType.PDF -> Res.drawable.pdf_file
        FileType.DOCUMENT -> Res.drawable.doc_file
        FileType.POWERPOINT -> Res.drawable.ppt_file
        FileType.EXCEL -> Res.drawable.excel_file
        FileType.TEXT -> Res.drawable.text_file
        FileType.FOLDER -> Res.drawable.folder
        FileType.ZIP -> Res.drawable.zip_folder
        FileType.UNKNOWN -> Res.drawable.unknown_file
    }
}