package com.terista.manager.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terista.manager.databinding.ItemFavoriteBinding
import com.terista.manager.domain.model.FileItem

class FavoritesAdapter(
    private val onClick: (FileItem) -> Unit
) : ListAdapter<FileItem, FavoritesAdapter.ViewHolder>(DiffCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: FileItem) {
            binding.apply {
                textName.text = item.name
                imageIcon.setImageResource(item.iconRes)
                imageFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                root.setOnClickListener { onClick(item) }
            }
        }
    }
    
    companion object DiffCallback : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(old: FileItem, new: FileItem) = old.path == new.path
        override fun areContentsTheSame(old: FileItem, new: FileItem) = old == new
    }
}