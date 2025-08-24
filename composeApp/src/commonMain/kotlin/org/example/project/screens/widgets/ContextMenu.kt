package org.example.project.screens.widgets

import androidx.compose.runtime.Composable
import org.example.project.data.AppViewModel
import org.example.project.dto.DirEntry

@Composable
expect fun ContextMenu(
    expanded: Boolean,
    selectedFiles: List<DirEntry>,
    onDismissRequest: () -> Unit,
    onCreateNewFile: (isDirectory: Boolean) -> Unit,
    onCopy: (List<DirEntry>) -> Unit,
    onCut: (List<DirEntry>) -> Unit,
    onPaste: () -> Unit,
    onDeleteFiles: () -> Unit,
    onRenameFile: () -> Unit,
)