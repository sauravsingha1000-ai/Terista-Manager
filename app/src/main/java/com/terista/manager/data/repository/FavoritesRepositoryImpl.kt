// data/repository/FavoritesRepositoryImpl.kt
package com.terista.manager.data.repository

import com.terista.manager.data.local.FavoriteDao
import com.terista.manager.data.local.FavoriteFile
import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val dao: FavoriteDao
) : FavoritesRepository {
    
    override fun getFavorites(): Flow<List<FileItem>> {
        return dao.getAllFavorites().map { favorites ->
            favorites.map { favorite ->
                FileItem(
                    name = favorite.name,
                    path = favorite.path,
                    isDirectory = favorite.isDirectory,
                    size = favorite.size,
                    lastModified = favorite.lastModified,
                    isFavorite = true
                )
            }
        }
    }
    
    override suspend fun toggleFavorite(fileItem: FileItem) {
        val favorite = dao.isFavorite(fileItem.path)
        if (favorite != null) {
            dao.removeFavorite(favorite)
        } else {
            dao.addFavorite(FavoriteFile(
                path = fileItem.path,
                name = fileItem.name,
                isDirectory = fileItem.isDirectory,
                size = fileItem.size,
                lastModified = fileItem.lastModified
            ))
        }
    }
}