package suncor.com.android.uicomponents.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import suncor.com.android.uicomponents.R
import kotlin.math.abs

class SuncorBulletPointTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private val leftDrawable = ContextCompat.getDrawable(context, R.drawable.bullet_point)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBulletPoint(compoundDrawables[0], canvas)
    }

    private fun setBulletPoint(drawableLeft: Drawable?, canvas: Canvas?) {
        if (!TextUtils.isEmpty(text)) {
            leftDrawable?.let { drlft ->
                if (lineCount == 1) {
                    setCompoundDrawablesWithIntrinsicBounds(drlft, null, null, null)
                } else {
                    val buttonWidth = drlft.intrinsicWidth
                    val buttonHeight = drlft.intrinsicHeight
                    val topSpace = abs(buttonHeight - lineHeight) / 2

                    drlft.setBounds(0, topSpace, buttonWidth, topSpace + buttonHeight)

                    canvas?.apply {
                        save()
                        drlft.draw(canvas)
                        restore()
                    }
                }
            }
        }
    }
}