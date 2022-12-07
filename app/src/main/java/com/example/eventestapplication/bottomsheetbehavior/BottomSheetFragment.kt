package com.example.eventestapplication.bottomsheetbehavior

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.example.eventestapplication.R
import com.example.eventestapplication.dragging.itembinder.HeadImageItemBinder
import com.example.eventestapplication.dragging.itembinder.TitleItemBinder
import com.example.eventestapplication.entities.TitleBean
import com.example.eventestapplication.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*

class BottomSheetFragment(private val behavior: BottomSheetBehavior<*>) : Fragment() {

    private val adapter = MultiTypeAdapter()

    private val coverRect = Rect()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSheetDialog()
        registerItem()
        initRecyclerView()
        loadPanelData()
    }

    private fun registerItem() {
        adapter.register(TitleBean::class.java, TitleItemBinder())
        adapter.register(String::class.java, HeadImageItemBinder())
    }

    private fun initRecyclerView() {
        bottom_sheet_rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        bottom_sheet_rv.adapter = adapter
        bottom_sheet_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 上滑 dy > 0 ; 下滑 dy < 0
                val layoutManager = bottom_sheet_rv.layoutManager as LinearLayoutManager
                if (layoutManager.findFirstVisibleItemPosition() != 0) return
                logep("dy = $dy, progress = ${getProgress()}")
                cover_view.alpha = 1 - getProgress()
            }
        })
        bottom_sheet.setTopRoundCorner(12.dpF)
    }

    private fun getProgress(): Float {
        val layoutManager = bottom_sheet_rv.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstVisibleItemPosition() != 0) return 1f
        val viewHolder = bottom_sheet_rv.findViewHolderForAdapterPosition(0)
        val screenWidth = UIUtil.getScreenWidth(context).toFloat()
        viewHolder?.itemView?.apply {
            val rect = Rect()
            if (getGlobalVisibleRect(rect) && !coverRect.isEmpty) {
                val visibleHeight = rect.bottom - coverRect.bottom
                logep("rect bottom = ${rect.bottom}, coverRect = ${coverRect.bottom}")
                return if (visibleHeight > 0) visibleHeight / (screenWidth - coverRect.height()) else 0f
            }
        }
        return 0f
    }

    private fun loadPanelData() {
        val dataList = listOf(
            "",
            TitleBean("0 cdjh"),
            TitleBean("1 chudiebdujcnik"),
            TitleBean("2 胡迪和随程度"),
            TitleBean("3 崔对身边的"),
            TitleBean("4 从南大街")
        )
        adapter.items = dataList
        adapter.notifyDataSetChanged()
    }

    private fun showSheetDialog() {
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.peekHeight = this.resources.displayMetrics.heightPixels / 2 + 50
        behavior.isHideable = true
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        logep("STATE_EXPANDED")
                        cover_view.show()
                        cover_view.alpha = 0f
                        cover_view.post {
                            cover_view.getGlobalVisibleRect(coverRect)
                        }
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        logep("STATE_COLLAPSED")
                        cover_view.hide()
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        logep("STATE_HIDDEN")
                        activity?.finish()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                logep("slideOffset = $slideOffset")
                offset_view.alpha = slideOffset
            }

        })
    }

    companion object {
        fun newInstance(behavior: BottomSheetBehavior<*>): BottomSheetFragment {
            return BottomSheetFragment(behavior)
        }
    }
}