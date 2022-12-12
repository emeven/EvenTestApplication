package com.example.eventestapplication.span

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.TextPaint
import android.text.style.TypefaceSpan
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class RedNumTypefaceSpan(val type: Typeface) : TypefaceSpan(type) {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, type)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, type)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old: Typeface = paint.typeface
        oldStyle = old.style
        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }
        paint.typeface = tf
    }
}