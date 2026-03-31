// ui/media/subview/ZoomableImageView.kt
package com.terista.manager.ui.media.subview

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import java.io.File

class ZoomableImageView(context: Context) : ImageView(context) {
    
    private var scaleDetector: ScaleGestureDetector
    private var matrix = Matrix()
    private var savedMatrix = Matrix()
    private var scaleFactor = 1f
    
    init {
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        imageMatrix = matrix
        scaleType = ScaleType.MATRIX
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
    
    fun setImageFile(file: File) {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        setImageBitmap(bitmap)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        return true
    }
    
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(0.5f, min(scaleFactor, 5.0f))
            
            matrix.postScale(scaleFactor, scaleFactor, 
                detector.focusX, detector.focusY)
            imageMatrix = matrix
            invalidate()
            return true
        }
    }
}