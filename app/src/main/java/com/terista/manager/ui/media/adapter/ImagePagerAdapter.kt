// ui/media/adapter/ImagePagerAdapter.kt
package com.terista.manager.ui.media.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terista.manager.databinding.ItemImageViewerBinding
import com.terista.manager.ui.media.subview.ZoomableImageView
import java.io.File

class ImagePagerAdapter(private val imagePaths: List<String>) : 
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageViewerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imagePaths[position])
    }
    
    override fun getItemCount() = imagePaths.size
    
    class ImageViewHolder(
        private val binding: ItemImageViewerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(imagePath: String) {
            val file = File(imagePath)
            if (file.exists()) {
                binding.zoomableImageView.setImageFile(file)
            }
        }
    }
}