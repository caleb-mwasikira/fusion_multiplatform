package org.example.project.widgets

import androidx.compose.runtime.Composable
import org.example.project.data.DirEntry
import org.example.project.data.SharedViewModel

@Composable
expect fun ContextMenu(
    expanded: Boolean,
    selectedFiles: List<DirEntry>,
    sharedViewModel: SharedViewModel,
    onDismissRequest: () -> Unit,
    onDeleteFiles: () -> Unit,
    onRenameFiles: () -> Unit,
)