package com.example.eventestapplication.dragging.slide

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import com.example.eventestapplication.R
import kotlin.math.abs

//import androidx.customview.widget.ViewDragHelper

/**
 * 仿美团订单，嵌套滑动拖拽控件
 */
class SlideNestedPanelLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {

    //默认面板状态
    private val DEFAULT_STATE: PanelState = PanelState.COLLAPSED

    //最少滑动速度
    private val DEFAULT_FLING_VELOCITY = 400

    //默认渐变色
    private val DEFAULT_FADE_COLOR = -0x67000000

    //默认覆盖标识
    private val DEFAULT_OVERLAY_FLAG = false

    //默认裁剪标识
    private val DEFAULT_CLIP_FLAG = true

    //默认基准点
    private val DEFAULT_ANCHOR_POINT = 1.0f

    //默认面板高度
    private val DEFAULT_PANEL_HEIGHT = 68

    //默认视觉差比例
    private val DEFAULT_PARALLAX_OFFSET = 0

    //面板高度
    private var mPanelHeight = 0


    //视觉差比例
    private var mParallaxOffset = 0


    //最少滑动速度因子
    private var mFlingVelocity = 0


    //覆盖渐变颜色
    private var mFadeColor = 0


    //嵌套滑动view resId
    private var mScrollViewResId = 0


    //覆盖内容标识
    private var mOverlayFlag = false


    //裁剪标识
    private var mClipPanelFlag = false

    //面板停止基准点
    private var mAnchorPoint = 0f


    //状态
    private var mPanelState: PanelState = DEFAULT_STATE


    //拖拽助手helper
    private var mDragHelper: ViewDragHelper? = null


    //是否依附到窗口
    private var attachedToWindow = true


    //锁定面板，不可滑动标识
    private var isUnableToDrag = false


    //主View
    private var mMainView: View? = null


    //面板内拖拽view
    private var mDragView: View? = null


    //面板内Scroll View
    private var mScrollView: View? = null

    //滑动辅助
    private val mScrollableViewHelper = ScrollableViewHelper()


    //面板距离展开的位置，范围0-anchorPoint(0为折叠，anchorPoint为展开基准点)
    private var mSlideOffset = 0f


    //记录下面板已滑动的范围
    private var mSlideRange = 0


    //是否自己处理事件
    private var isMyHandleTouch = false


    //是否到顶(-1不确定、0底部、1顶部)
    private var isOnTopFlag: Short = -1


    //绘制区域
    private val mTmpRect = Rect()


    //当Main滑动时用来画渐变的画笔
    private val mCoveredFadePaint = Paint()

    //状态回调
    private var stateCallback: StateCallback? = null


    //标记触摸位置
    private var mPrevMotionX = 0f
    private var mPrevMotionY = 0f
    private var mInitialMotionX = 0f
    private var mInitialMotionY = 0f


    init {
        //定义拖拽的插值器
        //获取自定义属性

        //定义拖拽的插值器
        //获取自定义属性
        var scrollerInterpolator: Interpolator? = null
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SlideNestedPanelLayout)
            if (ta != null) {
                mPanelHeight = ta.getDimensionPixelSize(R.styleable.SlideNestedPanelLayout_panelHeight, -1)
                mParallaxOffset = ta.getDimensionPixelSize(R.styleable.SlideNestedPanelLayout_parallaxOffset, -1)
                mFlingVelocity = ta.getInt(R.styleable.SlideNestedPanelLayout_flingVelocity, DEFAULT_FLING_VELOCITY)
                mFadeColor = ta.getColor(R.styleable.SlideNestedPanelLayout_fadeColor, DEFAULT_FADE_COLOR)
                mScrollViewResId = ta.getResourceId(R.styleable.SlideNestedPanelLayout_scrollView, -1)
                mOverlayFlag = ta.getBoolean(R.styleable.SlideNestedPanelLayout_overlay, DEFAULT_OVERLAY_FLAG)
                mClipPanelFlag = ta.getBoolean(R.styleable.SlideNestedPanelLayout_clipPanel, DEFAULT_CLIP_FLAG)
                mAnchorPoint = ta.getFloat(R.styleable.SlideNestedPanelLayout_anchorPoint, DEFAULT_ANCHOR_POINT)
                mPanelState = PanelState.values()[ta.getInt(R.styleable.SlideNestedPanelLayout_initialState, DEFAULT_STATE.ordinal)]
                val interpolatorResId = ta.getResourceId(R.styleable.SlideNestedPanelLayout_interpolator, -1)
                if (interpolatorResId != -1) {
                    scrollerInterpolator = AnimationUtils.loadInterpolator(context, interpolatorResId)
                }
                ta.recycle()
            }
        }

        //根据密度算默值

        //根据密度算默值
        val density = context.resources.displayMetrics.density
        if (mPanelHeight == -1) mPanelHeight = (DEFAULT_PANEL_HEIGHT * density + 0.5f).toInt()

        if (mParallaxOffset == -1) mParallaxOffset = (DEFAULT_PARALLAX_OFFSET * density).toInt()

        //不要执行onDraw

        //不要执行onDraw
        setWillNotDraw(false)

        mDragHelper = ViewDragHelper.create(this, 1.0f, DragHelperCallback())
        mDragHelper?.minVelocity = mFlingVelocity * density
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return PanelLayoutParams()
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return if (p is MarginLayoutParams) PanelLayoutParams(p) else PanelLayoutParams(p)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is PanelLayoutParams && super.checkLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return PanelLayoutParams(context, attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (mScrollViewResId != -1) mScrollView = findViewById(mScrollViewResId)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val childCount = childCount
        if (isAttachedToWindow) {
            mSlideOffset = when (mPanelState) {
                PanelState.EXPANDED -> mAnchorPoint
                PanelState.ANCHORED -> mAnchorPoint
                else -> 0f
            }
        }
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.layoutParams as PanelLayoutParams
            if (child.visibility == GONE && (i == 0 || isAttachedToWindow)) continue
            val childHeight = child.measuredHeight
            var childTop = paddingTop
            if (child === mDragView) childTop = computePanelToPosition(mSlideOffset)
            val childBottom = childTop + childHeight
            val childLeft = paddingLeft + params.leftMargin
            val childRight = childLeft + child.measuredWidth
            child.layout(childLeft, childTop, childRight, childBottom)
        }
        applyParallaxForCurrentSlideOffset()
        attachedToWindow = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //宽高必须占满
        require(!(widthMode != MeasureSpec.EXACTLY && widthMode != MeasureSpec.AT_MOST)) { "Width 必须填满或者指定值" }
        require(!(heightMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.AT_MOST)) { "Height 必须填或者指定值" }
        val childCount = childCount
        require(childCount == 2) { "SlideNestedPanelLayout必须有2给子view" }
        mMainView = getChildAt(0)
        mDragView = getChildAt(1)
        val layoutWidth = widthSize - paddingLeft - paddingRight
        val layoutHeight = heightSize - paddingTop - paddingBottom

        //第一步：首先测量子view的宽高
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.layoutParams as PanelLayoutParams
            if (child.visibility == GONE && i == 0) continue
            var width = layoutWidth
            var height = layoutHeight


            //如果是主view，要记录需要overlay的高度
            if (child === mMainView) {
                if (!mOverlayFlag) height -= mPanelHeight
                width -= params.leftMargin + params.rightMargin
            } else if (child === mDragView) {
                height -= params.topMargin
            }

            //判断width应该使用的Mode
            val childWidthSpec = if (params.width === LayoutParams.WRAP_CONTENT) {
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST)
            } else if (params.width === LayoutParams.MATCH_PARENT) {
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
            } else {
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
            }

            //判断height应该使用的Mode
            var childHeightSpec: Int
            if (params.height === LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
            } else {
                //根据权重修正高度
                if (params.weight > 0 && params.weight < 1) height = (height * params.weight).toInt() else if (params.height !== LayoutParams.MATCH_PARENT) height = params.height
                childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            }
            child.measure(childWidthSpec, childHeightSpec)
            if (child === mDragView) mSlideRange = (mDragView?.measuredHeight ?: 0) - mPanelHeight
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (!isEnabled) super.onTouchEvent(event) else try {
            mDragHelper?.processTouchEvent(event)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action: Int = MotionEventCompat.getActionMasked(ev)
        //点击外部渐变层折叠收缩
        //松开，非折叠状态，且不在拖拽，点击落在dragView
        if (ev.action == MotionEvent.ACTION_UP && mDragHelper?.isDragging == false
            && mPanelState !== PanelState.COLLAPSED && isViewUnder(mDragView, ev.x.toInt(), ev.y.toInt())
        ) {
            mScrollView?.scrollTo(0, 0)
            mDragHelper?.smoothSlideViewTo(mDragView!!, 0, computePanelToPosition(0f))
            ViewCompat.postInvalidateOnAnimation(this)

            //点击渐变收缩后，把拖拽标识恢复
            isEnabled = true
            return true
        }
        val x = ev.x
        val y = ev.y
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mPrevMotionX = x
                mPrevMotionY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mPrevMotionX
                val dy = y - mPrevMotionY
                mPrevMotionX = x
                mPrevMotionY = y

                //横向滑动就不分发了
                if (Math.abs(dx) > Math.abs(dy)) {
                    return true
                }

                //滑动向上、向下
                return if (dy > 0) { //收缩
                    if (mScrollableViewHelper.getScrollableViewScrollPosition(mScrollView, true) > 0) {
                        isMyHandleTouch = true
                        return super.dispatchTouchEvent(ev)
                    }

                    //之前子view处理了事件
                    //我们就需要重新组合一下让面板得到一个合理的点击事件
                    if (isMyHandleTouch) {
                        val up = MotionEvent.obtain(ev)
                        up.action = MotionEvent.ACTION_CANCEL
                        super.dispatchTouchEvent(up)
                        up.recycle()
                        ev.action = MotionEvent.ACTION_DOWN
                    }
                    isMyHandleTouch = false
                    onTouchEvent(ev)
                } else { //展开

                    //scrollY=0表示没滑动过，canScroll(1)表示可scroll up
                    //逻辑或的意义：拖拽到顶后，要不要禁用外部拖拽
                    if (isOnTopFlag.toInt() == 1) {
                        val offset = mDragView?.scrollY
                        val scroll = mScrollableViewHelper.getScrollableViewScrollPosition(mScrollView, true) > 0
                        isEnabled = offset == 0 || scroll
                        mDragHelper?.abort()
                        return super.dispatchTouchEvent(ev)
                    }

                    //面板是否全部展开
                    if (mSlideOffset < mAnchorPoint) {
                        isMyHandleTouch = false
                        return onTouchEvent(ev)
                    }
                    if (!isMyHandleTouch && mDragHelper?.isDragging == true) {
                        mDragHelper?.cancel()
                        ev.action = MotionEvent.ACTION_DOWN
                    }
                    isMyHandleTouch = true
                    super.dispatchTouchEvent(ev)
                }
            }
            MotionEvent.ACTION_UP -> {

                //如果内嵌视图正在处理触摸，会接收到一个up事件、
                //我们想要清楚之前所有的拖拽状态，这样我们就不会意外拦截一个触摸事件
                if (isMyHandleTouch) {
                    mDragHelper?.setDragState(ViewDragHelper.STATE_IDLE)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //如果scrollView处理事件，则不要拦截
        if (isMyHandleTouch) {
            mDragHelper!!.abort()
            return false
        }
        val action = MotionEventCompat.getActionMasked(ev)
        val x = ev.x
        val y = ev.y
        val adx = abs(x - mInitialMotionX)
        val ady = abs(y - mInitialMotionY)
        val dragSlop = mDragHelper!!.touchSlop
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                isUnableToDrag = false
                mInitialMotionX = x
                mInitialMotionY = y
                if (isViewUnder(mDragView, x.toInt(), y.toInt())) {
                    mDragHelper!!.cancel()
                    isUnableToDrag = true
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (ady > dragSlop && adx > ady) {
                    mDragHelper!!.cancel()
                    isUnableToDrag = true
                    return false
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {

                //如果抬手释放后drawView还在拖拽状态，需要调用processTouchEvent
                if (mDragHelper?.isDragging == true) {
                    mDragHelper?.processTouchEvent(ev)
                    return true
                }
            }
        }
        return mDragHelper!!.shouldInterceptTouchEvent(ev)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val result: Boolean
        val save = canvas.save()
        //mainView的遮盖渐变层
        if (mDragView != null && mDragView !== child) {
            canvas.getClipBounds(mTmpRect)
            if (!mOverlayFlag) {
                mTmpRect.bottom = mTmpRect.bottom.coerceAtMost(mDragView!!.top)
            }
            if (mClipPanelFlag) {
                canvas.clipRect(mTmpRect)
            }
            result = super.drawChild(canvas, child, drawingTime)
            if (mFadeColor != 0 && mSlideOffset > 0) {
                val baseAlpha = mFadeColor and -0x1000000 ushr 24
                val imag = (baseAlpha * mSlideOffset).toInt()
                val color = imag shl 24 or (mFadeColor and 0xffffff)
                mCoveredFadePaint.color = color
                canvas.drawRect(mTmpRect, mCoveredFadePaint)
            }
        } else {
            result = super.drawChild(canvas, child, drawingTime)
        }

        //没有合适的回调方法，只能另辟蹊径了
        //在这里判断dragView有没有到顶，然后把事件给内嵌view
        val targetY: Int = computePanelToPosition(mAnchorPoint)
        val originalY: Int = computePanelToPosition(0f)
        isOnTopFlag = if (mDragView!!.top == targetY) {
            //避免多次回调
            if (isOnTopFlag.toInt() != 1 && stateCallback != null) {
                stateCallback?.onExpandedState()
            }
            1
        } else if (mDragView!!.top == originalY) {
            if (isOnTopFlag.toInt() == -1 && stateCallback != null) {
                stateCallback?.onCollapsedState()
            }
            0
        } else {
            -1
        }
        canvas.restoreToCount(save)
        return result
    }

    override fun computeScroll() {
        if (mDragHelper != null && mDragHelper?.continueSettling(true) == true) {
            if (!isEnabled) {
                mDragHelper?.abort()
                return
            }
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    /**
     * 坐标是否落在target view
     */
    private fun isViewUnder(view: View?, x: Int, y: Int): Boolean {
        if (view == null) return true
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)
        val parentLocation = IntArray(2)
        getLocationOnScreen(parentLocation)
        val screenX = parentLocation[0] + x
        val screenY = parentLocation[1] + y
        return screenX < viewLocation[0] || screenX >= viewLocation[0] + view.width || screenY < viewLocation[1] || screenY >= viewLocation[1] + view.height
    }

    /**
     * 面板状态
     */
    private fun setPanelStateInternal(state: PanelState) {
        if (mPanelState === state) return
        mPanelState = state
    }

    /**
     * 计算滑动的偏移量
     */
    private fun computePanelToPosition(slideOffset: Float): Int {
        val slidePixelOffset = (slideOffset * mSlideRange).toInt()
        return measuredHeight - paddingBottom - mPanelHeight - slidePixelOffset
    }

    /**
     * 更新视觉差位置
     */
    private fun applyParallaxForCurrentSlideOffset() {
        if (mParallaxOffset > 0) {
            val offset = -(mParallaxOffset * Math.max(mSlideOffset, 0f)).toInt()
            mMainView!!.translationY = offset.toFloat()
        }
    }

    /**
     * 拖拽状态更新以及位置的更新
     */
    private fun onPanelDragged(newTop: Int) {
        setPanelStateInternal(PanelState.DRAGGING)
        //重新计算距离顶部偏移
        mSlideOffset = computeSlideOffset(newTop)
        //更新视觉差效果和分发事件
        applyParallaxForCurrentSlideOffset()
        //如果偏移是向上，覆盖则无效，需要增加main的高度
        val lp = mMainView!!.layoutParams
        val defaultHeight = height - paddingBottom - paddingTop - mPanelHeight
        if (mSlideOffset <= 0 && !mOverlayFlag) {
            lp.height = newTop - paddingBottom
            if (lp.height == defaultHeight) {
                lp.height = LayoutParams.MATCH_PARENT
            }
        } else if (lp.height != LayoutParams.MATCH_PARENT && !mOverlayFlag) {
            lp.height = LayoutParams.MATCH_PARENT
        }
        mMainView!!.requestLayout()
    }

    /**
     * 计算滑动偏移量
     */
    private fun computeSlideOffset(topPosition: Int): Float {
        val topBoundCollapsed = computePanelToPosition(0f)
        return (topBoundCollapsed - topPosition).toFloat() / mSlideRange
    }


    /**
     * 状态回调
     */
    fun setStateCallback(callback: StateCallback?) {
        stateCallback = callback
    }

    /**
     * 拖拽回调
     */
    inner class DragHelperCallback : ViewDragHelper.Callback() {

        private var slideUp = false

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return !isUnableToDrag && child === mDragView
        }

        override fun onViewDragStateChanged(state: Int) {
            if (mDragHelper == null || mDragHelper?.viewDragState != ViewDragHelper.STATE_IDLE) return
            mSlideOffset = computeSlideOffset(mDragView?.top ?: 0)
            applyParallaxForCurrentSlideOffset()
            when (mSlideOffset) {
                1f -> {
                    setPanelStateInternal(PanelState.EXPANDED)
                }
                0f -> {
                    setPanelStateInternal(PanelState.COLLAPSED)
                }
                else -> {
                    setPanelStateInternal(PanelState.ANCHORED)
                }
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            slideUp = dy > 0 //正为收缩，负为展开
            onPanelDragged(top)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val target = if (!slideUp) {
                if (mSlideOffset >= mAnchorPoint / 6) {
                    computePanelToPosition(mAnchorPoint)
                } else {
                    computePanelToPosition(0f)
                }
            } else {
                if (mSlideOffset >= mAnchorPoint / 3) {
                    computePanelToPosition(0f)
                } else {
                    computePanelToPosition(mAnchorPoint)
                }
            }
            if (mDragHelper != null) {
                mDragHelper?.settleCapturedViewAt(releasedChild.left, target)
            }
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return mSlideRange
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val original = computePanelToPosition(0f)
            val anchor = computePanelToPosition(mAnchorPoint)
            return top.coerceAtLeast(anchor).coerceAtMost(original)
        }
    }
}