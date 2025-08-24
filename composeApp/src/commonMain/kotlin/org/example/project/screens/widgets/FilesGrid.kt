package org.example.project.screens.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.data.AppViewModel
import org.example.project.dto.DirEntry

@Composable
expect fun FilesGrid(
    appViewModel: AppViewModel,
)

@Composable
expect fun FileItemCard(
    file: DirEntry,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
)
