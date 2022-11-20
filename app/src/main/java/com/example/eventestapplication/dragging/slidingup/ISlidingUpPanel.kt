package com.example.eventestapplication.dragging.slidingup

import android.view.View

/**
 *
 *
 * Your views must implement this interface, and override its methods according to your demands,
 * so the [SlidingUpPanelLayout] can work properly.
 *
 */
interface ISlidingUpPanel<T : View?> {
    /**
     * The **Panel** was focused and can be slided.
     *
     * @return instance of a focused **Panel**.
     */
    fun getPanelView(): T

    /**
     * Generally, this method returns [SlidingUpPanelLayout]'s height.
     *
     * @return The height of a focused **Panel** when expanded. In pixels.
     */
    fun getPanelExpandedHeight(): Int

    /**
     * @return The height of a focused **Panel** when collapsed. In pixels.
     */
    fun getPanelCollapsedHeight(): Int

    /**
     * @return The [SlidingUpPanelLayout.SlideState] of a focused **Panel**.
     */
    @SlidingUpPanelLayout.SlideState
    fun getSlideState(): Int

    /**
     * @param slideState {@link SlidingUpPanelLayout.SlideState}
     */
    fun setSlideState(@SlidingUpPanelLayout.SlideState slideState: Int)

    /**
     * This method would be called inside [SlidingUpPanelLayout.onLayout].
     * The value returned of this method is the offsets of the **Panel**'s top to its parent's top. In pixels.
     *
     *
     * Briefly, it controls initial position of **Panel** in its parent.
     *
     *
     * @return The `Top` of **Panel**. In pixels.
     */
    fun getPanelTopBySlidingState(@SlidingUpPanelLayout.SlideState slideState: Int): Int

    /**
     * The method will be called when a **Panel** is being slided.
     *
     * @param panel          The **Panel** is being slided
     * @param top            The top of the **Panel** is being slided
     * @param dy             Change in Y position from the last call
     * @param slidedProgress [SlidingUpPanelLayout.mSlidedProgress]
     */
    fun onSliding(panel: ISlidingUpPanel<*>, top: Int, dy: Int, slidedProgress: Float)
}