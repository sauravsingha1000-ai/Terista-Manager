// domain/model/FileItem.kt
package com.terista.manager.domain.model

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String = "",
    val isFavorite: Boolean = false,
    val iconRes: Int = 0
)