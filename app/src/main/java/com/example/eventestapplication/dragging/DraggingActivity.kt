package com.example.eventestapplication.dragging

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventestapplication.R

/**
 * 用于实现可拖动半层
 */
class DraggingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dragging)
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, DraggingActivity::class.java)
            context.startActivity(intent)
        }
    }
}