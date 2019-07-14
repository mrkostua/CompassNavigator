package com.example.simplecompassproject

import android.app.Application
import com.example.simplecompassproject.di.allModules
import org.koin.android.ext.android.startKoin
import timber.log.Timber

/**
 * Created by Kostiantyn Prysiazhnyi on 7/13/2019.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initializeKoin()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initializeKoin() {
        startKoin(this, allModules(this))
    }
}