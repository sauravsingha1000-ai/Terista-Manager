// ui/tools/StorageAnalyzerActivity.kt
package com.terista.manager.ui.tools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.terista.manager.core.utils.StorageUtils
import com.terista.manager.databinding.ActivityStorageAnalyzerBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StorageAnalyzerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStorageAnalyzerBinding
    private val viewModel: StorageAnalyzerViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageAnalyzerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        viewModel.analyzeStorage()
        observeData()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerStorage.layoutManager = LinearLayoutManager(this)
        binding.recyclerStorage.adapter = StorageCategoryAdapter()
    }
    
    private fun observeData() {
        lifecycleScope.launch {
            viewModel.storageCategories.collectLatest { categories ->
                (binding.recyclerStorage.adapter as StorageCategoryAdapter).submitList(categories)
                binding.storageView.setPercentage(viewModel.totalUsagePercentage)
            }
        }
    }
}