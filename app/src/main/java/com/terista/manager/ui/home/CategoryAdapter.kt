// ui/home/CategoryAdapter.kt
package com.terista.manager.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.terista.manager.databinding.ItemCategoryBinding
import com.terista.manager.domain.model.CategoryItem

class CategoryAdapter(
    private val onClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    
    private val categories = listOf(
        CategoryItem("Images", android.R.drawable.ic_menu_camera, "image/*"),
        CategoryItem("Videos", android.R.drawable.ic_menu_camera, "video/*"),
        CategoryItem("Audio", android.R.drawable.ic_lock_idle_alarm, "audio/*"),
        CategoryItem("Documents", android.R.drawable.ic_dialog_info, "application/*"),
        CategoryItem("APKs", android.R.drawable.sym_def_app_icon, "application/vnd.android.package-archive"),
        CategoryItem("Archives", android.R.drawable.ic_menu_compass, "application/zip")
    )
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }
    
    override fun getItemCount() = categories.size
    
    inner class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryItem) {
            binding.imageIcon.setImageResource(item.iconRes)
            binding.textLabel.text = item.name
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}

data class CategoryItem(val name: String, val iconRes: Int, val mimeType: String)