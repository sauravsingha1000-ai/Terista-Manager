// ui/tools/ApkExtractorActivity.kt
package com.terista.manager.ui.tools

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.terista.manager.databinding.ActivityApkExtractorBinding
import com.terista.manager.domain.model.FileItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

@AndroidEntryPoint
class ApkExtractorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityApkExtractorBinding
    private val viewModel: ApkExtractorViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApkExtractorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupObservers()
        viewModel.scanApks()
    }
    
    private fun setupRecyclerView() {
        binding.recyclerApks.layoutManager = LinearLayoutManager(this)
        binding.recyclerApks.adapter = ApkAdapter { apkFile ->
            viewModel.extractApk(apkFile)
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.apkFiles.collectLatest { files ->
                (binding.recyclerApks.adapter as ApkAdapter).submitList(files)
            }
        }
        
        lifecycleScope.launch {
            viewModel.extractProgress.collectLatest { progress ->
                binding.progressBar.visibility = if (progress > 0) View.VISIBLE else View.GONE
                binding.progressText.text = "Extracting... ${progress}%"
            }
        }
    }
}