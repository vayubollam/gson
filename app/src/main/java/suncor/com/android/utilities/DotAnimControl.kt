package suncor.com.android.utilities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.animation.LinearInterpolator
import androidx.annotation.Size
import androidx.annotation.StringRes
import suncor.com.android.R

class DotAnimControl(private val type: DotAnimType, private val callback: Callback) {

    private var animator: ValueAnimator? = null

    fun start(context: Context, @StringRes stringId: Int) = apply {
        val textList = when (type) {
            DotAnimType.COUNT -> getCountList(context, stringId)
            DotAnimType.SPAN -> getSpanList(context, stringId)
        }

        animator = getAnimator(context, textList)
        animator?.start()
    }

    private fun getCountList(context: Context, @StringRes stringId: Int): List<String> {
        val simpleText = context.getString(stringId)
        val dotText = context.getString(R.string.dot)

        val textList = mutableListOf<String>()
        for (i in 0 until DOT_COUNT + 1) {
            val text = StringBuilder(simpleText).apply {
                repeat(i) { append(dotText) }
            }.toString()

            textList.add(text)
        }

        return textList
    }

    private fun getSpanList(context: Context, @StringRes stringId: Int): List<SpannableString> {
        val simpleText = context.getString(stringId)
        val dotText = context.getString(R.string.dot)

        val resultText = StringBuilder(simpleText).apply {
            repeat(DOT_COUNT) { append(dotText) }
        }.toString()

        val textList = mutableListOf<SpannableString>()
        for (i in 0 until DOT_COUNT + 1) {
            val spannable = SpannableString(resultText)

            val start = resultText.length - (DOT_COUNT - i)
            val end = resultText.length
            val flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            spannable.setSpan(ForegroundColorSpan(Color.TRANSPARENT), start, end, flag)

            textList.add(spannable)
        }

        return textList
    }

    private fun getAnimator(
        context: Context,
        @Size(value = DOT_COUNT + 1L) list: List<CharSequence>
    ): ValueAnimator {
        val valueTo = list.size

        return ValueAnimator.ofInt(0, valueTo).apply {
            this.interpolator = LinearInterpolator()
            this.duration = context.resources.getInteger(R.integer.dots_anim_time).toLong()
            this.repeatCount = ObjectAnimator.INFINITE
            this.repeatMode = ObjectAnimator.RESTART

            addUpdateListener {
                val value = it.animatedValue as? Int


                /**
                 * Sometimes [ValueAnimator] give a corner value which equals valueTo.
                 */
                if (value == null || value == valueTo) return@addUpdateListener

                val text = list.getOrNull(value)
                if (text != null) {
                    callback.onDotAnimUpdate(text)
                }
            }
        }
    }

    fun stop() {
        animator?.cancel()
        animator = null
    }

    /**
     * Inside this callback need call [TextView.setText] or something similar.
     */
    interface Callback {
        fun onDotAnimUpdate(text: CharSequence)
    }

    companion object {
        const val DOT_COUNT = 3
    }
}

/**
 * Class identifying type of animation for [DotAnimControl].
 *
 * Description:
 * [COUNT] - create strings with different count of dots.
 * [SPAN] - create fix length string but with different tinting dots.
 *
 * Disadvantages:
 * [COUNT] - if text gravity "center" it will looks ugly, due to different string length.
 * [SPAN] - it can't be set for preference summaries
 */
enum class DotAnimType { COUNT, SPAN }