package org.example.project

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.project.data.SharedViewModel
import javax.swing.JFileChooser
import javax.swing.JFrame

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1200.dp, 896.dp),
        isMinimized = false,
    )

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "MinIo",
    ) {
        val sharedViewModel = remember { SharedViewModel() }

        App(
            windowSizeClass = getWindowSizeClass(
                widthPx = windowState.size.width.value
            ),
            sharedViewModel = sharedViewModel,
            onUploadDirectory = {
                val frame = JFrame()
                frame.isAlwaysOnTop = true

                val chooser = JFileChooser().apply {
                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    dialogTitle = "Select Folder"
                }

                val result = chooser.showOpenDialog(frame)
                val selected = if (result == JFileChooser.APPROVE_OPTION) {
                    chooser.selectedFile.path
                } else {
                    null
                }

                selected?.let {
                    println("Selected folder; $it")
                    sharedViewModel.changeWorkingDir(it)
                }
            }
        )
    }
}