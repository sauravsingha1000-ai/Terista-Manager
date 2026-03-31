// ui/tools/ApkExtractorViewModel.kt
package com.terista.manager.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.usecase.ScanApksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApkExtractorViewModel @Inject constructor(
    private val scanApksUseCase: ScanApksUseCase
) : ViewModel() {
    
    private val _apkFiles = MutableStateFlow<List<FileItem>>(emptyList())
    val apkFiles: StateFlow<List<FileItem>> = _apkFiles.asStateFlow()
    
    private val _extractProgress = MutableStateFlow(0)
    val extractProgress: StateFlow<Int> = _extractProgress.asStateFlow()
    
    fun scanApks() {
        viewModelScope.launch {
            scanApksUseCase().collect { apks ->
                _apkFiles.value = apks
            }
        }
    }
    
    fun extractApk(apkFile: FileItem) {
        viewModelScope.launch {
            // Simulate extraction progress
            for (i in 0..100 step 10) {
                _extractProgress.value = i
                kotlinx.coroutines.delay(200)
            }
            _extractProgress.value = 0
        }
    }
}