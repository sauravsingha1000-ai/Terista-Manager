// ui/media/VideoPlayerActivity.kt
package com.terista.manager.ui.media

import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.terista.manager.databinding.ActivityVideoPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var player: ExoPlayer
    private lateinit var gestureDetector: GestureDetectorCompat
    private var isControlsVisible = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val videoPath = intent.getStringExtra("video_path") ?: return
        initPlayer(videoPath)
        setupGestures()
        binding.playerView.requestFocus()
    }
    
    private fun initPlayer(videoPath: String) {
        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(java.io.File(videoPath))))
            prepare()
            playWhenReady = true
        }
        binding.playerView.player = player
    }
    
    private fun setupGestures() {
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
                return true
            }
            
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                toggleControls()
                return true
            }
        })
    }
    
    private fun toggleControls() {
        isControlsVisible = !isControlsVisible
        binding.playerView.useController = isControlsVisible
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }
    
    override fun onPause() {
        super.onPause()
        player.pause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}