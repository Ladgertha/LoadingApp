package ru.ladgertha.loadingapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 24F * resources.displayMetrics.scaledDensity
    }

    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.normalBackgroundButton)
        style = Paint.Style.FILL
    }

    private val circlePaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    private var buttonLabel: String = resources.getString(R.string.button_name)
    private var loadingProgress: Int = 1

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonPaint.color =
                    resources.getColor(R.color.clickedBackgroundButton, null)
            }
            ButtonState.Completed -> {
                buttonPaint.color =
                    resources.getColor(R.color.normalBackgroundButton, null)
                buttonLabel = resources.getString(R.string.button_name)
                //valueAnimator.end()
            }
            ButtonState.Loading -> {
                //valueAnimator.start()
                buttonLabel = resources.getString(R.string.button_loading)
                buttonPaint.color =
                    resources.getColor(R.color.loadingBackgroundButton, null)
            }
        }
    }


    init {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawBackgroundButton(canvas)
            drawText(canvas)
            drawCircle(canvas)
        }
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(
            buttonLabel,
            (widthSize / 2f),
            (heightSize / 2f),
            textPaint
        )
    }

    private fun drawBackgroundButton(canvas: Canvas) {
        canvas.drawRoundRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            30f,
            30f,
            buttonPaint
        )
    }

    private fun drawCircle(canvas: Canvas) {
        val circleMargin = 0.2f
        val circleLeft =
            widthSize.toFloat() - heightSize.toFloat() + heightSize.toFloat() * circleMargin
        val circleTop = heightSize.toFloat() * circleMargin
        val circleRight = widthSize.toFloat() - heightSize.toFloat() * circleMargin
        val circleBottom = heightSize.toFloat() - heightSize.toFloat() * circleMargin

        canvas.drawArc(
            circleLeft,
            circleTop,
            circleRight,
            circleBottom,
            0F,
            360F * loadingProgress / 100,
            true,
            circlePaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setState(buttonState: ButtonState) {
        this.buttonState = buttonState
        invalidate()
    }

    fun setProgress(progress: Int) {
        loadingProgress = progress
        invalidate()
    }
}