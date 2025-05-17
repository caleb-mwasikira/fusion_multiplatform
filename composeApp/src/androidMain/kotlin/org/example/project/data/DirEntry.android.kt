package org.example.project.data

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import org.example.project.ContextProvider
import org.example.project.MainActivity.Companion.TAG
import java.io.File

actual fun openDocument(doc: DirEntry) {
    val context = ContextProvider.get()
    val uri = Uri.parse(doc.path)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndTypeAndNormalize(uri, doc.mime)
        addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_ACTIVITY_NEW_TASK // Calling startActivity() from outside an Activity requires this flag
        )
    }

    Log.d(TAG, "Opening file ${doc.path}. MIME type; ${doc.mime}")
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Log.e(TAG, "Error opening file; ${e.message}")
    }
}

actual fun listDirEntries(path: String, ignoreHiddenFiles: Boolean): List<DirEntry> {
    try {
        val parentUri = Uri.parse(path)
        val parentId = when {
            parentUri.path?.contains("/document/") == true -> {
                DocumentsContract.getDocumentId(parentUri)
            }

            parentUri.path?.contains("/tree/") == true -> {
                DocumentsContract.getTreeDocumentId(parentUri)
            }

            else -> {
                throw IllegalArgumentException("Invalid URI: $parentUri")
            }
        }

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(parentUri, parentId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        )

        val dirEntries = mutableListOf<DirEntry>()
        val context = ContextProvider.get()
        val cursor = context.contentResolver.query(childrenUri, projection, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val childDocumentId = it.getString(0)
                val name = it.getString(1)
                val mime = it.getString(2)
                val size = it.getString(3)
                val lastModified = it.getString(4)

                if (ignoreHiddenFiles && name.isHiddenFile()) {
                    println("Ignored hidden file $path")
                    continue
                }

                val isDirectory = mime == DocumentsContract.Document.MIME_TYPE_DIR
                val extension = mime.substringAfterLast("/")
                val childUri =
                    DocumentsContract.buildDocumentUriUsingTree(parentUri, childDocumentId)

                val dirEntry = DirEntry(
                    name = name,
                    path = childUri.toString(),
                    isDirectory = isDirectory,
                    size = size.toLongOrNull() ?: 0L,
                    lastModified = lastModified.toLongOrNull() ?: 0L,
                    permissions = FilePermissions(),
                    fileType = getFileType(isDirectory, extension),
                    mime = mime,
                )
                dirEntries.add(dirEntry)
            }
        }
        return dirEntries
    } catch (e: Exception) {
        Log.e(TAG, "Error listing directory entries in $path; ${e.message}")
        return emptyList()
    }
}

actual fun createNewFile(filename: String): File? {
    return try {
        val context = ContextProvider.get()
        val file = File(context.filesDir, filename)
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
        val parentUri = Uri.parse(path)
        val parentId = when {
            parentUri.path?.contains("/document/") == true -> {
                DocumentsContract.getDocumentId(parentUri)
            }

            parentUri.path?.contains("/tree/") == true -> {
                DocumentsContract.getTreeDocumentId(parentUri)
            }

            else -> {
                throw IllegalArgumentException("Invalid URI: $parentUri")
            }
        }
        val uri = DocumentsContract.buildDocumentUriUsingTree(parentUri, parentId)

        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        )
        val context = ContextProvider.get()
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        var dirEntry: DirEntry? = null
        cursor?.let {
            if (it.moveToFirst()) {
                val name = it.getString(0)
                val mime = it.getString(1)
                val size = it.getString(2)
                val lastModified = it.getString(3)

                val isDirectory = mime == DocumentsContract.Document.MIME_TYPE_DIR
                val extension = mime.substringAfterLast("/")

                dirEntry = DirEntry(
                    name = name,
                    path = uri.toString(),
                    isDirectory = isDirectory,
                    size = size.toLongOrNull() ?: 0L,
                    lastModified = lastModified.toLongOrNull() ?: 0L,
                    permissions = FilePermissions(),
                    fileType = getFileType(isDirectory, extension),
                    mime = mime,
                )
            }
        }
        dirEntry

    } catch (e: Exception) {
        println("Error getting file; ${e.message}")
        null
    }
}