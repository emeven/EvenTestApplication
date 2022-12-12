package com.example.eventestapplication

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import com.example.eventestapplication.bottomsheetbehavior.BottomSheetBehaviorActivity
import com.example.eventestapplication.dragging.DraggingActivity
import com.example.eventestapplication.span.CustomPriceSpan
import com.example.eventestapplication.span.RedNumTypefaceSpan
import com.example.eventestapplication.utils.dp
import com.example.eventestapplication.utils.dpF
import com.example.eventestapplication.utils.logep
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

    val redNumberMediumFont: Typeface by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Typeface.createFromAsset(assets, "fonts/REDNumber-Medium.ttf")
    }

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
        initFooterBtn()
        initPrice()
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

    private fun initFooterBtn() {
        val mainText = "预约提醒\n"
        val subText = "6.12 08:00 开售"
        val spanBuilder = SpannableString(mainText + subText)
        spanBuilder.setSpan(AbsoluteSizeSpan(10.dp), mainText.length, mainText.length + subText.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        footerBtn.text = spanBuilder
    }

    private fun initPrice() {
        val symbol = "¥"
        val priceIntegerPart = "450"
        val priceDecimalPart = ".67"
        val dealPriceText = "到手价"
        val dealPrice = "425.35"

        val spannableStringBuilder = SpannableStringBuilder(symbol)

        val priceIntegerPartSpan = SpannableString(priceIntegerPart)
        setRedNumTypeface(priceIntegerPartSpan, priceIntegerPartSpan.length)
        setCustomSpan(priceIntegerPartSpan, priceIntegerPartSpan.length, 22.dp, R.color.xhsTheme_colorGrayLevel1, 1.dp)
        spannableStringBuilder.append(priceIntegerPartSpan)

        val decimalPartSpan = SpannableString(priceDecimalPart)
        setRedNumTypeface(decimalPartSpan, decimalPartSpan.length)
        spannableStringBuilder.append(decimalPartSpan)

        // 到手价
        val dealPriceTextSpan = SpannableString(dealPriceText)
        setCustomSpan(dealPriceTextSpan, dealPriceText.length, 14.dp, R.color.xhsTheme_colorRed, 6.dp, 3.dpF)
        spannableStringBuilder.append(dealPriceTextSpan)

        // 到手价 ¥
        val symbolSpan = SpannableString(symbol)
        setCustomSpan(symbolSpan, symbol.length, 16.dp, R.color.xhsTheme_colorRed, 2.dp, 2.dpF)
        spannableStringBuilder.append(symbolSpan)

        // 到手价 价格
        val dealPriceSpan = SpannableString(dealPrice)
        setRedNumTypeface(dealPriceSpan, dealPriceSpan.length)
        setCustomSpan(dealPriceSpan, dealPriceSpan.length, 16.dp, R.color.xhsTheme_colorRed, 1.dp, 1.dpF)
        spannableStringBuilder.append(dealPriceSpan)

        priceText.text = spannableStringBuilder

        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        priceText.measure(spec, spec)
        logep("measuredWidth = ${priceText.measuredWidth}")

        priceText.post {
            logep("width = ${priceText.width}")
        }
    }

    private fun setRedNumTypeface(span: SpannableString, length: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            span.setSpan(RedNumTypefaceSpan(redNumberMediumFont), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun setCustomSpan(span: SpannableString, length: Int, fontSize: Int, @ColorRes colorRes: Int, marginStart: Int = 0, marginBottom: Float = 0f) {
        span.setSpan(CustomPriceSpan(fontSize, resources.getColor(colorRes)).apply {
            setMarginStart(marginStart)
            setBottomInterval(marginBottom)
        }, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}