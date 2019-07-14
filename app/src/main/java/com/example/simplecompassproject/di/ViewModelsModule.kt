package com.example.simplecompassproject.di

import com.example.simplecompassproject.ui.compass.CompassViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

val viewModules = module {
    viewModel { CompassViewModel(get()) }
}