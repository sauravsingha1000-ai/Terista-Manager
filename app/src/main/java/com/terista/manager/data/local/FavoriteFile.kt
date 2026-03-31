// data/local/FavoriteFile.kt
package com.terista.manager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.terista.manager.domain.model.FileItem

@Entity(tableName = "favorites")
data class FavoriteFile(
    @PrimaryKey val path: String,
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
)