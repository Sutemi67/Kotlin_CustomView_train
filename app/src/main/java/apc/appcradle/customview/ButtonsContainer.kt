package apc.appcradle.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

internal class ButtonsContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = R.style.DefaultButtonsContainerStyle,
) : LinearLayout(
    context, attrs, defStyleAttr, defStyleRes
) {
    init {
        LayoutInflater.from(context).inflate(R.layout.buttons_container, this, true)
        val topButton = findViewById<Button>(R.id.top_button)
        val bottomButton = findViewById<Button>(R.id.bottom_button)

        this.orientation = VERTICAL
        this.setPadding(
            resources.getDimensionPixelSize(R.dimen.dp16),
            resources.getDimensionPixelSize(R.dimen.dp16),
            resources.getDimensionPixelSize(R.dimen.dp16),
            resources.getDimensionPixelSize(R.dimen.dp16)
        )

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ButtonsContainer,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                val topButtonText = getString(R.styleable.ButtonsContainer_topButtonText) ?: ""
                val bottomButtonBackgroundColor =
                    getColor(R.styleable.ButtonsContainer_bottomButtonBackgroundColor, 0)

                topButton.text = topButtonText
                bottomButton.setBackgroundColor(bottomButtonBackgroundColor)
            } finally {
                recycle()
            }
        }
    }
}