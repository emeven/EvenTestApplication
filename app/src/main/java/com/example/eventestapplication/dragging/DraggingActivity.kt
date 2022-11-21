package com.example.eventestapplication.dragging

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.drakeet.multitype.MultiTypeAdapter
import com.example.eventestapplication.R
import com.example.eventestapplication.dragging.itembinder.TitleItemBinder
import com.example.eventestapplication.dragging.panel.CustomPanelView
import com.example.eventestapplication.dragging.slidingup.ISlidingUpPanel
import com.example.eventestapplication.dragging.slidingup.SlidingUpPanelLayout
import com.example.eventestapplication.entities.TitleBean
import com.example.eventestapplication.utils.UIUtil
import com.example.eventestapplication.utils.logep
import kotlinx.android.synthetic.main.activity_dragging_v2.*


/**
 * 用于实现可拖动半层
 */
class DraggingActivity : AppCompatActivity() {

    private val mWeatherList = ArrayList<String>()

    private val adapter = MultiTypeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dragging_v2)
//        setContentView(R.layout.activity_dragging)

        registerItem()

        initSlidingPanel()
        loadData()
    }

    private fun initSlidingPanel() {
        sliding_up_panel_layout.setPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSliding(panel: ISlidingUpPanel<*>?, slideProgress: Float) {
//                logep("onPanelSliding")
            }

            override fun onPanelCollapsed(panel: ISlidingUpPanel<*>?) {
//                logep("onPanelCollapsed")
            }

            override fun onPanelExpanded(panel: ISlidingUpPanel<*>?) {
//                logep("onPanelExpanded")
            }

            override fun onPanelHidden(panel: ISlidingUpPanel<*>?) {
//                logep("onPanelHidden")
            }

        })
    }

    private fun loadData() {
        mWeatherList.clear()
        val size = 1
        sliding_up_panel_layout.adapter = object : SlidingUpPanelLayout.Adapter() {
            override fun getItemCount(): Int = size

            override fun onCreateSlidingPanel(position: Int): ISlidingUpPanel<*> {
                val panel = CustomPanelView(context = this@DraggingActivity)
                panel.setPanelHeight(UIUtil.getScreenHeight(this@DraggingActivity) / 2)
                panel.initRecyclerView(adapter)
                return panel
            }

            override fun onBindView(panel: ISlidingUpPanel<*>?, position: Int) {
                val customPanel = panel as? CustomPanelView ?: return
                customPanel.setOnClickListener {
                    if (customPanel.getSlideState() != SlidingUpPanelLayout.EXPANDED) {
                        sliding_up_panel_layout.expandPanel()
                    } else {
                        sliding_up_panel_layout.collapsePanel()
                    }
                }
            }

        }
        loadPanelData()
    }

    private fun registerItem() {
        adapter.register(TitleBean::class.java, TitleItemBinder())
    }

    private fun loadPanelData() {
        val dataList = listOf(
            TitleBean("0 cdjh"),
            TitleBean("1 chudiebdujcnik"),
            TitleBean("2 胡迪和随程度"),
            TitleBean("3 崔对身边的"),
            TitleBean("4 从南大街")
        )
        adapter.items = dataList
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, DraggingActivity::class.java)
            context.startActivity(intent)
        }
    }
}