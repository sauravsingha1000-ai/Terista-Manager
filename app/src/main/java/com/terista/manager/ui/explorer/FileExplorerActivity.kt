package com.terista.manager.ui.explorer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.terista.manager.R
import com.terista.manager.core.utils.StorageUtils.TAG
import com.terista.manager.databinding.ActivityFileExplorerBinding
import com.terista.manager.domain.model.FileItem
import com.terista.manager.ui.media.ImageViewerActivity
import com.terista.manager.ui.media.MusicPlayerActivity
import com.terista.manager.ui.media.VideoPlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FileExplorerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFileExplorerBinding
    private val viewModel: FileExplorerViewModel by viewModels()
    private lateinit var adapter: FileAdapter
    
    private var currentPath = Environment.getExternalStorageDirectory().absolutePath
    private var showHiddenFiles = false
    private var isMultiSelectMode = false
    private var isGridView = false
    
    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadFiles()
        } else {
            checkManageExternalStorage()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileExplorerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        Log.d(TAG, "FileExplorerActivity started at path: $currentPath")
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        checkPermissions()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener { navigateUp() }
        updateToolbarTitle()
    }
    
    private fun setupRecyclerView() {
        adapter = FileAdapter(
            onItemClick = { fileItem -> handleFileClick(fileItem) },
            onLongClick = { fileItem -> 
                isMultiSelectMode = true
                adapter.toggleSelection(fileItem)
                updateMultiSelectUI()
            },
            onFavoriteClick = { fileItem ->
                // Update favorite state in list
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Favorite ${if (fileItem.isFavorite) "added" else "removed"}", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.recyclerView.adapter = adapter
        toggleViewType(isGridView)
    }
    
    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.files.collectLatest { files ->
                adapter.submitList(files)
                binding.emptyView.visibility = if (files.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
    
    private fun toggleViewType(useGrid: Boolean) {
        isGridView = useGrid
        binding.recyclerView.layoutManager = if (useGrid) {
            GridLayoutManager(this, 3)
        } else {
            GridLayoutManager(this, 1)
        }
    }
    
    private fun updateToolbarTitle() {
        binding.toolbar.title = if (currentPath == Environment.getExternalStorageDirectory().absolutePath) {
            "Internal Storage"
        } else {
            File(currentPath).name
        }
    }
    
    private fun updateMultiSelectUI() {
        val count = adapter.getSelectedFiles().size
        if (count > 0) {
            binding.toolbar.title = "$count selected"
        } else {
            updateToolbarTitle()
        }
        invalidateOptionsMenu()
    }
    
    private fun handleFileClick(fileItem: FileItem) {
        if (isMultiSelectMode) {
            adapter.toggleSelection(fileItem)
            updateMultiSelectUI()
        } else {
            openFileOrFolder(fileItem)
        }
    }
    
    private fun openFileOrFolder(fileItem: FileItem) {
        if (fileItem.isDirectory) {
            navigateTo(fileItem.path)
        } else {
            openMediaFile(fileItem)
        }
    }
    
    private fun navigateTo(path: String) {
        currentPath = path
        updateToolbarTitle()
        loadFiles()
    }
    
    private fun openMediaFile(fileItem: FileItem) {
        val intent = when {
            fileItem.mimeType.startsWith("image/") -> {
                val images = getImagesInDirectory(File(currentPath))
                Intent(this, ImageViewerActivity::class.java).apply {
                    putStringArrayListExtra("image_paths", ArrayList(images))
                    putExtra("position", images.indexOf(fileItem.path))
                }
            }
            fileItem.mimeType.startsWith("video/") -> 
                Intent(this, VideoPlayerActivity::class.java).apply {
                    putExtra("video_path", fileItem.path)
                }
            fileItem.mimeType.startsWith("audio/") -> 
                Intent(this, MusicPlayerActivity::class.java).apply {
                    putExtra("music_path", fileItem.path)
                }
            else -> {
                try {
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.fromFile(File(fileItem.path)), fileItem.mimeType)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Cannot open ${fileItem.name}", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        startActivity(intent)
    }
    
    private fun getImagesInDirectory(dirPath: String): List<String> {
        return try {
            val dir = File(dirPath).parent ?: return emptyList()
            File(dir).listFiles { file ->
                file.isFile && file.mimeType?.startsWith("image/") == true
            }?.map { it.absolutePath } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun navigateUp() {
        if (isMultiSelectMode) {
            adapter.clearSelection()
            isMultiSelectMode = false
            updateMultiSelectUI()
            return
        }
        
        val parent = File(currentPath).parent
        if (parent != null && parent != Environment.getDataDirectory().absolutePath) {
            navigateTo(parent)
        } else {
            finish()
        }
    }
    
    private fun loadFiles() {
        viewModel.loadFiles(currentPath, showHiddenFiles)
    }
    
    private fun checkPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && 
            !Environment.isExternalStorageManager() -> {
                checkManageExternalStorage()
            }
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                loadFiles()
            }
        }
    }
    
    private fun checkManageExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.explorer_menu, menu)
        
        // Multi-select mode menu
        if (isMultiSelectMode) {
            menu.findItem(R.id.action_select_all).isVisible = true
            menu.findItem(R.id.action_delete).isVisible = true
            menu.findItem(R.id.action_share).isVisible = true
        }
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_name -> {
                viewModel.sortByName()
                true
            }
            R.id.action_sort_date -> {
                viewModel.sortByDate()
                true
            }
            R.id.action_view_grid -> {
                toggleViewType(true)
                true
            }
            R.id.action_view_list -> {
                toggleViewType(false)
                true
            }
            R.id.action_hidden -> {
                showHiddenFiles = !showHiddenFiles
                loadFiles()
                true
            }
            R.id.action_select_all -> {
                adapter.getSelectedFiles().clear()
                adapter.submitList(viewModel.files.value?.map { it.copy() })
                updateMultiSelectUI()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showDeleteConfirmation() {
        val selected = adapter.getSelectedFiles()
        if (selected.isEmpty()) return
        
        AlertDialog.Builder(this)
            .setTitle("Delete ${selected.size} item(s)?")
            .setMessage("This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                // TODO: Implement delete with recycle bin
                adapter.clearSelection()
                isMultiSelectMode = false
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onBackPressed() {
        if (isMultiSelectMode) {
            adapter.clearSelection()
            isMultiSelectMode = false
            updateMultiSelectUI()
        } else {
            super.onBackPressed()
        }
    }
}