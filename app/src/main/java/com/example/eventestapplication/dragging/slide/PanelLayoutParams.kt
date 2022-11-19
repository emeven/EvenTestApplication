package com.example.eventestapplication.dragging.slide

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams

class PanelLayoutParams : MarginLayoutParams {
    var weight = 0f

    constructor() : super(MATCH_PARENT, MATCH_PARENT) {}
    constructor(width: Int, height: Int) : super(width, height) {}
    constructor(width: Int, height: Int, weight: Int) : this(width, height) {
        this.weight = weight.toFloat()
    }

    constructor(source: ViewGroup.LayoutParams?) : super(source) {}
    constructor(source: MarginLayoutParams?) : super(source) {}
    constructor(source: PanelLayoutParams?) : super(source) {}
    constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
        val ta = c.obtainStyledAttributes(attrs, ATTRS)
        if (ta != null) {
            weight = ta.getFloat(0, 0f)
            ta.recycle()
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.layout_weight)
    }
}