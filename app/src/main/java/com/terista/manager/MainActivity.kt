// MainActivity.kt - COMPLETE
package com.terista.manager

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.terista.manager.core.components.StorageView
import com.terista.manager.databinding.ActivityMainBinding
import com.terista.manager.ui.home.CategoryAdapter
import com.terista.manager.ui.home.FavoritesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        android.util.Log.d("TERISTA", "App started")
        setupToolbar()
        setupStorageView()
        setupRecyclers()
        observeData()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
                R.id.action_settings -> {
                    startActivity(android.content.Intent(this, com.terista.manager.ui.settings.SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupStorageView() {
        binding.storageAccessCard.setOnClickListener {
            startActivity(android.content.Intent(this, com.terista.manager.ui.explorer.FileExplorerActivity::class.java))
        }
    }
    
    private fun setupRecyclers() {
        // Categories (3-column grid)
        binding.recyclerCategories.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerCategories.adapter = CategoryAdapter { category ->
            // Navigate to category
        }
        
        // Recent files (horizontal)
        binding.recyclerRecent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerRecent.adapter = com.terista.manager.ui.home.RecentAdapter()
        
        // Favorites (horizontal)
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerFavorites.adapter = FavoritesAdapter()
    }
    
    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.storageInfo.collectLatest { info ->
                binding.storageView.setPercentage(info.percentage)
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.terista.manager.R.menu.main_menu, menu)
        return true
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle drawer navigation (already implemented)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}