package com.alphacorp.instaloader

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

class AnimatedGradientDrawable : Drawable() {
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rotationAngle = 0f
    private val colors = intArrayOf(
        0xFFE91E63.toInt(), // Pink
        0xFF9C27B0.toInt(), // Purple
        0xFF673AB7.toInt(), // Deep Purple
        0xFF3F51B5.toInt(), // Indigo
        0xFF2196F3.toInt(), // Blue
        0xFF00BCD4.toInt(), // Cyan
        0xFF009688.toInt(), // Teal
        0xFF4CAF50.toInt(), // Green
        0xFF8BC34A.toInt(), // Light Green
        0xFFCDDC39.toInt(), // Lime
        0xFFFFEB3B.toInt(), // Yellow
        0xFFFFC107.toInt(), // Amber
        0xFFFF9800.toInt(), // Orange
        0xFFFF5722.toInt(), // Deep Orange
        0xFF795548.toInt(), // Brown
        0xFF9E9E9E.toInt(), // Grey
        0xFF607D8B.toInt(), // Blue Grey
        0xFFE91E63.toInt()  // Back to Pink
    )
    
    init {
        paint.shader = createGradientShader()
    }
    
    private fun createGradientShader(): LinearGradient {
        val positions = FloatArray(colors.size) { it.toFloat() / (colors.size - 1) }
        return LinearGradient(
            0f, 0f, 1f, 1f,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
    }
    
    fun updateRotation(angle: Float) {
        rotationAngle = angle
        paint.shader = createGradientShader()
        invalidateSelf()
    }
    
    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        
        canvas.save()
        canvas.rotate(rotationAngle, centerX, centerY)
        
        // Draw the gradient
        canvas.drawRect(bounds, paint)
        
        canvas.restore()
    }
    
    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }
    
    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
    
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}



