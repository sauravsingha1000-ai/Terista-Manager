// domain/usecase/GetFilesUseCase.kt
package com.terista.manager.domain.usecase

import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(
    private val repository: FileRepository
) {
    operator fun invoke(path: String, showHidden: Boolean): Flow<List<FileItem>> {
        return repository.getFiles(path, showHidden)
    }
}