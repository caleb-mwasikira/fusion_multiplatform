package org.example.project.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.data.SharedViewModel
import org.example.project.dto.DirEntry

@Composable
expect fun FilesGrid(
    sharedViewModel: SharedViewModel,
)

@Composable
expect fun FileItemCard(
    file: DirEntry,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
)
