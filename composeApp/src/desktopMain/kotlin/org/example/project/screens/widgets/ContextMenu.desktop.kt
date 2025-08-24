package org.example.project.screens.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.content_copy_24dp
import minio_multiplatform.composeapp.generated.resources.content_cut_24dp
import minio_multiplatform.composeapp.generated.resources.content_paste_24dp
import minio_multiplatform.composeapp.generated.resources.create_new_file_24dp
import minio_multiplatform.composeapp.generated.resources.create_new_folder_24dp
import minio_multiplatform.composeapp.generated.resources.delete_24dp
import org.example.project.data.AppViewModel
import org.example.project.data.ClipboardAction
import org.example.project.dto.DirEntry
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun ContextMenu(
    expanded: Boolean,
    selectedFiles: List<DirEntry>,
    onDismissRequest: () -> Unit,
    onCreateNewFile: (isDirectory: Boolean) -> Unit,
    onCopy: (List<DirEntry>) -> Unit,
    onCut: (List<DirEntry>) -> Unit,
    onPaste: () -> Unit,
    onDeleteFiles: () -> Unit,
    onRenameFile: () -> Unit,
) {
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
        ) {
            DropdownMenuItem(
                text = {
                    Text("Create New Folder", style = MaterialTheme.typography.titleMedium)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.create_new_folder_24dp),
                        contentDescription = "Create New Folder"
                    )
                },
                onClick = { onCreateNewFile(true) },
            )
            DropdownMenuItem(
                text = {
                    Text("Create New File", style = MaterialTheme.typography.titleMedium)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.create_new_file_24dp),
                        contentDescription = "Create New File"
                    )
                },
                onClick = { onCreateNewFile(false) },
            )
            DropdownMenuItem(
                text = {
                    Text("Cut", style = MaterialTheme.typography.titleMedium)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.content_cut_24dp),
                        contentDescription = "Cut"
                    )
                },
                onClick = {
                    onCut(selectedFiles)
                },
                enabled = selectedFiles.isNotEmpty()
            )
            DropdownMenuItem(
                text = {
                    Text("Copy", style = MaterialTheme.typography.titleMedium)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.content_copy_24dp),
                        contentDescription = "Copy"
                    )
                },
                onClick = {
                    onCopy(selectedFiles)
                },
                enabled = selectedFiles.isNotEmpty()
            )

            DropdownMenuItem(
                text = {
                    Text("Rename", style = MaterialTheme.typography.titleMedium)
                },
                leadingIcon = {},
                onClick = onRenameFile,
                enabled = selectedFiles.isNotEmpty()
            )

            DropdownMenuItem(
                text = {
                    Text("Paste", style = MaterialTheme.typography.titleMedium)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.content_paste_24dp),
                        contentDescription = "Paste"
                    )
                },
                onClick = {
                    onPaste()
                },
            )

            DropdownMenuItem(
                text = {
                    Text("Delete", style = MaterialTheme.typography.titleMedium)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.delete_24dp),
                        contentDescription = "Delete"
                    )
                },
                onClick = onDeleteFiles,
                enabled = selectedFiles.isNotEmpty()
            )
        }
    }
}