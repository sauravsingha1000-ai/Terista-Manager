// ui/media/MusicPlayerActivity.kt
package com.terista.manager.ui.media

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.terista.manager.R
import com.terista.manager.databinding.ActivityMusicPlayerBinding
import com.terista.manager.service.MusicService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMusicPlayerBinding
    private var musicService: MusicService? = null
    private var isBound = false
    
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val musicPath = intent.getStringExtra("music_path") ?: return
        bindMusicService(musicPath)
        setupControls()
    }
    
    private fun bindMusicService(musicPath: String) {
        val intent = Intent(this, MusicService::class.java).apply {
            putExtra("music_path", musicPath)
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startForegroundService(intent)
    }
    
    private fun setupControls() {
        binding.btnPlayPause.setOnClickListener {
            musicService?.togglePlayPause()
        }
        
        binding.seekBar.setOnSeekBarChangeListener(object : 
            android.widget.SeekBar.OnSeekBarChangeListener {
            
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.seekTo(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }
}