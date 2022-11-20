package com.example.eventestapplication.dragging.panel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.eventestapplication.R
import com.example.eventestapplication.dragging.slidingup.ISlidingUpPanel
import com.example.eventestapplication.dragging.slidingup.SlidingUpPanelLayout
import com.example.eventestapplication.utils.UIUtil

class CustomPanelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): FrameLayout(context, attrs), ISlidingUpPanel<CustomPanelView> {

    private var slideState = SlidingUpPanelLayout.COLLAPSED

    private var panelHeight = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_panel, this, true)
    }

    fun setPanelHeight(panelHeight: Int) {
        this.panelHeight = panelHeight
    }

    override fun getPanelView(): CustomPanelView = this

    override fun getPanelExpandedHeight(): Int {
        return UIUtil.getScreenHeight(context) / 4 * 3
    }

    override fun getPanelCollapsedHeight(): Int {
        return UIUtil.getScreenHeight(context) / 2
    }

    override fun getSlideState(): Int {
        return slideState
    }

    override fun setSlideState(slideState: Int) {
        this.slideState = slideState
    }

    override fun getPanelTopBySlidingState(slideState: Int): Int {
        return when(slideState) {
            SlidingUpPanelLayout.EXPANDED -> 0
            SlidingUpPanelLayout.COLLAPSED -> getPanelExpandedHeight() - getPanelCollapsedHeight()
            SlidingUpPanelLayout.HIDDEN -> getPanelExpandedHeight()
            else -> 0
        }
    }

    override fun onSliding(panel: ISlidingUpPanel<*>, top: Int, dy: Int, slidedProgress: Float) {

    }
}