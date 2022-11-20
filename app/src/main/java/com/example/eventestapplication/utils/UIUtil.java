package com.example.eventestapplication.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import org.jetbrains.annotations.Nullable;

/**
 * @author Perceiver
 * @date 2019/2/14 下午2:54
 */
public class UIUtil {

    private static final DisplayMetrics sMetrics = Resources.getSystem().getDisplayMetrics();

    /**
     * 屏幕尺寸变化后，可能更新metric更新不及时
     */
    public static void updateScreenDisplaySizeImmediately(@Nullable int width, @Nullable int height) {

    }

    /**
     * 获取屏幕宽度，该函数废弃，在部分机型会返回错误宽度
     * 推荐使用 getScreenWidth(context)
     */
    @Deprecated
    public static int getScreenWidth() {
        return getScreenWidth(XYUtilsCenter.getApp());
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     * Use getScreenHeight() instead
     *
     * @param context
     * @return
     */
    @Deprecated
    public static int getDisplayheightPixels(Context context) {
        return getScreenHeight();
    }

    /**
     * 获取屏幕高度，该函数废弃，在部分机型会返回错误高度
     * 推荐使用 getScreenHeight(context)
     */
    @Deprecated
    public static int getScreenHeight() {
        return getScreenHeight(XYUtilsCenter.getApp());
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int px2dp(float pxValue) {
        final float scale = sMetrics != null ? sMetrics.density : 1;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值
     *
     * @param dipValue
     * @return
     */
    @Deprecated
    public static int dip2px(Context context, float dipValue) {
        return dp2px(dipValue);
    }

    /**
     *
     * @param dipValue
     * @return
     *
     * {#com.xingin.utils.ext.dp}
     */
    @Deprecated
    public static int dp2px(float dipValue) {
        final float scale = sMetrics != null ? sMetrics.density : 1;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     *
     * @param spValue
     * @return
     *
     * {#com.xingin.utils.ext.sp}
     */
    @Deprecated
    public static int sp2px(float spValue) {
        final float fontScale = sMetrics != null ? sMetrics.scaledDensity : 1;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     *  {@link UIHandler#postDelay(long, Runnable)}
     */
    @Deprecated
    public static void postDelayed(Runnable runnable, long time) {
        UIHandler.postDelay(time, runnable);
    }

    /**
     *  {@link UIHandler#post(Runnable)}
     */
    @Deprecated
    public static void post(Runnable runnable) {
        UIHandler.post(runnable);
    }

    /**
     *  获取状态栏的高度
     *  use BarUtils getStatusBarHeight instead of
     *  {@link BarUtils#getStatusBarHeight()}
     */
    @Deprecated
    public static int getStatusBarHeight(Context context) {
        if (null == context) {
            return 0;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static int getDimensionPixelSize(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取TextView 的width
     */
    public static Float getTextWidth(String str, Typeface typeface, Float textSize) {
        if (TextUtils.isEmpty(str)) return 0f;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        return paint.measureText(str);
    }
}

