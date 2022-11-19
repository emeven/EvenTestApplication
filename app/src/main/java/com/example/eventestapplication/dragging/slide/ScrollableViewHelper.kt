package com.example.eventestapplication.dragging.slide

import android.view.View
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * Helper class for determining the current scroll positions for scrollable views. Currently works
 * for ListView, ScrollView and RecyclerView, but the library users can override it to add support
 * for other views.
 */
class ScrollableViewHelper {
    /**
     * Returns the current scroll position of the scrollable view. If this method returns zero or
     * less, it means at the scrollable view is in a position such as the panel should handle
     * scrolling. If the method returns anything above zero, then the panel will let the scrollable
     * view handle the scrolling
     *
     * @param view the scrollable view
     * @param isSlidingUp whether or not the panel is sliding up or down
     * @return the scroll position
     */
    fun getScrollableViewScrollPosition(view: View?, isSlidingUp: Boolean): Int {
        if (view == null) return 0
        return if (view is ScrollView || view is NestedScrollView) {
            if (isSlidingUp) {
                view.scrollY
            } else {
                val sv = view as FrameLayout
                val child = sv.getChildAt(0)
                child.bottom - (sv.height + sv.scrollY)
            }
        } else if (view is ListView && view.childCount > 0) {
            if (view.adapter == null) return 0
            if (isSlidingUp) {
                val firstChild = view.getChildAt(0)
                // Approximate the scroll position based on the top child and the first visible item
                view.firstVisiblePosition * firstChild.height - firstChild.top
            } else {
                val lastChild = view.getChildAt(view.childCount - 1)
                // Approximate the scroll position based on the bottom child and the last visible item
                (view.adapter.count - view.lastVisiblePosition - 1) * lastChild.height + lastChild.bottom - view.bottom
            }
        } else if (view is RecyclerView && view.childCount > 0) {
            val rv: RecyclerView = view as RecyclerView
            val lm = rv.layoutManager ?: return 0
            val adapter = rv.adapter ?: return 0
            if (isSlidingUp) {
                val firstChild: View = rv.getChildAt(0)
                // Approximate the scroll position based on the top child and the first visible item
                rv.getChildLayoutPosition(firstChild) * lm.getDecoratedMeasuredHeight(firstChild) - lm.getDecoratedTop(firstChild)
            } else {
                val lastChild: View = rv.getChildAt(rv.childCount - 1)
                // Approximate the scroll position based on the bottom child and the last visible item
                (adapter.itemCount - 1) * lm.getDecoratedMeasuredHeight(lastChild) + lm.getDecoratedBottom(lastChild) - rv.bottom
            }
        } else {
            0
        }
    }
}