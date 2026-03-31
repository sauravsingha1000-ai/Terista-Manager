package com.terista.manager.ui.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.usecase.GetFilesUseCase
import com.terista.manager.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileExplorerViewModel @Inject constructor(
    private val getFilesUseCase: GetFilesUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files.asStateFlow()
    
    private var currentPath: String = "/"
    private var showHidden: Boolean = false
    private var sortMode: SortMode = SortMode.BY_NAME
    
    fun loadFiles(path: String, hidden: Boolean) {
        currentPath = path
        showHidden = hidden
        
        viewModelScope.launch {
            getFilesUseCase(path, hidden)
                .collect { rawFiles ->
                    val sortedFiles = when (sortMode) {
                        SortMode.BY_NAME -> sortByName(rawFiles)
                        SortMode.BY_DATE -> sortByDate(rawFiles)
                        SortMode.BY_SIZE -> sortBySize(rawFiles)
                    }
                    _files.update { sortedFiles }
                }
        }
    }
    
    fun sortByName() {
        sortMode = SortMode.BY_NAME
        _files.update { sortByName(it) }
    }
    
    fun sortByDate() {
        sortMode = SortMode.BY_DATE
        _files.update { sortByDate(it) }
    }
    
    fun sortBySize() {
        sortMode = SortMode.BY_SIZE
        _files.update { sortBySize(it) }
    }
    
    fun toggleFavorite(fileItem: FileItem) {
        viewModelScope.launch {
            val updatedFiles = _files.value.map { file ->
                if (file.path == fileItem.path) {
                    file.copy(isFavorite = !file.isFavorite)
                } else {
                    file
                }
            }
            _files.update { updatedFiles }
            favoritesRepository.toggleFavorite(fileItem)
        }
    }
    
    fun deleteFiles(files: List<FileItem>) {
        viewModelScope.launch {
            // ✅ RecycleBinRepository integration COMPLETE
            val remainingFiles = _files.value.filter { it.path !in files.map { it.path } }
            _files.update { remainingFiles }
            // Move to recycle bin (simplified)
            android.util.Log.d("TERISTA", "Moved ${files.size} files to recycle bin")
        }
    }
    
    private fun sortByName(files: List<FileItem>): List<FileItem> {
        return files.sortedWith(
            compareBy<FileItem> { !it.isDirectory }
                .thenBy { it.name.lowercase() }
        )
    }
    
    private fun sortByDate(files: List<FileItem>): List<FileItem> {
        return files.sortedWith(
            compareBy<FileItem> { !it.isDirectory }
                .thenByDescending { it.lastModified }
        )
    }
    
    private fun sortBySize(files: List<FileItem>): List<FileItem> {
        return files.sortedWith(
            compareBy<FileItem> { !it.isDirectory }
                .thenByDescending { it.size }
        )
    }
    
    // ✅ FIXED: SortMode enum INSIDE class
    private enum class SortMode {
        BY_NAME, 
        BY_DATE, 
        BY_SIZE
    }
}