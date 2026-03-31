package com.terista.manager.ui.explorer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terista.manager.R
import com.terista.manager.databinding.ItemFileBinding
import com.terista.manager.domain.model.FileItem
import com.terista.manager.domain.repository.FavoritesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FileAdapter @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val onItemClick: (FileItem) -> Unit,
    private val onLongClick: (FileItem) -> Unit,
    private val onFavoriteClick: (FileItem) -> Unit
) : ListAdapter<FileItem, FileAdapter.FileViewHolder>(DiffCallback) {
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val selectedFiles = mutableSetOf<FileItem>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FileViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    override fun getItemCount(): Int = currentList.size
    
    fun toggleSelection(file: FileItem) {
        if (selectedFiles.contains(file)) {
            selectedFiles.remove(file)
        } else {
            selectedFiles.add(file)
        }
        notifyDataSetChanged()
    }
    
    fun getSelectedFiles(): Set<FileItem> = selectedFiles
    
    fun clearSelection() {
        selectedFiles.clear()
        notifyDataSetChanged()
    }
    
    inner class FileViewHolder(
        private val binding: ItemFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        @SuppressLint("SetTextI18n")
        fun bind(file: FileItem) {
            binding.apply {
                // File name & icon
                textName.text = file.name
                imageIcon.setImageResource(file.iconRes)
                
                // File type indicator
                if (file.isDirectory) {
                    textSize.text = "Folder"
                    textSize.setTextColor(ContextCompat.getColor(root.context, R.color.folder_blue))
                    imageIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.folder_blue))
                } else {
                    textSize.text = formatFileSize(file.size)
                    textSize.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                    imageIcon.setColorFilter(ContextCompat.getColor(root.context, R.color.icon_primary))
                }
                
                // Favorite button
                if (file.isFavorite) {
                    imageFavorite.setImageResource(R.drawable.ic_heart_filled)
                    imageFavorite.setColorFilter(ContextCompat.getColor(root.context, R.color.accent_red))
                } else {
                    imageFavorite.setImageResource(R.drawable.ic_heart_outline)
                    imageFavorite.setColorFilter(ContextCompat.getColor(root.context, R.color.text_secondary))
                }
                
                // Selection state
                if (selectedFiles.contains(file)) {
                    root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.selection_blue))
                } else {
                    root.setBackgroundColor(ContextCompat.getColor(root.context, android.R.color.transparent))
                }
                
                // Click listeners
                root.setOnClickListener { 
                    if (selectedFiles.isNotEmpty()) {
                        toggleSelection(file)
                    } else {
                        onItemClick(file)
                    }
                }
                
                root.setOnLongClickListener {
                    toggleSelection(file)
                    onLongClick(file)
                    true
                }
                
                imageFavorite.setOnClickListener {
                    coroutineScope.launch {
                        val updatedFile = file.copy(isFavorite = !file.isFavorite)
                        favoritesRepository.toggleFavorite(file)
                        onFavoriteClick(updatedFile)
                    }
                }
            }
        }
        
        private fun formatFileSize(size: Long): String {
            return when {
                size == 0L -> "0 B"
                size < 1024 -> "${size} B"
                size < 1024 * 1024 -> "${String.format("%.1f", size / 1024.0)} KB"
                size < 1024 * 1024 * 1024 -> "${String.format("%.1f", size / (1024.0 * 1024.0))} MB"
                else -> "${String.format("%.1f", size / (1024.0 * 1024.0 * 1024.0))} GB"
            }
        }
    }
    
    companion object DiffCallback : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem.path == newItem.path
        }
        
        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }
    }
}