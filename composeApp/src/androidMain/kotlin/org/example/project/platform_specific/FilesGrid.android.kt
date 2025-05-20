package org.example.project.platform_specific

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.data.DirEntry
import org.example.project.data.openDocument

@Composable
actual fun FilesGrid(
    files: List<DirEntry>,
    changeWorkingDir: (String) -> Unit,
) {
    var displayContextMenu by remember { mutableStateOf(false) }
    val selectedFileIds = remember { mutableStateListOf<Int>() }

    LaunchedEffect(selectedFileIds.size) {
        displayContextMenu = selectedFileIds.isNotEmpty()
    }

    LaunchedEffect(files.hashCode()) {
        selectedFileIds.clear()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(148.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = files.size) { index ->
                val file = files[index]

                FileItemCard(
                    file = file,
                    isSelected = selectedFileIds.contains(index),
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            selectedFileIds.clear()
                            if (file.isDirectory) {
                                changeWorkingDir(file.path)
                            } else {
                                openDocument(file)
                            }
                        },
                        onLongClick = {
                            val alreadySelected = selectedFileIds.contains(index)
                            if (alreadySelected) {
                                selectedFileIds.remove(index)
                            } else {
                                selectedFileIds.add(index)
                            }
                        }
                    )
                ) {}
            }
        }

        // Custom Bottom Sheet; Regular ModalBottomSheet steals focus
        // away from the main content preventing users from selecting
        // more files
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            AnimatedVisibility(
                visible = displayContextMenu,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("This is a custom bottom sheet.")
                }
            }
        }
    }
}


