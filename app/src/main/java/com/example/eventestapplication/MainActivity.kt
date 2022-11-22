package com.example.eventestapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventestapplication.bottomsheetbehavior.BottomSheetBehaviorActivity
import com.example.eventestapplication.dragging.DraggingActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //注入的模块
//    private val myAppModules = module {
//        //类名为注入类限定符
//        factory { User(get()) }
//        factory { User2(get()) }
//    }

//    private val myAppModules2 = module {
//        scope(named("自定义作用域申明")) {
//            scoped { User(get()) }
//            scoped { User2(get()) }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        getKoin().loadModules(listOf( myAppModules2))
//
//        try {
////            val user1: User = get()
//
//            //2获取自定义作用域的注入类
//            val scope = getKoin().createScope("scopeID", named("自定义作用域申明"))
//            val user2 by inject<User>()
//
//
////            //3 获取自定义作用域的注入类
////            val scope3 = getKoin().createScope("ID", named("自定义作用域申明"))
////            val user3: User = scope3.get()
//        } catch (e: Throwable) {
//            Log.e("", e.toString())
//        }


//        Log.d("main", "user1 = ${user1.name}, user2 = ${user2.name}, user3 = ${user3.name}")
//        Log.d("main", "user1 = ${user1.name}, user2 = ${user2.name}")

        initClicks()

    }

    private fun initClicks() {
        // 拖拽半层
        draggingDialog.setOnClickListener {
            DraggingActivity.start(this)
        }

        // bottom sheet behavior
        bottom_sheet_fragment.setOnClickListener {
            BottomSheetBehaviorActivity.start(this)
        }
    }

}