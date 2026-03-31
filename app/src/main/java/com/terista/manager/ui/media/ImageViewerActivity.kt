// ui/media/ImageViewerActivity.kt
package com.terista.manager.ui.media

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager2.widget.ViewPager2
import com.terista.manager.databinding.ActivityImageViewerBinding
import com.terista.manager.ui.media.adapter.ImagePagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class ImageViewerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityImageViewerBinding
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetectorCompat
    private var scaleFactor = 1.0f
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val imagePaths = intent.getStringArrayListExtra("image_paths") ?: arrayListOf()
        val initialPosition = intent.getIntExtra("position", 0)
        
        setupViewPager(imagePaths, initialPosition)
        setupGestures()
    }
    
    private fun setupViewPager(paths: List<String>, position: Int) {
        binding.viewPager.adapter = ImagePagerAdapter(paths)
        binding.viewPager.setCurrentItem(position, false)
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.pageIndicator.text = "${position + 1} / ${paths.size}"
            }
        })
    }
    
    private fun setupGestures() {
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = max(0.5f, min(scaleFactor, 5.0f))
                binding.viewPager.scaleX = scaleFactor
                binding.viewPager.scaleY = scaleFactor
                return true
            }
        })
        
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                binding.viewPager.beginFakeDrag(velocityX / 1000)
                return true
            }
        })
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }
}