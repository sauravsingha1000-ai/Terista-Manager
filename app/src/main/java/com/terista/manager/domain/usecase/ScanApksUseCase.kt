package com.terista.manager.domain.usecase

import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScanApksUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    
    operator fun invoke(): Flow<List<FileItem>> {
        return fileRepository.getFiles("/sdcard", false)
            .map { files ->
                files.filter { file ->
                    file.mimeType == "application/vnd.android.package-archive" ||
                    file.name.lowercase().endsWith(".apk")
                }
            }
    }
}