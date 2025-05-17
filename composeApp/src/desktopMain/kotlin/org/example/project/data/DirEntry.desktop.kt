package org.example.project.data

import java.awt.Desktop
import java.io.File
import kotlin.io.path.Path

actual fun openDocument(doc: DirEntry) {
    val file = File(doc.path)
    if (!file.exists()) {
        println("File ${doc.path} not found")
        return
    }

    if (!Desktop.isDesktopSupported()) {
        println("Desktop not supported")
        return
    }

    val desktop = Desktop.getDesktop()
    try {
        desktop.open(file)
    } catch (e: Exception) {
        println("Error opening file; ${e.message}")
    }
}

actual fun listDirEntries(path: String, ignoreHiddenFiles: Boolean): List<DirEntry> {
    val file = File(path)
    if (!file.exists()) {
        println("File $path does not exist")
        return emptyList()
    }

    if (ignoreHiddenFiles && file.name.isHiddenFile()) {
        println("Ignored hidden file $path")
        return emptyList()
    }

    if (file.isFile) {
        return listOf(file.toDirEntry())
    }

    val children = file.listFiles()?.toList() ?: emptyList()
    return children.map { child -> child.toDirEntry() }
}

actual fun createNewFile(filename: String): File? {
    return try {
        val path = Path(System.getProperty("user.home"), ".minio", filename)
        val file = path.toFile()
        if (!file.exists()) {
            file.createNewFile()
        }
        file
    } catch (e: Exception) {
        println("Error creating new internal file; ${e.message}")
        null
    }
}

actual fun getDirEntry(path: String): DirEntry? {
    return try {
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        file.toDirEntry()

    } catch (e: Exception) {
        println("Error getting file; ${e.message}")
        null
    }
}