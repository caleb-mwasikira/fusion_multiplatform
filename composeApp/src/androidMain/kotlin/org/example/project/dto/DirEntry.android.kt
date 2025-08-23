package org.example.project.dto

import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import org.example.project.MainActivity
import org.example.project.MainActivity.Companion.TAG

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
        val context = MainActivity.instance.applicationContext
        context.contentResolver.query(
            childrenUri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val childDocumentId = cursor.getString(0)
                val name = cursor.getString(1)
                val mime = cursor.getString(2)
                val size = cursor.getString(3)
                val lastModified = cursor.getString(4)

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
                    size = size.toLongOrNull() ?: 0L,
                    lastModified = lastModified.toLongOrNull() ?: 0L,
                    permissions = FilePermissions(),
                    fileType = getFileType(isDirectory, extension),
                )
                dirEntries.add(dirEntry)
            }
        }
        return dirEntries
    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "Error listing directory entries in $path; ${e.message}")
        return emptyList()
    }
}

fun getDocumentUri(path: String): Uri? {
    return try {
        val documentUri = Uri.parse(path)
        val documentId = when {
            documentUri.path?.contains("/document/") == true -> {
                DocumentsContract.getDocumentId(documentUri)
            }

            documentUri.path?.contains("/tree/") == true -> {
                DocumentsContract.getTreeDocumentId(documentUri)
            }

            else -> {
                return null
            }
        }
        DocumentsContract.buildDocumentUriUsingTree(documentUri, documentId)
    } catch (e: Exception) {
        Log.e(TAG, "Error acquiring document uri; ${e.message}")
        null
    }
}

fun getParentUri(uri: Uri): Uri? {
    return try {
        val documentId = when {
            uri.path?.contains("/document/") == true -> {
                DocumentsContract.getDocumentId(uri)
            }

            uri.path?.contains("/tree/") == true -> {
                DocumentsContract.getTreeDocumentId(uri)
            }

            else -> {
                return null
            }
        }

        val parentDocumentId = documentId.substringBeforeLast("/")
        DocumentsContract.buildDocumentUriUsingTree(uri, parentDocumentId)

    } catch (e: Exception) {
        Log.e(TAG, "Error acquiring parent uri; ${e.message}")
        null
    }
}

actual fun getDirEntry(path: String): DirEntry? {
    return try {
        val uri = getDocumentUri(path) ?: throw IllegalArgumentException("Invalid URI: $path")
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        )

        var dirEntry: DirEntry? = null
        val context = MainActivity.instance.applicationContext
        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val name = cursor.getString(0)
                val mime = cursor.getString(1)
                val size = cursor.getString(2)
                val lastModified = cursor.getString(3)

                val isDirectory = mime == DocumentsContract.Document.MIME_TYPE_DIR
                val extension = mime.substringAfterLast("/")

                dirEntry = DirEntry(
                    name = name,
                    path = uri.toString(),
                    size = size.toLongOrNull() ?: 0L,
                    lastModified = lastModified.toLongOrNull() ?: 0L,
                    permissions = FilePermissions(),
                    fileType = getFileType(isDirectory, extension),
                )
            }
        }

        dirEntry

    } catch (e: Exception) {
        Log.e(TAG, "Error converting path into DirEntry; ${e.message}")
        null
    }
}