package org.example.project.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.PopupPositionProvider
import org.example.project.data.DirEntry
import org.example.project.data.SharedViewModel


@Composable
expect fun ContextMenu(
    popupPositionProvider: PopupPositionProvider? = null,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    selectedFiles: List<DirEntry>,
    sharedViewModel: SharedViewModel,
)