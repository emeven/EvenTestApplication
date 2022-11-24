package com.example.eventestapplication.bottomsheetbehavior

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventestapplication.R
import com.example.eventestapplication.utils.StatusBarUtils.enableTransparentStatusBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_bottom_sheet_behavior.*

class BottomSheetBehaviorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_sheet_behavior)
        // 透明状态栏
        enableTransparentStatusBar()
        // bottom sheet
        addBottomSheetFragment()
    }

    private fun addBottomSheetFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.bottom_sheet_container, BottomSheetFragment.newInstance(BottomSheetBehavior.from(bottom_sheet_container)))
            .commit()
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, BottomSheetBehaviorActivity::class.java)
            context.startActivity(intent)
        }
    }
}