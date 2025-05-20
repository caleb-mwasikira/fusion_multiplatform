package org.example.project.platform_specific

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.onClick
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.unit.dp
import org.example.project.SelectionMode
import org.example.project.data.DirEntry
import org.example.project.data.openDocument

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun FilesGrid(
    files: List<DirEntry>,
    changeWorkingDir: (String) -> Unit,
) {
    val selectedFileIds = remember { mutableStateListOf<Int>() }
    var selectionMode by remember { mutableStateOf<SelectionMode?>(null) }
    val focusRequester = remember { FocusRequester() }

    // Runs every time files changes
    LaunchedEffect(files) {
        focusRequester.requestFocus()
        selectedFileIds.clear()
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(148.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                val ctrlPressed = it.isCtrlPressed && it.type == KeyEventType.KeyDown
                val shiftPressed = it.isShiftPressed && it.type == KeyEventType.KeyDown
                val keyAPressed = it.key == Key.A && it.type == KeyEventType.KeyDown

                selectionMode = when {
                    ctrlPressed && keyAPressed -> SelectionMode.All
                    ctrlPressed -> SelectionMode.Single
                    shiftPressed -> SelectionMode.Range
                    else -> null
                }
                false
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { selectedFileIds.clear() }
            )
    ) {
        items(count = files.size) { index ->
            val file = files[index]
            var displayContextMenu by remember { mutableStateOf(false) }

            FileItemCard(
                file = file,
                isSelected = selectedFileIds.contains(index),
                modifier = Modifier.combinedClickable(
                    onClick = {
                        when (selectionMode) {
                            SelectionMode.Single -> {
                                val alreadySelected = selectedFileIds.contains(index)
                                if (alreadySelected) {
                                    selectedFileIds.remove(index)
                                } else {
                                    selectedFileIds.add(index)
                                }
                            }

                            SelectionMode.Range -> {
                                if (selectedFileIds.isEmpty()) {
                                    selectedFileIds.add(index)
                                    return@combinedClickable
                                }

                                val index1 = selectedFileIds.last()
                                val index2 = files.indexOf(file)
                                val start = if (index1 < index2) index1 else index2
                                val stop = if (index1 > index2) index1 else index2

                                selectedFileIds.clear()
                                for (i in start until stop + 1) {
                                    selectedFileIds.add(i)
                                }
                            }

                            SelectionMode.All -> {
                                selectedFileIds.addAll(files.indices)
                            }

                            null -> {
                                selectedFileIds.clear()
                                if (file.isDirectory) {
                                    changeWorkingDir(file.path)
                                } else {
                                    openDocument(file)
                                }
                            }
                        }
                    },
                )
                    .onClick(
                        // Right click to display context menu
                        matcher = PointerMatcher.mouse(PointerButton.Secondary),
                        onClick = {
                            displayContextMenu = true
                        }
                    ),
            ) {
                // Context Menu
                DropdownMenu(
                    expanded = displayContextMenu,
                    onDismissRequest = {
                        displayContextMenu = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text("Hello World") },
                        onClick = {},
                    )
                }
            }
        }
    }
}
