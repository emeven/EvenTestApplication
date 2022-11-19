package com.example.eventestapplication.dragging.slide

import android.view.View

/**
 * 面板滑动回调
 */
interface PanelSlideListener {

    //当面板滑动位置发送变化
    fun onPanelSlide(panel: View, slideOffset: Float)

    //当面板状态发送变化
    fun onPanelStateChanged(panel: View, preState: PanelState, newState: PanelState)
}