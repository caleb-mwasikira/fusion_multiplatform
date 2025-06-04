package org.example.project.data

import java.io.File

data class DirEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val isFile: Boolean = !isDirectory,
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
    allFiles.addAll(children.filter { it.isFile })
    stack.pushMany(*children.filter { it.isDirectory }.toTypedArray())

    while (stack.isNotEmpty()) {
        val child =
            stack.pop() ?: throw IllegalStateException("Stack isNotEmpty() function is broken")
        if (child.isDirectory) {
            children = listDirEntries(child.path, ignoreHiddenFiles)
            allFiles.addAll(children.filter { it.isFile })
            stack.pushMany(*children.filter { it.isDirectory }.toTypedArray())
            continue
        }
        allFiles.add(child)
    }
    return allFiles
}