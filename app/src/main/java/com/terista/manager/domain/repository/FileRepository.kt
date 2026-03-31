package com.terista.manager.domain.repository

import com.terista.manager.domain.model.FileItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface FileRepository {
    fun getFiles(path: String, showHidden: Boolean): Flow<List<FileItem>>
}