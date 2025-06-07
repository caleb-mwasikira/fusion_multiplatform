package org.example.project.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class Snapshot(
    val path: String = "",
    var lastModified: Long? = null,
)

/**
 * Captures the path and lastModified fields of a DirEntry
 */
fun DirEntry.getSnapshot(): Snapshot {
    return Snapshot(
        path = this.path,
        lastModified = this.lastModified,
    )
}

/**
 * Captures the names and lastModified timestamps of all files
 * within a directory
 */
suspend fun getSnapshotsOfAllFilesIn(path: String): List<Snapshot> = withContext(Dispatchers.IO) {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val fileChannel = Channel<DirEntry>()

    // Coroutine to generate dir entry files
    scope.launch {
        val stack = Stack<DirEntry>()
        var children = listDirEntries(path)
        stack.pushMany(*children.toTypedArray())

        while (stack.isNotEmpty()) {
            val child =
                stack.pop() ?: throw IllegalStateException("Stack isNotEmpty() function is broken")
            if (child.isDirectory()) {
                children = listDirEntries(child.path)
                stack.pushMany(*children.toTypedArray())
                continue
            }
            fileChannel.send(child)
        }
        fileChannel.close()
    }

    // Consume dir entry files from channel
    val resultsDeferred = scope.async {
        val results = mutableListOf<Snapshot>()

        for (file in fileChannel) {
            val snapshot = file.getSnapshot()
            results.add(snapshot)
        }
        results
    }
    resultsDeferred.await()
}