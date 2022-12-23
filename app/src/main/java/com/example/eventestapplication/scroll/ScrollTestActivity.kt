package com.example.eventestapplication.scroll

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventestapplication.R
import com.example.eventestapplication.utils.logep
import kotlinx.android.synthetic.main.activity_scroll_test.*

class ScrollTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_test)

        logep("canScrollVertically -1 = ${scroll_container.canScrollVertically(-1)}")
        logep("canScrollVertically 1 = ${scroll_container.canScrollVertically(1)}")

        scroll_container.post {
            logep("canScrollVertically post -1 = ${scroll_container.canScrollVertically(-1)}")
            logep("canScrollVertically post 1 = ${scroll_container.canScrollVertically(1)}")
        }

        val list = ArrayList<String>()
        list.add("香味")
        list.add("容量")
        tv_11.text = "请选择：$list"
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, ScrollTestActivity::class.java)
            context.startActivity(intent)
        }
    }
}