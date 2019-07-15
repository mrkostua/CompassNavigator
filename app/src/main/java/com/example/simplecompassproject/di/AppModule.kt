package com.example.simplecompassproject.di

import com.example.simplecompassproject.App
import com.example.simplecompassproject.util.ui.UiNavigator
import com.example.simplecompassproject.util.ui.compass.CompassUtil
import com.example.simplecompassproject.util.ui.compass.ICompassUtil
import com.example.simplecompassproject.util.ui.location.CoordinatesValidator
import com.example.simplecompassproject.util.ui.location.ILocationService
import com.example.simplecompassproject.util.ui.location.LocationService
import org.koin.dsl.module.module

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

fun appModule(app: App) = module {
    single { app }
    factory { CompassUtil(app) as ICompassUtil }
    factory { LocationService(app) as ILocationService }

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

