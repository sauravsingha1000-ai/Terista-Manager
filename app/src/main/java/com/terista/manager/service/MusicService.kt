// service/MusicService.kt
package com.terista.manager.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.terista.manager.R
import com.terista.manager.ui.media.MusicPlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
    
    @Inject lateinit var mediaPlayer: MediaPlayer
    private val binder = LocalBinder()
    
    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val musicPath = intent?.getStringExtra("music_path") ?: return START_NOT_STICKY
        
        mediaPlayer.reset()
        mediaPlayer.setDataSource(musicPath)
        mediaPlayer.prepare()
        mediaPlayer.start()
        
        createNotification()
        return START_STICKY
    }
    
    private fun createNotification() {
        val channelId = "music_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Music Player", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        
        val intent = Intent(this, MusicPlayerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Terista Music")
            .setContentText("Playing...")
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        
        startForeground(1, notification)
    }
    
    fun togglePlayPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }
    
    fun seekTo(position: Long) {
        mediaPlayer.seekTo(position.toInt())
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
}