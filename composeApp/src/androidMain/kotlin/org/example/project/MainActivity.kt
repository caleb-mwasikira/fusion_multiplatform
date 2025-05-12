package org.example.project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.data.SharedViewModel
import org.example.project.data.listDirEntries
import org.example.project.data.setApplicationContext
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "MainActivity"
    }

    private val sharedViewModel = SharedViewModel()

    private val selectDirectoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val treeUri = result.data?.data
            treeUri?.let { uri ->
                // Persist permission across reboots
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                Log.d(TAG, "Selected folder: $uri")
                lifecycleScope.launch {
                    val children = listDirEntries(uri.toString())
                    children.forEach { child ->
                        if (!child.isDirectory) {
                            saveFileToInternalStorage(child.path)
                        }
                    }
                }
                sharedViewModel.changeWorkingDir(uri.toString())
            }
        }
    }

    private suspend fun saveFileToInternalStorage(path: String) {
        try {
            val uri = Uri.parse(path)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val fileName = getFileNameFromUri(uri) ?: run {
                    Log.e(TAG, "Error getting file name from: $uri")
                    return
                }
                val internalFile = File(filesDir, fileName)

                withContext(Dispatchers.IO) {
                    FileOutputStream(internalFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d(TAG, "File saved to internal storage: ${internalFile.absolutePath}")

            } ?: run {
                Log.e(TAG, "Error opening input stream for: $uri")
            }

        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception while accessing URI: $path. Missing permissions?", e)
            return
        } catch (e: Exception) {
            Log.e(TAG, "Error saving file $path to internal storage: ${e.message}")
            return
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var filename: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                filename = cursor.getString(displayNameIndex)
            }
        }
        if (filename == null && uri.path != null) {
            val lastSegment = uri.lastPathSegment
            if (lastSegment != null) {
                filename = lastSegment
            }
        }
        return filename
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            SideEffect { // Runs this once per composition
                setApplicationContext(context)
            }

            App(
                windowSizeClass = getWindowSizeClass(),
                sharedViewModel = sharedViewModel,
                onUploadDirectory = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                        )
                    }

                    selectDirectoryLauncher.launch(intent)
                }
            )
        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App(
        windowSizeClass = getWindowSizeClass(),
        sharedViewModel = SharedViewModel(),
        onUploadDirectory = {},
    )
}