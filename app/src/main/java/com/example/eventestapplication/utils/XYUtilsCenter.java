package com.example.eventestapplication.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.FileProvider;

//import com.xingin.utils.core.SPUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Perceiver
 * @date 2018/10/19 下午5:32
 * <p>
 * utils about initialization
 */
public final class XYUtilsCenter {

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private static final String PERMISSION_ACTIVITY_CLASS_NAME =
            "com.xingin.common.util.PermissionUtils$PermissionActivity";

    private static final ActivityLifecycleImpl ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();

    private static final Handler UTIL_HANDLER = new Handler(Looper.getMainLooper());

//    public static OnXYUtilsCallbackListener sXYUtilsCallbackListener = null;

    /**
     * 当前展现页面的标记
     * onResume 和 onPause 时put和remove。
     * key：Activity名字
     * value：业务模块
     */
    private static Map<String, String> sPageTag = new HashMap<>();

    public static boolean isDebug = false;
    public static boolean sEnableBinderOptimize = false;

    private XYUtilsCenter() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param context context
     */
    public static void init(final Context context) {
        if (context == null) {
            init(getApplicationByReflect());
            return;
        }
        init((Application) context.getApplicationContext());
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param app application
     */
    public static void init(final Application app) {
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
            sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } else {
            if (app != null && app.getClass() != sApplication.getClass()) {
                sApplication.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
                ACTIVITY_LIFECYCLE.mActivityList.clear();
                sApplication = app;
                sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
            }
        }
    }

    /**
     * set the debug status
     * */
    public static void enableDebug(boolean isDebug) {
        System.out.println("jimmy, XYUtilsCenter.enableDebug(), isDebug = " + isDebug);
        XYUtilsCenter.isDebug = isDebug;
    }

    /**
     * enable binder optimize
     * */
    public static void enableBinderOptimize(boolean enable) {
        XYUtilsCenter.sEnableBinderOptimize = enable;
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

//    /**
//     * register CLog CallBack
//     */
//    public static void registerXYUtilsListener(OnXYUtilsCallbackListener xyUtilsCallbackListener) {
//        sXYUtilsCallbackListener = xyUtilsCallbackListener;
//    }

    public static ActivityLifecycleImpl getActivityLifecycle() {
        return ACTIVITY_LIFECYCLE;
    }

    public static LinkedList<Activity> getActivityList() {
        return ACTIVITY_LIFECYCLE.mActivityList;
    }

    public static Context getTopActivityOrApp() {
        if (isAppForeground()) {
            Activity topActivity = ACTIVITY_LIFECYCLE.getTopActivity();
            return topActivity == null ? XYUtilsCenter.getApp() : topActivity;
        } else {
            return XYUtilsCenter.getApp();
        }
    }

    public static void setPageTag(Activity activity, String tag) {
        if (activity != null) {
            String key = activity.getClass().getSimpleName();
            if (TextUtils.isEmpty(tag)) {
                sPageTag.remove(key);
            } else {
                sPageTag.put(key, tag);
            }
        }
    }

    public static String getCurrentPageTag() {
        String key = XYUtilsCenter.getTopActivityOrApp().getClass().getSimpleName();
        if (sPageTag.containsKey(key)) {
            String value = sPageTag.get(key);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }
        return key;
    }

    public static boolean isAppForeground() {
        return !ACTIVITY_LIFECYCLE.isBackground();
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            XYUtilsCenter.UTIL_HANDLER.post(runnable);
        }
    }

    public static void runOnUiThreadDelayed(final Runnable runnable, long delayMillis) {
        XYUtilsCenter.UTIL_HANDLER.postDelayed(runnable, delayMillis);
    }

    static String getCurrentProcessName() {
        String name = getCurrentProcessNameByFile();
        if (!TextUtils.isEmpty(name)) return name;
        name = getCurrentProcessNameByAms();
        if (!TextUtils.isEmpty(name)) return name;
        name = getCurrentProcessNameByReflect();
        return name;
    }

    static void fixSoftInputLeaks(final Window window) {
        InputMethodManager imm =
                (InputMethodManager) XYUtilsCenter.getApp().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        String[] leakViews = new String[]{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"};
        for (String leakView : leakViews) {
            try {
                Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                if (leakViewField == null) continue;
                if (!leakViewField.isAccessible()) {
                    leakViewField.setAccessible(true);
                }
                Object obj = leakViewField.get(imm);
                if (!(obj instanceof View)) continue;
                View view = (View) obj;
                if (view.getRootView() == window.getDecorView().getRootView()) {
                    leakViewField.set(imm, null);
                }
            } catch (Throwable ignore) {/**/}
        }
    }

//    static SPUtils getSpUtils4Utils() {
//        return SPUtils.getInstance("Utils");
//    }

    ///////////////////////////////////////////////////////////////////////////
    // private method
    ///////////////////////////////////////////////////////////////////////////
    private static String getCurrentProcessNameByFile() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentProcessNameByAms() {
        ActivityManager am = (ActivityManager) XYUtilsCenter.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return "";
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) return "";
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName;
                }
            }
        }
        return "";
    }

    private static String getCurrentProcessNameByReflect() {
        String processName = "";
        try {
            Application app = XYUtilsCenter.getApp();
            Field loadedApkField = app.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(app);

            Field activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(loadedApk);

            Method getProcessName = activityThread.getClass().getDeclaredMethod("getProcessName");
            processName = (String) getProcessName.invoke(activityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processName;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    public static final class FileProvider4UtilCode extends FileProvider {

        @Override
        public boolean onCreate() {
            XYUtilsCenter.init(getContext());
            return true;
        }
    }

    public static class ActivityLifecycleImpl implements ActivityLifecycleCallbacks {

        final LinkedList<Activity> mActivityList = new LinkedList<>();
        /**
         * 高效的获取mTopActivity
         */
        public Activity mTopActivity = null;
        /**
         * 高效的获取mTopActivityFullName
         */
        public String mTopActivityFullName = "";

        final ConcurrentHashMap<Object, OnAppStatusChangedListener> mStatusListenerMap = new ConcurrentHashMap<>();

        final ConcurrentHashMap<Activity, Set<OnActivityDestroyedListener>> mDestroyedListenerMap = new ConcurrentHashMap<>();

        private int mForegroundCount = 0;
        private int mConfigCount = 0;
        private boolean mIsBackground = true;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (!mIsBackground) {
                setTopActivity(activity);
            }
            if (mConfigCount < 0) {
                ++mConfigCount;
            } else {
                ++mForegroundCount;
            }
            if (mIsBackground) {
                mIsBackground = false;
                postStatus(true, activity);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {/**/

        }

        boolean isBackground() {
            return mIsBackground;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity.isChangingConfigurations()) {
                --mConfigCount;
            } else {
                --mForegroundCount;
                if (mForegroundCount <= 0) {
                    mIsBackground = true;
                    postStatus(false, activity);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {/**/}

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityList.remove(activity);
            consumeOnActivityDestroyedListener(activity);
        }

        public Activity getTopActivity() {
            if (!mActivityList.isEmpty()) {
                final Activity topActivity = mActivityList.getLast();
                if (topActivity != null) {
                    return topActivity;
                }
            }
            Activity topActivityByReflect = getTopActivityByReflect();
            if (topActivityByReflect != null) {
                setTopActivity(topActivityByReflect);
            }
            return topActivityByReflect;
        }

        public void addOnAppStatusChangedListener(final Object object,
                                                  final OnAppStatusChangedListener listener) {
            mStatusListenerMap.put(object, listener);
        }

        public void removeOnAppStatusChangedListener(final Object object) {
            mStatusListenerMap.remove(object);
        }

        public void removeOnActivityDestroyedListener(final Activity activity) {
            if (activity == null) return;
            mDestroyedListenerMap.remove(activity);
        }

        public void addOnActivityDestroyedListener(final Activity activity,
                                                   final OnActivityDestroyedListener listener) {
            if (activity == null || listener == null) return;
            Set<OnActivityDestroyedListener> listeners;
            if (!mDestroyedListenerMap.containsKey(activity)) {
                listeners = new HashSet<>();
                mDestroyedListenerMap.put(activity, listeners);
            } else {
                listeners = mDestroyedListenerMap.get(activity);
                if (listeners.contains(listener)) return;
            }
            listeners.add(listener);
        }

        public void postStatus(final boolean isForeground, final Activity activity) {
            if (mStatusListenerMap.isEmpty()) return;
            for (OnAppStatusChangedListener onAppStatusChangedListener : mStatusListenerMap.values()) {
                if (onAppStatusChangedListener == null) return;
                if (isForeground) {
                    onAppStatusChangedListener.onForeground(activity);
                } else {
                    onAppStatusChangedListener.onBackground();
                }
            }
        }

        public void setTopActivity(final Activity activity) {
            if (PERMISSION_ACTIVITY_CLASS_NAME.equals(activity.getClass().getName())) return;
            mTopActivity = activity;
            if (activity != null) {
                mTopActivityFullName = activity.getClass().getCanonicalName();
            }

            if (mActivityList.contains(activity)) {
                if (!mActivityList.getLast().equals(activity)) {
                    mActivityList.remove(activity);
                    mActivityList.addLast(activity);
                }
            } else {
                mActivityList.addLast(activity);
            }
        }

        public void consumeOnActivityDestroyedListener(Activity activity) {
            Set<Map.Entry<Activity, Set<OnActivityDestroyedListener>>> entries = mDestroyedListenerMap.entrySet();
            for (Map.Entry<Activity, Set<OnActivityDestroyedListener>> entry : entries) {
                if (entry.getKey() == activity) {
                    Set<OnActivityDestroyedListener> value = entry.getValue();
                    for (OnActivityDestroyedListener listener : value) {
                        listener.onActivityDestroyed(activity);
                    }
                    removeOnActivityDestroyedListener(activity);
                }
            }
        }

        public Activity getTopActivityByReflect() {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
                Field activitiesField = activityThreadClass.getDeclaredField("mActivityList");
                activitiesField.setAccessible(true);
                Map activities = (Map) activitiesField.get(activityThread);
                if (activities == null) return null;
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        return (Activity) activityField.get(activityRecord);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public abstract static class Task<Result> implements Runnable {

        private static final int NEW         = 0;
        private static final int COMPLETING  = 1;
        private static final int CANCELLED   = 2;
        private static final int EXCEPTIONAL = 3;

        private volatile int state = NEW;

        abstract Result doInBackground();

        private Callback<Result> mCallback;

        public Task(final Callback<Result> callback) {
            mCallback = callback;
        }

        @Override
        public void run() {
            try {
                final Result t = doInBackground();

                if (state != NEW) return;
                state = COMPLETING;
                UTIL_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onCall(t);
                    }
                });
            } catch (Throwable th) {
                if (state != NEW) return;
                state = EXCEPTIONAL;
            }
        }

        public void cancel() {
            state = CANCELLED;
        }

        public boolean isDone() {
            return state != NEW;
        }

        public boolean isCanceled() {
            return state == CANCELLED;
        }
    }

    public interface Callback<T> {
        void onCall(T data);
    }

    public interface OnAppStatusChangedListener {
        void onForeground(Activity activity);

        void onBackground();
    }

    public interface OnActivityDestroyedListener {
        void onActivityDestroyed(Activity activity);
    }

//    @Deprecated
//    public static void trackPage(String tag, String message) {
//        if (isDebug && sXYUtilsCallbackListener != null) {
//            sXYUtilsCallbackListener.trackPage(tag, message);
//        }
//    }
}