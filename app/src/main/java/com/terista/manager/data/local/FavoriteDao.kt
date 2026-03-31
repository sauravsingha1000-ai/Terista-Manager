// data/local/FavoriteDao.kt
package com.terista.manager.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY lastModified DESC")
    fun getAllFavorites(): Flow<List<FavoriteFile>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(file: FavoriteFile)
    
    @Delete
    suspend fun removeFavorite(file: FavoriteFile)
    
    @Query("SELECT * FROM favorites WHERE path = :path")
    suspend fun isFavorite(path: String): FavoriteFile?
}