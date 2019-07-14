package com.example.simplecompassproject.di

import com.example.simplecompassproject.App
import org.koin.dsl.module.module

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

fun appModule(app: App) = module {
    single { app }
}

fun allModules(app: App) = listOf(
    appModule(app),
    viewModules
)

