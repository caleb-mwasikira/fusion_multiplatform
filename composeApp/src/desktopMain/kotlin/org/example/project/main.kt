package org.example.project

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import minio_multiplatform.composeapp.generated.resources.Res
import org.example.project.data.SharedViewModel
import org.example.project.widgets.getWindowSizeClass
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.swing.JFileChooser
import javax.swing.JFrame

fun selectDirectory(): String? {
    return try {
        val frame = JFrame()
        frame.isAlwaysOnTop = true

        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = "Select Folder"
        }

        val result = chooser.showOpenDialog(frame)
        return if (result == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.path
        } else {
            null
        }
    } catch (e: Exception) {
        println("Error selecting dir; ${e.message}")
        null
    }
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun runBundledFuseClient() {
    try {
        val binaryFile = File.createTempFile("fuse-client", null).apply {
            deleteOnExit()
            outputStream().use { outputStream ->
                val bytes = Res.readBytes("files/go-fuse-client")
                outputStream.write(bytes)
            }
            setExecutable(true)
        }
        val userHomeDir = System.getProperty("user.home")
        val command = listOf(
            binaryFile.absolutePath,
            "-realpath", "$userHomeDir/Public",
            "--debug"
        )
        println("[RUN] command ${command.joinToString(" ")}")

        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()
        Runtime.getRuntime().addShutdownHook(Thread {
            process.destroy()
        })

        // Launch a coroutine to read from stdout
        GlobalScope.launch {
            BufferedReader(InputStreamReader(process.inputStream)).useLines { lines ->
                lines.forEach {
                    println("[STDOUT]: $it")
                }
            }
        }

        // Launch a coroutine to read from stderr
        GlobalScope.launch {
            BufferedReader(InputStreamReader(process.errorStream)).useLines { lines ->
                lines.forEach {
                    System.err.println("[STDERR]: $it")
                }
            }
        }

        process.waitFor()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun main() = application {
    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        runBundledFuseClient()
    }

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
                val selected = selectDirectory()
                selected?.let {
                    sharedViewModel.trackNewDir(it)
                }
            }
        )
    }
}