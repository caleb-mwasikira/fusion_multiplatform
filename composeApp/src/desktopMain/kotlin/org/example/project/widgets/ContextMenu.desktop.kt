package org.example.project.widgets

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import kotlinx.coroutines.launch
import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.content_copy_24dp
import minio_multiplatform.composeapp.generated.resources.content_cut_24dp
import minio_multiplatform.composeapp.generated.resources.content_paste_24dp
import minio_multiplatform.composeapp.generated.resources.delete_24dp
import org.example.project.data.ClipboardAction
import org.example.project.data.DirEntry
import org.example.project.data.SharedViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun ContextMenu(
    popupPositionProvider: PopupPositionProvider?,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    selectedFiles: List<DirEntry>,
    sharedViewModel: SharedViewModel,
) {
    assert(popupPositionProvider != null) // Desktop ContextMenu MUST have a popupPositionProvider

    val scope = rememberCoroutineScope()

    Popup(
        popupPositionProvider = popupPositionProvider!!,
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
            ) {
                DropdownMenuItem(
                    text = { Text("Cut") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.content_cut_24dp),
                            contentDescription = "Cut"
                        )
                    },
                    onClick = {
                        sharedViewModel.addPasteBin(selectedFiles, ClipboardAction.Cut)
                        onDismissRequest()
                    },
                )
                DropdownMenuItem(
                    text = { Text("Copy") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.content_copy_24dp),
                            contentDescription = "Copy"
                        )
                    },
                    onClick = {
                        sharedViewModel.addPasteBin(selectedFiles, ClipboardAction.Copy)
                        onDismissRequest()
                    },
                )

                val okayToPaste by sharedViewModel.isOkayToPaste.collectAsState()

                if (okayToPaste) {
                    DropdownMenuItem(
                        text = { Text("Paste") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.content_paste_24dp),
                                contentDescription = "Paste"
                            )
                        },
                        onClick = {
                            scope.launch {
                                sharedViewModel.paste()
                            }
                            onDismissRequest()
                        },
                    )
                }

                DropdownMenuItem(
                    text = { Text("Delete") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.delete_24dp),
                            contentDescription = "Delete"
                        )
                    },
                    onClick = onDeleteRequest,
                )
            }
        }
    }
}