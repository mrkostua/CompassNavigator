package com.example.simplecompassproject.di

import com.example.simplecompassproject.App
import com.example.simplecompassproject.util.CompassUtil
import com.example.simplecompassproject.util.CoordinatesValidator
import com.example.simplecompassproject.util.ui.UiNavigator
import org.koin.dsl.module.module

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

fun appModule(app: App) = module {
    single { app }
    single { CompassUtil(app) }
}

fun commonsModule() = module {
    factory { CoordinatesValidator() }
    single { UiNavigator() }
}

fun allModules(app: App) = listOf(
    appModule(app),
    viewModules,
    commonsModule()
)

