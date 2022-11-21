package com.example.eventestapplication.dragging.panel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.example.eventestapplication.R
import com.example.eventestapplication.dragging.slidingup.ISlidingUpPanel
import com.example.eventestapplication.dragging.slidingup.SlidingUpPanelLayout
import com.example.eventestapplication.utils.UIUtil
import kotlinx.android.synthetic.main.layout_custom_panel.view.*

class CustomPanelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): FrameLayout(context, attrs), ISlidingUpPanel<CustomPanelView> {

    private var slideState = SlidingUpPanelLayout.COLLAPSED

    private var panelHeight = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_panel, this, true)
    }

    fun initRecyclerView(adapter: MultiTypeAdapter) {
        panelList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        panelList.adapter = adapter
        panelList.canScrollVertically(-1)
    }

    fun setPanelHeight(panelHeight: Int) {
        this.panelHeight = panelHeight
    }

    fun canScroll(dy: Float): Boolean {
        // dy > 0 上滑
        val layoutManager = panelList.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && dy > 0) {
            return false
        }
        if (layoutManager.findLastCompletelyVisibleItemPosition() == (panelList.adapter?.itemCount ?: 0) - 1 && dy > 0) {
            return false
        }
        return panelList.canScrollVertically(dy.toInt())
    }

    override fun getPanelView(): CustomPanelView = this

    override fun getPanelExpandedHeight(): Int {
        return UIUtil.getScreenHeight(context)
    }

    override fun getPanelCollapsedHeight(): Int {
        return UIUtil.getScreenHeight(context) / 4 * 3
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
        // bottom -> top
        // slidedProgress: 0f -> 1f
        // dy: -60 -> -1
        // top: 477 -> 0
    }

    private fun getActionString(action: Int): String {
        return when(action) {
            MotionEvent.ACTION_DOWN -> "down"
            MotionEvent.ACTION_UP -> "up"
            MotionEvent.ACTION_MOVE -> "move"
            MotionEvent.ACTION_CANCEL -> "cancel"
            MotionEvent.ACTION_OUTSIDE -> "outside"
            else -> "else"
        }
    }
}