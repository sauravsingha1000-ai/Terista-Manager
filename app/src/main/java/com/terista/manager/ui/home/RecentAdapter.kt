package com.terista.manager.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terista.manager.databinding.ItemRecentFileBinding
import com.terista.manager.domain.model.FileItem
import java.text.SimpleDateFormat
import java.util.*

class RecentAdapter(
    private val onClick: (FileItem) -> Unit
) : ListAdapter<FileItem, RecentAdapter.ViewHolder>(DiffCallback) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemRecentFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        @SuppressLint("SetTextI18n")
        fun bind(item: FileItem) {
            binding.apply {
                textName.text = item.name
                textTime.text = formatTime(item.lastModified)
                imageIcon.setImageResource(item.iconRes)
                root.setOnClickListener { onClick(item) }
            }
        }
        
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
    
    companion object DiffCallback : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(old: FileItem, new: FileItem) = old.path == new.path
        override fun areContentsTheSame(old: FileItem, new: FileItem) = old == new
    }
}