package com.example.simplecompassproject

import android.app.Application
import timber.log.Timber

/**
 * Created by Kostiantyn Prysiazhnyi on 7/13/2019.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}