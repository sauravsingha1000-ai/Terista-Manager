// data/repository/FileRepositoryImpl.kt
package com.terista.manager.data.repository

import android.util.Log
import com.terista.manager.core.utils.StorageUtils
import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor() : FileRepository {
    
    private companion object {
        const val TAG = "TERISTA"
    }
    
    override fun getFiles(path: String, showHidden: Boolean): Flow<List<FileItem>> = flow {
        try {
            Log.d(TAG, "Loading files from: $path")
            val dir = File(path)
            if (!dir.exists() || !dir.canRead()) {
                emit(emptyList())
                return@flow
            }
            
            val files = mutableListOf<FileItem>()
            dir.listFiles()?.forEach { file ->
                if (!showHidden && file.name.startsWith(".")) return@forEach
                
                val fileItem = FileItem(
                    name = file.name,
                    path = file.absolutePath,
                    isDirectory = file.isDirectory,
                    size = if (file.isDirectory) 0L else file.length(),
                    lastModified = file.lastModified(),
                    mimeType = getMimeType(file),
                    iconRes = getIconRes(file)
                )
                files.add(fileItem)
            }
            
            files.sortWith(compareBy<FileItem> { !it.isDirectory }
                .thenBy { it.name.lowercase(Locale.getDefault()) })
                
            emit(files)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading files", e)
            emit(emptyList())
        }
    }
    
    // Your code is perfect, just add these 2 lines for extra polish:

private fun getMimeType(file: File): String {
    return when (file.extension.lowercase()) {
        "jpg", "jpeg", "png", "gif", "webp" -> "image/*"  // ✅ Added webp
        "mp4", "avi", "mkv", "mov" -> "video/*"           // ✅ Added mov
        "mp3", "wav", "flac", "aac" -> "audio/*"          // ✅ Added aac  
        "pdf" -> "application/pdf"
        "apk" -> "application/vnd.android.package-archive"
        "zip", "rar" -> "application/zip"                  // ✅ Added archives
        else -> "*/*"
    }
}
    
    private fun getIconRes(file: File): Int {
        return when {
            file.isDirectory -> android.R.drawable.ic_menu_gallery  // Folder icon
            file.extension.equals("apk", true) -> android.R.drawable.sym_def_app_icon
            file.extension.equals("pdf", true) -> android.R.drawable.stat_notify_sync_noanim
            else -> android.R.drawable.ic_menu_camera  // Default file
        }
    }
}