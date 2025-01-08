package apc.appcradle.customview.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import apc.appcradle.customview.R
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

    private var maxProgress = 100f
    private var currentProgress = 35f

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


}