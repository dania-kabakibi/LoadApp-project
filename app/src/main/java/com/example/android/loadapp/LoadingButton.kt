package com.example.android.loadapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.concurrent.schedule

enum class Text(val text: Int) {
    LOADING(R.string.button_loading),
    DOWNLOAD(R.string.button_download)
}

enum class State {
    LOADING, COMPLETED
}

class LoadingButton(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var text = resources.getString(Text.DOWNLOAD.text)

    var state = State.COMPLETED

    private var customBackground = 0
    private var customTextColor = 0
    private var customCircleColor = 0

    private var rectLeft: Float = 0f
    private var rectTop: Float = 0f
    private var rectRight: Float = 0f
    private var rectBottom: Float = 0f

    private var sweepAngle = 0f
    private val maxSweepAngle = 360f
    private val animationDuration = 1500L

    private var currentWidth = 0f
    private var targetWidth = 0f

    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val newRectPaint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.dark_green)
    }

    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
    }

    private val circlePaint = Paint().apply {
        style = Paint.Style.FILL
    }

    init {
        isClickable = true

        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)

        customTextColor = typedArray.getColor(R.styleable.LoadingButton_customTextColor, 0)
        customBackground = typedArray.getColor(R.styleable.LoadingButton_customBackground, 0)
        customCircleColor = typedArray.getColor(R.styleable.LoadingButton_customCircleColor, 0)

        typedArray.recycle()

        paint.color = customBackground
        textPaint.color = customTextColor
        circlePaint.color = customCircleColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = 0
        rectLeft = margin.toFloat()
        rectTop = margin.toFloat()
        rectRight = (w - margin).toFloat()
        rectBottom = (h - margin).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        targetWidth = width.toFloat()

        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paint)

        if (state == State.LOADING) {
            val rLeft = 0f
            val rRight = currentWidth
            val rTop = centerY - 100f
            val rBottom = centerY + 100f

            canvas.drawRect(rLeft, rTop, rRight, rBottom, newRectPaint)
        }

        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        val textX = centerX
        val textY = centerY + textBounds.height() / 2f

        canvas.drawText(text, textX, textY, textPaint)

        if (state == State.LOADING) {
            val arcRadius = 45f
            val arcX = textX + textBounds.width() / 2f + arcRadius
            val arcY = centerY

            val left = arcX - arcRadius + 15f
            val top = arcY - arcRadius
            val right = arcX + arcRadius + 15f
            val bottom = arcY + arcRadius

            canvas.drawArc(
                left, top, right, bottom,
                0f, sweepAngle, true, circlePaint
            )
        }
        if (state == State.LOADING) reset()
    }

    //_________________________________________________________________________

    fun startAnimation(view: View) {
        animateCircle(view)
        animateButton(view)
        changeText()
    }

    private fun animateCircle(button: View) {
        ValueAnimator.ofFloat(0f, maxSweepAngle).apply {
            duration = animationDuration
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animator ->
                sweepAngle = animator.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    button.isEnabled = false
                }

                override fun onAnimationEnd(animation: Animator) {
                    button.isEnabled = true
                }
            })
        }.start()
    }

    private fun animateButton(button: View) {
        ValueAnimator.ofFloat(0f, targetWidth).apply {
            duration = animationDuration
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animator ->
                currentWidth = animator.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    button.isEnabled = false
                }

                override fun onAnimationEnd(animation: Animator) {
                    button.isEnabled = true
                }
            })
        }.start()
    }

    private fun changeText() {
        text = resources.getString(Text.LOADING.text)
        invalidate()
    }

    //_____________________________________________________________________

    private fun reset() {
        if (downloadCompleted) {
            Timer().schedule(2000) {
                state = State.COMPLETED
                resetText()
            }
        }
    }

    private fun resetText() {
        text = resources.getString(Text.DOWNLOAD.text)
        invalidate()
    }
}