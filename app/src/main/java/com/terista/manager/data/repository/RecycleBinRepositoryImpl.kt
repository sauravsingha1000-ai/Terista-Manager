// data/repository/RecycleBinRepositoryImpl.kt
package com.terista.manager.data.repository

import com.terista.manager.data.local.RecycleDao
import com.terista.manager.data.local.RecycleItem
import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.repository.RecycleBinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class RecycleBinRepositoryImpl @Inject constructor(
    private val dao: RecycleDao
) : RecycleBinRepository {
    
    override fun getRecycleItems(): Flow<List<FileItem>> {
        return dao.getAll().map { items ->
            items.map { recycle ->
                FileItem(
                    name = recycle.name,
                    path = recycle.path,
                    isDirectory = false,
                    size = recycle.size,
                    lastModified = recycle.deletedTime,
                    isFavorite = false
                )
            }
        }
    }
    
    override suspend fun moveToRecycleBin(fileItem: FileItem) {
        val recycleItem = RecycleItem(
            path = "${getRecycleBinPath()}/${fileItem.name}_${System.currentTimeMillis()}",
            name = fileItem.name,
            originalPath = fileItem.path,
            size = fileItem.size
        )
        dao.moveToBin(recycleItem)
        File(fileItem.path).renameTo(File(recycleItem.path))
    }
    
    override suspend fun restore(path: String) {
        dao.restore(path)
        // Move file back to original location
    }
    
    private fun getRecycleBinPath(): String {
        val binDir = File(android.os.Environment.getExternalStorageDirectory(), ".terista_recycle")
        if (!binDir.exists()) binDir.mkdirs()
        return binDir.absolutePath
    }
}