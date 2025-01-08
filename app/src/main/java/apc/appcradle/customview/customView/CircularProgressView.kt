package apc.appcradle.customview.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import apc.appcradle.customview.R
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min


class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    //region Размеры
    private val minViewSize = resources.getDimensionPixelSize(R.dimen.circularProgressViewMinSize)
    private val strokeWidthPx = 20f
    private var maxProgress = 100f
    private var currentProgress = 35f

    //endregion
    //region Кисти
    private val trackPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = strokeWidthPx
    }
    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = strokeWidthPx
    }

    //endregion
    //region Расчеты
    private var centerX = measuredWidth / 2f
    private var centerY = measuredHeight / 2f
    private var radius = (measuredWidth - strokeWidthPx) / 2f
    private val arcRect = RectF()
    //endregion

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                // логика обработки
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                // логика обработки
                return true
            }

            // ...
        }
    )

    fun setCurrentProgress(newCurrentProgress: Float) {
        if (newCurrentProgress < 0f || newCurrentProgress > 100f) {
            return
        }
        this.currentProgress = newCurrentProgress
        invalidate()
    }

    private fun Canvas.drawTrack(centerX: Float, centerY: Float, radius: Float) {
        // Рисуем фоновую дорожку
        drawCircle(centerX, centerY, radius, trackPaint)
    }

    private fun Canvas.drawProgress() {
        val sweepAngle = (currentProgress / maxProgress) * 360
        drawArc(arcRect, -90f, sweepAngle, false, progressPaint)
    }

    private fun updateProgress(pX: Float, pY: Float) {
        val dy = (pY - centerY).toDouble()
        val dx = (pX - centerX).toDouble()
        var angle = Math.toDegrees(atan2(dy, dx) + (Math.PI / 2))
        if (angle < 0) {
            angle += 360
        }
        val progress = max(0.0, min(1.0, angle / 360.0))
        setCurrentProgress(
            newCurrentProgress = (progress * maxProgress).toFloat()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Расчёт ширины
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val contentWidth = when (val widthMode = MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> minViewSize
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
            else -> error("Неизвестный режим ширины ($widthMode)")
        }
        // Расчёт высоты
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val contentHeight = when (val heightMode = MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> minViewSize
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> heightSize
            else -> error("Неизвестный режим высоты ($heightMode)")
        }
        // Берём минимальное значение — либо ширину, либо высоту,
        // чтобы сформировать квадрат.
        val size = min(contentWidth, contentHeight)
        // Устанавливаем посчитанные размеры
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) = with(canvas) {
        drawTrack(centerX, centerY, radius)
        drawProgress()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = measuredWidth / 2f
        centerY = measuredHeight / 2f
        radius = (measuredWidth - strokeWidthPx) / 2f
        arcRect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                updateProgress(event.x, event.y)
                return true
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

}