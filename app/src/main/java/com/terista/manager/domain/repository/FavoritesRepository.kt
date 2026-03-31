package com.terista.manager.domain.repository

import com.terista.manager.domain.model.FileItem
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<FileItem>>
    suspend fun toggleFavorite(fileItem: FileItem)
}
