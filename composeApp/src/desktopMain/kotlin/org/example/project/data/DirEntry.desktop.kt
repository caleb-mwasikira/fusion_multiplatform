package org.example.project.data

import java.awt.Desktop
import java.io.File

actual fun listDirEntries(path: String): List<DirEntry> {
    val file = File(path)
    if (!file.exists()) {
        println("file path $path does not exist")
        return emptyList()
    }

    if (file.isFile) {
        return listOf(file.toDirEntry())
    }

    val children = file.listFiles()?.toList() ?: emptyList()
    return children.map { child -> child.toDirEntry() }
}

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

