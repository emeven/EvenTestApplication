package com.example.eventestapplication.utils

import android.util.Log

inline fun logep(msg: String) {
    Log.d("even_p", msg)
}

fun costTiming(callback: () -> Unit) {
    val startTime = System.currentTimeMillis()
    callback.invoke()
    logep("${System.currentTimeMillis() - startTime}")
}