package org.example.project.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.path.Path

class SharedViewModel {
    private val previousDirs = Stack<String>()
    private val nextDirs = Stack<String>()

    val hidingHiddenFiles = MutableStateFlow(true)
    val selectedFileFilter = MutableStateFlow<FileType?>(null)

    private var _syncedDirs = MutableStateFlow<List<String>>(emptyList())
    private var _workingDir = MutableStateFlow<String?>(null)
    private val _currentFiles = MutableStateFlow<List<DirEntry>>(emptyList())
    private val _filteredFiles = MutableStateFlow<List<DirEntry>>(emptyList())
    val files: StateFlow<List<DirEntry>>
        get() = _filteredFiles

    private val viewModelScope = CoroutineScope(Dispatchers.Default + Job())

    init {
        _syncedDirs.value = CustomPreferences.getTrackedDirs().toList()

        viewModelScope.launch {
            // the combine function runs every time any of the input flows
            // emits a new value.
            // when _workingDir or trackedDirs flows change recompute _currentFiles
            combine(
                _workingDir,
                _syncedDirs,
            ) { workingDir, trackedDirs ->
                val currentFiles = if (workingDir == null) {
                    listSyncedDirs(trackedDirs)
                } else {
                    listDirEntries(workingDir)
                }
                currentFiles
            }.collect {
                _currentFiles.value = it
            }
        }

        viewModelScope.launch {
            // when _currentFiles, hidingHiddenFiles or selectedFileFilter flows
            // changes, recompute _filteredFiles
            combine(
                _currentFiles,
                hidingHiddenFiles,
                selectedFileFilter,
            ) { files, hidingHiddenFiles, selectedFileFilter ->
                var filtered = files
                if (hidingHiddenFiles) {
                    filtered = filtered.filter { file -> !file.name.isHiddenFile() }
                }

                selectedFileFilter?.let {
                    filtered = filtered.filter { file -> file.fileType == it }
                }
                filtered
            }.collect {
                _filteredFiles.value = it
            }
        }
    }

    private fun listSyncedDirs(dirs: List<String>): List<DirEntry> {
        return dirs.mapNotNull {
            getDirEntry(it)
        }
    }

    fun selectFileFilter(fileType: FileType?) {
        selectedFileFilter.value = fileType
    }

    fun toggleHiddenFiles() {
        hidingHiddenFiles.value = !hidingHiddenFiles.value
    }

    fun trackNewDir(dir: String) {
        if (dir in _syncedDirs.value) {
            return
        }
        _syncedDirs.update { oldList ->
            oldList + dir
        }

        viewModelScope.launch {
            CustomPreferences.trackNewDir(dir)
        }
    }

    fun changeWorkingDir(dir: String) {
        val newRoot = Path(dir).root
        val oldRoot = Path(_workingDir.value ?: "").root

        previousDirs.push(_workingDir.value)
        if (oldRoot != newRoot) {
            nextDirs.clear()
        }

        _workingDir.value = dir
        println("Changed working dir to $dir")
    }

    fun gotoPreviousDir() {
        val previousDir = previousDirs.pop()
        nextDirs.push(_workingDir.value)
        _workingDir.value = previousDir
    }

    fun gotoNextDir() {
        val nextDir = nextDirs.pop()
        nextDir?.let {
            previousDirs.push(_workingDir.value)
            _workingDir.value = it
            println("Moved to next dir; $it")
        }
    }
}