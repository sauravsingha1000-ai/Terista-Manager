// core/components/StorageView.kt
package com.terista.manager.core.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import kotlin.math.min

class StorageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var percentage = 0f
    private var animatedPercentage = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokeWidth = 20f
    private val radius = 100f
    
    private val gradient = LinearGradient(
        -radius, -radius, radius, radius,
        intArrayOf(0xFFFF6B35.toInt(), 0xFFF7931E.toInt()),
        null, Shader.TileMode.CLAMP
    )

    fun setPercentage(p: Float) {
        percentage = p.coerceIn(0f, 100f)
        animateToPercentage()
    }

    private fun animateToPercentage() {
        ValueAnimator.ofFloat(animatedPercentage, percentage).apply {
            duration = 1000
            addUpdateListener { animation ->
                animatedPercentage = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val viewRadius = min(centerX, centerY) - strokeWidth
        
        // Background circle
        paint.color = 0xFF2A2A30.toInt()
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(centerX, centerY, viewRadius, paint)
        
        // Progress arc
        paint.shader = gradient
        paint.strokeCap = Paint.Cap.ROUND
        val sweepAngle = (animatedPercentage / 100) * 360
        canvas.drawArc(
            centerX - viewRadius, centerY - viewRadius,
            centerX + viewRadius, centerY + viewRadius,
            -90f, sweepAngle, false, paint
        )
        
        // Center text
        paint.shader = null
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 36f
        paint.color = Color.WHITE
        canvas.drawText("${percentage.toInt()}%", centerX, centerY + 10f, paint)
        
        paint.textSize = 14f
        paint.color = 0xFF8A8A92.toInt()
        canvas.drawText("Used", centerX, centerY + 30f, paint)
    }
}