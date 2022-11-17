package com.example.eventestapplication

import android.app.Application
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            allowOverride(override = false)
        }
    }
}