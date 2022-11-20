package com.example.eventestapplication.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author Perceiver
 * @date 2019/2/14 下午2:53
 */
public class UIHandler {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static void post(final Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void post(final Runnable runnable, Object token) {
        Message message = Message.obtain(sHandler, runnable);
        message.obj = token;
        sHandler.sendMessageDelayed(message, 0);
    }

    public static void postDelay(long millis, final Runnable runnable, Object token) {
        Message message = Message.obtain(sHandler, runnable);
        message.obj = token;
        sHandler.sendMessageDelayed(message, millis);
    }

    public static void postImmediately(final Runnable runnable, Object token) {
        if (Thread.currentThread() == sHandler.getLooper().getThread()) {
            runnable.run();
        } else {
            post(runnable, token);
        }
    }

    /**
     * 如果当前已经处于主线程，则立即执行
     */
    public static void postImmediately(final Runnable runnable) {
        if (Thread.currentThread() == sHandler.getLooper().getThread()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    public static void postDelay(long millis, final Runnable runnable) {
        sHandler.postDelayed(runnable, millis);
    }

//    /**
//     * @deprecated use {@link com.xingin.utils.async.LightExecutor#postIdle}
//     */
    @Deprecated()
    public static void postIdle(final Runnable r) {
        Looper.myQueue().addIdleHandler(() -> {
            r.run();
            return false;
        });
    }

    /**
     * removeRunnable
     */
    public static void removeRunnable(final Runnable runnable) {
        sHandler.removeCallbacks(runnable);
    }

    /**
     * 移除所有消息
     */
    public static void removeAllMsg(Object token) {
        sHandler.removeCallbacksAndMessages(token);
    }
}
