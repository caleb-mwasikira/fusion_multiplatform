package org.example.project.dto

import org.example.project.data.Stack
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

data class DirEntry(
    val name: String,
    val path: String,
    val size: Long = 0L,                    // File size in bytes
    val lastModified: Long = 0L,            // Epoch millis
    val permissions: FilePermissions = FilePermissions(),
    val fileType: FileType,
) {
    override fun toString(): String {
        return "DirEntry(name=${this.name}, " +
                "path=${this.path}, " +
                "size=${formatFileSize(this.size)}, " +
                "lastModified=${formatTimeMillis(this.lastModified)}, " +
                "permissions=${this.permissions}), " +
                "fileType=${this.fileType})"
    }
}

fun DirEntry.isFile(): Boolean {
    return this.fileType !in setOf(FileType.FOLDER, FileType.ZIP)
}

fun DirEntry.isDirectory(): Boolean {
    return this.fileType in setOf(FileType.FOLDER, FileType.ZIP)
}

fun formatTimeMillis(timeMillis: Long): String {
    val zone = TimeZone.getDefault()
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), zone.toZoneId())
    return dateTime.toString()
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

data class FilePermissions(
    val readable: Boolean = true,
    val writable: Boolean = true,
    val executable: Boolean = false
) {
    override fun toString(): String {
        val perm = StringBuilder()
        if (this.readable) perm.append("r") else perm.append("-")
        if (this.writable) perm.append("w") else perm.append("-")
        if (this.executable) perm.append("x") else perm.append("-")
        return perm.toString()
    }
}

fun File.toDirEntry(): DirEntry {
    return DirEntry(
        name = this.name,
        path = this.path,
        size = this.length(),
        lastModified = this.lastModified(),
        permissions = FilePermissions(
            readable = this.canRead(),
            writable = this.canWrite(),
            executable = this.canExecute()
        ),
        fileType = getFileType(isDirectory, this.extension),
    )
}

fun String.isHiddenFile(): Boolean {
    return this.firstOrNull()?.equals('.') ?: false
}

expect fun getDirEntry(path: String): DirEntry?

/**
 * List all files and folders in a given path
 */
expect fun listDirEntries(path: String, ignoreHiddenFiles: Boolean = true): List<DirEntry>

fun listDirEntriesRecursive(path: String, ignoreHiddenFiles: Boolean): List<DirEntry> {
    val allFiles = mutableListOf<DirEntry>()

    val stack = Stack<DirEntry>()
    var children = listDirEntries(path)
    allFiles.addAll(children.filter { it.isFile() })
    stack.pushMany(*children.filter { it.isDirectory() }.toTypedArray())

    while (stack.isNotEmpty()) {
        val child =
            stack.pop() ?: throw IllegalStateException("Stack isNotEmpty() function is broken")
        if (child.isDirectory()) {
            children = listDirEntries(child.path, ignoreHiddenFiles)
            allFiles.addAll(children.filter { it.isFile() })
            stack.pushMany(*children.filter { it.isDirectory() }.toTypedArray())
            continue
        }
        allFiles.add(child)
    }
    return allFiles
}