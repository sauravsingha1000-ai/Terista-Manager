package com.terista.manager.ui.home

import android.content.Context
import android.os.StatFs
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import kotlin.math.roundToInt

@HiltViewModel
class HomeViewModel : ViewModel() {

    private val _storageInfo = MutableStateFlow(StorageInfo(0f, "0 GB", "128 GB"))
    val storageInfo: StateFlow<StorageInfo> = _storageInfo.asStateFlow()

    fun loadStorageInfo(context: Context) {
        viewModelScope.launch {
            try {
                val path = Environment.getExternalStorageDirectory().path
                val statFs = StatFs(path)
                
                val totalBytes = statFs.totalBytes.toFloat()
                val availableBytes = statFs.availableBytes.toFloat()
                val usedBytes = totalBytes - availableBytes
                
                val percentage = if (totalBytes > 0) {
                    ((usedBytes / totalBytes) * 100f).roundToInt().toFloat()
                } else 0f
                
                val totalGB = "%.0f GB".format(totalBytes / (1024f * 1024f * 1024f))
                val usedGB = "%.0f GB".format(usedBytes / (1024f * 1024f * 1024f))
                
                _storageInfo.value = StorageInfo(percentage, "$usedGB / $totalGB")
                Log.d("TERISTA", "Storage: $percentage% used ($usedGB / $totalGB)")
            } catch (e: Exception) {
                Log.e("TERISTA", "Storage calc error", e)
                _storageInfo.value = StorageInfo(0f, "Error", "Error")
            }
        }
    }
}

data class StorageInfo(
    val percentage: Float,
    val displayText: String
)