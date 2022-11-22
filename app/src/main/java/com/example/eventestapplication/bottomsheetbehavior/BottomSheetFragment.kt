package com.example.eventestapplication.bottomsheetbehavior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.example.eventestapplication.R
import com.example.eventestapplication.dragging.itembinder.HeadImageItemBinder
import com.example.eventestapplication.dragging.itembinder.TitleItemBinder
import com.example.eventestapplication.entities.TitleBean
import com.example.eventestapplication.utils.logep
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*

class BottomSheetFragment(val behavior: RecyclerViewBottomSheetBehavior<*>) : Fragment() {

    private val adapter = MultiTypeAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
        showSheetDialog()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        behavior.setState(RecyclerViewBottomSheetBehavior.STATE_COLLAPSED)
        behavior.peekHeight = this.resources.displayMetrics.heightPixels / 2 + 50
        behavior.isHideable = true
        behavior.setBottomSheetCallback(object : RecyclerViewBottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == RecyclerViewBottomSheetBehavior.STATE_HIDDEN) {
                    activity?.finish()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                logep("slideOffset = $slideOffset")
            }
        })
    }

    companion object {
        fun newInstance(behavior: RecyclerViewBottomSheetBehavior<*>): BottomSheetFragment {
            return BottomSheetFragment(behavior)
        }
    }
}