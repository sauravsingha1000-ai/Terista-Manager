// core/utils/StorageUtils.kt
package com.terista.manager.core.utils

import android.os.StatFs
import android.os.Environment
import android.util.Log
import java.io.File

object StorageUtils {
    private const val TAG = "TERISTA"
    
    fun getStorageInfo(): StorageInfo {
        try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong
            
            val total = totalBlocks * blockSize
            val available = availableBlocks * blockSize
            val used = total - available
            val percentage = if (total > 0) (used * 100 / total).toFloat() else 0f
            
            Log.d(TAG, "Storage - Total: ${formatBytes(total)}, Used: ${formatBytes(used)}")
            return StorageInfo(total, used, available, percentage)
        } catch (e: Exception) {
            Log.e(TAG, "Storage calculation error", e)
            return StorageInfo(0, 0, 0, 0f)
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        val kb = 1024.0
        val mb = kb * 1024
        val gb = mb * 1024
        
        return when {
            bytes >= gb -> String.format("%.1f GB", bytes / gb)
            bytes >= mb -> String.format("%.1f MB", bytes / mb)
            else -> String.format("%.1f KB", bytes / kb)
        }
    }
}

data class StorageInfo(
    val total: Long,
    val used: Long,
    val available: Long,
    val percentage: Float
)