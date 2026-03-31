// data/local/FavoritesDatabase.kt
package com.terista.manager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.terista.manager.domain.model.FileItem

@Database(
    entities = [FavoriteFile::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}