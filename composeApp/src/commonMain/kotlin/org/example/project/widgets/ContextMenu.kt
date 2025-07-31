package org.example.project.widgets

import androidx.compose.runtime.Composable
import org.example.project.data.SharedViewModel
import org.example.project.dto.DirEntry

@Composable
expect fun ContextMenu(
    expanded: Boolean,
    selectedFiles: List<DirEntry>,
    sharedViewModel: SharedViewModel,
    onDismissRequest: () -> Unit,
    onDeleteFiles: () -> Unit,
    onRenameFiles: () -> Unit,
)