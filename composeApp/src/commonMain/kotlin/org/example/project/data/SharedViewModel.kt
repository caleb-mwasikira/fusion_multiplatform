package org.example.project.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SharedViewModel(private var workingDir: String? = null) {
    private val previousDirs = Stack<String>()
    private val nextDirs = Stack<String>()

    var hidingHiddenFiles by mutableStateOf(false)
        private set
    var selectedFileFilter by mutableStateOf<FileType?>(null)
        private set

    private val _allFiles = MutableStateFlow<List<DirEntry>>(emptyList())
    private val _filteredFiles = MutableStateFlow<List<DirEntry>>(emptyList())

    val files: StateFlow<List<DirEntry>>
        get() = _filteredFiles

    private val viewModelScope = CoroutineScope(Dispatchers.Default + Job())

    init {
        // the combine function runs every time any of the input flows
        // emits a new value.
        viewModelScope.launch {
            combine(
                _allFiles,
                snapshotFlow { hidingHiddenFiles },
                snapshotFlow { selectedFileFilter },
            ) { allFiles, hidingHiddenFiles, selectedFileFilter ->
                var filtered = allFiles
                if(hidingHiddenFiles) {
                    filtered = filtered.filter { file ->
                        val firstChar = file.name.firstOrNull() ?: return@filter false
                        firstChar != '.'
                    }
                }

                selectedFileFilter?.let {
                    filtered = filtered.filter { file -> file.fileType == it }
                }
                filtered
            }.collect {
                _filteredFiles.value = it
            }
        }

        workingDir?.let {
            println("Saved working directory; $it")
            _allFiles.value = listDirEntries(it)
        }
    }

    fun selectFileFilter(fileType: FileType?) {
        selectedFileFilter = fileType
    }

    fun toggleHiddenFiles() {
        hidingHiddenFiles = !hidingHiddenFiles
    }

    fun changeWorkingDir(newPath: String) {
        if (newPath == workingDir) {
            return
        }

        previousDirs.push(workingDir)
        workingDir = newPath
        _allFiles.value = listDirEntries(newPath)
        println("Changed working dir; $newPath")
        println(_allFiles.value)
    }

    fun gotoPreviousDir() {
        val previousDir = previousDirs.pop()
        previousDir?.let { prev ->
            nextDirs.push(workingDir)
            workingDir = prev
            _allFiles.value = listDirEntries(prev)
            println("Moved to previous dir; $prev")
        }
    }

    fun gotoNextDir() {
        val nextDir = nextDirs.pop()
        nextDir?.let { next ->
            previousDirs.push(workingDir)
            workingDir = next
            _allFiles.value = listDirEntries(next)
            println("Moved to next dir; $next")
        }
    }
}