package org.example.project.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import org.example.project.MainActivity.Companion.TAG

private lateinit var applicationContext: Context

fun setApplicationContext(context: Context) {
    applicationContext = context
}

actual fun listDirEntries(path: String): List<DirEntry> {
    if (!::applicationContext.isInitialized) {
        Log.e(
            TAG,
            "Context is not initialized. Remember to call setApplicationContext() in MainActivity first."
        )
        return emptyList()
    }

    try {
        val treeUri = Uri.parse(path)
        val documentId = DocumentsContract.getTreeDocumentId(treeUri)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)

        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        )

        val dirEntries = mutableListOf<DirEntry>()
        val context = applicationContext
        val cursor = context.contentResolver.query(childrenUri, projection, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val childDocumentId = it.getString(0)
                val name = it.getString(1)
                val mime = it.getString(2)
                val size = it.getString(3)
                val lastModified = it.getString(4)

                val isDirectory = mime == DocumentsContract.Document.MIME_TYPE_DIR
                val extension = mime.substringAfterLast("/")
                val uri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childDocumentId)

                val dirEntry = DirEntry(
                    name = name,
                    path = uri.toString(),
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
        Log.e(TAG, "Error listing directory entries; ${e.message}")
        return emptyList()
    }
}

actual fun openDocument(doc: DirEntry) {
    if (!::applicationContext.isInitialized) {
        Log.e(
            TAG,
            "Context is not initialized. Remember to call setApplicationContext() in MainActivity first."
        )
        return
    }
    val context = applicationContext
    val uri = Uri.parse(doc.path)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndTypeAndNormalize(uri, doc.mime)
        addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
    }

    Log.d(TAG, "Opening file ${doc.path}. MIME type; ${doc.mime}")
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Log.e(TAG, "Error opening file; ${e.message}")
    }
}
