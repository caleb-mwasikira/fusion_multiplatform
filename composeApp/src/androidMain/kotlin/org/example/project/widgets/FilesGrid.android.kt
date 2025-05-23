package org.example.project.widgets

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.data.DirEntry
import org.example.project.data.SharedViewModel
import org.example.project.data.openDocument

@Composable
actual fun FilesGrid(
    sharedViewModel: SharedViewModel,
) {
    val files by sharedViewModel.files.collectAsState()
    var inSelectMode by remember { mutableStateOf(false) }
    val selectedFiles = remember { mutableStateListOf<DirEntry>() }

    LaunchedEffect(selectedFiles.size) {
        inSelectMode = selectedFiles.isNotEmpty()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {
                    inSelectMode = false
                },
                onLongClick = {
                    inSelectMode = !inSelectMode
                }
            )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(148.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = files.size) { index ->
                val file = files[index]
                val alreadySelected = selectedFiles.contains(file)

                FileItemCard(
                    file = file,
                    isSelected = selectedFiles.contains(file),
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            if (inSelectMode) {
                                if (alreadySelected) {
                                    selectedFiles.remove(file)
                                } else {
                                    selectedFiles.add(file)
                                }
                                return@combinedClickable
                            }

                            selectedFiles.clear()
                            if (file.isDirectory) {
                                sharedViewModel.changeWorkingDir(file)
                            } else {
                                openDocument(file)
                            }
                        },
                        onLongClick = {
                            if (alreadySelected) {
                                selectedFiles.remove(file)
                            } else {
                                selectedFiles.add(file)
                            }
                        }
                    )
                )
            }
        }

        var inDeleteMode by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        if (inDeleteMode) {
            ConfirmationDialog(
                title = "Are you sure you want to delete this file?",
                subtitle = "This action is irreversible",
                onDismissRequest = {
                    inDeleteMode = false
                    selectedFiles.clear()
                },
                onDecline = {
                    inDeleteMode = false
                    selectedFiles.clear()
                },
                onAccept = {
                    scope.launch {
                        sharedViewModel.delete(selectedFiles)
                        selectedFiles.clear()
                    }
                    inDeleteMode = false
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            ContextMenu(
                expanded = inSelectMode,
                onDismissRequest = {
                    inSelectMode = false
                },
                onDeleteRequest = {
                    inDeleteMode = true
                },
                selectedFiles = selectedFiles,
                sharedViewModel = sharedViewModel,
            )
        }
    }
}


