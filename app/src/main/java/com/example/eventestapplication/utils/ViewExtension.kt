package com.example.eventestapplication.utils

import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi

/**
 * view 拓展方法
 *
 * @return
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View?.setRoundCorner(radius: Float) {
    this?.apply {
        this.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(p0: View?, p1: Outline?) {
                p1?.setRoundRect(0, 0, p0?.width ?: 0, p0?.height ?: 0, radius)
            }
        }
        this.clipToOutline = true
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View?.setTopRoundCorner(radius: Float) {
    this?.apply {
        this.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(p0: View?, p1: Outline?) {
                p1?.setRoundRect(0, 0, p0?.width ?: 0, (p0?.height ?: 0) + radius.toInt(), radius)
            }
        }
        this.clipToOutline = true
    }
}