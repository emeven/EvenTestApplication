package com.example.eventestapplication.dragging

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.eventestapplication.R
import com.example.eventestapplication.dragging.panel.CustomPanelView
import com.example.eventestapplication.dragging.slidingup.ISlidingUpPanel
import com.example.eventestapplication.dragging.slidingup.SlidingUpPanelLayout
import com.example.eventestapplication.utils.UIUtil
import com.example.eventestapplication.utils.logep
import kotlinx.android.synthetic.main.activity_dragging_v2.*


/**
 * 用于实现可拖动半层
 */
class DraggingActivity : AppCompatActivity() {

    private val mWeatherList = ArrayList<String>()

    private val TAG = "DraggingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dragging_v2)
//        setContentView(R.layout.activity_dragging)

        sliding_up_panel_layout
        bg_layout
        initSlidingPanel()
        loadData()
    }

    private fun initSlidingPanel() {
        sliding_up_panel_layout.setPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSliding(panel: ISlidingUpPanel<*>?, slideProgress: Float) {
                logep("onPanelSliding")
            }

            override fun onPanelCollapsed(panel: ISlidingUpPanel<*>?) {
                logep("onPanelCollapsed")
            }

            override fun onPanelExpanded(panel: ISlidingUpPanel<*>?) {
                logep("onPanelExpanded")
            }

            override fun onPanelHidden(panel: ISlidingUpPanel<*>?) {
                logep("onPanelHidden")
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
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, DraggingActivity::class.java)
            context.startActivity(intent)
        }
    }
}