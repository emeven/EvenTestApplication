package com.example.eventestapplication.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

object StatusBarUtils {
    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context?): Int {
        if (context == null) {
            return 0
        }
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    //region Status Bar Color
    /**
     * 启用全透明状态栏，沉浸式
     */
    fun Activity?.enableTransparentStatusBar() = setStatusBarTransparent(this)

    /**
     * setStatusBarTransparent 会使状态栏变为沉浸式
     */
    fun setStatusBarTransparent(activity: Activity?) = setStatusBarTransparent(activity?.window)

    /**
     * setStatusBarTransparent 会使状态栏变为沉浸式
     */
    fun setStatusBarTransparent(window: Window?) {
        window?.apply {
            setStatusBarColor(
                window,
                ContextCompat.getColor(context, android.R.color.transparent)
            )
        }
    }

    /**
     * setStatusBarColor
     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 会使状态栏变为沉浸式
     */
    fun setStatusBarColor(window: Window?, @ColorInt color: Int) {
        if (window == null) return
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
}