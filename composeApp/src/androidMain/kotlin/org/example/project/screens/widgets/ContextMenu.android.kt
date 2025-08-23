package org.example.project.screens.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.content_copy_24dp
import minio_multiplatform.composeapp.generated.resources.content_cut_24dp
import minio_multiplatform.composeapp.generated.resources.content_paste_24dp
import minio_multiplatform.composeapp.generated.resources.create_new_file_24dp
import minio_multiplatform.composeapp.generated.resources.create_new_folder_24dp
import minio_multiplatform.composeapp.generated.resources.delete_24dp
import minio_multiplatform.composeapp.generated.resources.more_horiz_24dp
import org.example.project.data.ClipboardAction
import org.example.project.data.SharedViewModel
import org.example.project.dto.DirEntry
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun ContextMenu(
    expanded: Boolean,
    selectedFiles: List<DirEntry>,
    sharedViewModel: SharedViewModel,
    onDismissRequest: () -> Unit,
    onDeleteFiles: () -> Unit,
    onRenameFiles: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = expanded,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(72.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ContextMenuItem(
                        title = "Delete",
                        resource = Res.drawable.delete_24dp,
                        onClick = onDeleteFiles,
                        enabled = selectedFiles.isNotEmpty(),
                    )

                    val okayToPaste by sharedViewModel.isOkayToPaste.collectAsState()
                    ContextMenuItem(
                        title = "Paste",
                        resource = Res.drawable.content_paste_24dp,
                        onClick = {
                            scope.launch {
                                sharedViewModel.paste()
                            }
                            onDismissRequest()
                        },
                        enabled = okayToPaste
                    )
                    ContextMenuItem(
                        title = "Cut",
                        resource = Res.drawable.content_cut_24dp,
                        onClick = {
                            sharedViewModel.copyOrCut(selectedFiles, ClipboardAction.Cut)
                            onDismissRequest()
                        },
                        enabled = selectedFiles.isNotEmpty(),
                    )
                    ContextMenuItem(
                        title = "Copy",
                        resource = Res.drawable.content_copy_24dp,
                        onClick = {
                            sharedViewModel.copyOrCut(selectedFiles, ClipboardAction.Copy)
                            onDismissRequest()
                        },
                        enabled = selectedFiles.isNotEmpty(),
                    )

                    var openSecondaryContextMenu by remember { mutableStateOf(false) }
                    SecondaryContextMenu(
                        expanded = openSecondaryContextMenu,
                        numSelectedFiles = selectedFiles.size,
                        onDismissRequest = {
                            openSecondaryContextMenu = false
                            onDismissRequest()
                        },
                        onCreateNewFile = { isDirectory ->
                            scope.launch {
                                sharedViewModel.createNewFile(isDirectory)
                            }
                            onDismissRequest()
                        },
                        onRenameFiles = onRenameFiles,
                    )

                    ContextMenuItem(
                        title = "More",
                        resource = Res.drawable.more_horiz_24dp,
                        onClick = {
                            openSecondaryContextMenu = true
                        },
                        enabled = true
                    )
                }
            }

        }
    }
}

@Composable
fun ContextMenuItem(
    title: String,
    resource: DrawableResource,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = {
            if (enabled) {
                onClick()
            }
        },
    ) {
        val contentColor = if (enabled) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            Color.Gray
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(resource),
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}

@Composable
fun SecondaryContextMenu(
    expanded: Boolean,
    numSelectedFiles: Int,
    onDismissRequest: () -> Unit,
    onCreateNewFile: (isDirectory: Boolean) -> Unit,
    onRenameFiles: () -> Unit,
) {
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
                    contentDescription = "Create New Folder",
                    modifier = Modifier.size(28.dp)
                )
            },
            onClick = {
                onCreateNewFile(true)
            },
        )
        DropdownMenuItem(
            text = {
                Text("Create New File", style = MaterialTheme.typography.titleMedium)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.create_new_file_24dp),
                    contentDescription = "Create New File",
                    modifier = Modifier.size(28.dp)
                )
            },
            onClick = {
                onCreateNewFile(false)
            },
        )

        DropdownMenuItem(
            text = {
                Text("Rename", style = MaterialTheme.typography.titleMedium)
            },
            leadingIcon = {},
            onClick = onRenameFiles,
            enabled = numSelectedFiles > 0,
        )
    }
}