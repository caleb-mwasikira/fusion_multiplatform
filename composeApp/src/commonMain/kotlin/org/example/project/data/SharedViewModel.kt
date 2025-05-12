package org.example.project.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel(private var workingDir: String? = null) {
    private val previousDirs = Stack<String>()
    private val nextDirs = Stack<String>()

    var isShowingHiddenFiles by mutableStateOf(false)
        private set
    var selectedFileFilter by mutableStateOf<FileType?>(null)
        private set

    private val _files = MutableStateFlow<List<DirEntry>>(emptyList())
    private val _filteredFiles = MutableStateFlow<List<DirEntry>>(emptyList())

    val files: StateFlow<List<DirEntry>>
        get() = _filteredFiles

    init {
        workingDir?.let {
            _files.value = listDirEntries(it)
        }
    }

    fun selectFileFilter(fileType: FileType?) {
        selectedFileFilter = fileType
        updateFilteredFiles()
    }

    fun toggleHiddenFiles() {
        isShowingHiddenFiles = !isShowingHiddenFiles
        updateFilteredFiles()
    }

    fun changeWorkingDir(newPath: String) {
        previousDirs.push(workingDir)
        workingDir = newPath
        _files.value = listDirEntries(newPath)
        updateFilteredFiles()
        println("Changed working dir; $newPath")
        println(_files.value)
    }

    fun gotoPreviousDir() {
        val previousDir = previousDirs.pop()
        previousDir?.let { prev ->
            nextDirs.push(workingDir)
            workingDir = prev
            _files.value = listDirEntries(prev)
            updateFilteredFiles()
            println("Moved to previous dir; $prev")
        }
    }

    fun gotoNextDir() {
        val nextDir = nextDirs.pop()
        nextDir?.let { next ->
            previousDirs.push(workingDir)
            workingDir = next
            _files.value = listDirEntries(next)
            updateFilteredFiles()
            println("Moved to next dir; $next")
        }
    }

    private fun updateFilteredFiles() {
        var filtered = _files.value
        selectedFileFilter?.let { filter ->
            filtered = filtered.filter { file -> file.fileType == filter }
        }
        if (!isShowingHiddenFiles) {
            filtered = filtered.filter { file -> file.name.firstOrNull() != '.' }
        }
        _filteredFiles.value = filtered
    }
}