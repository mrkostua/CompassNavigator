package com.example.simplecompassproject.util.ui

import androidx.lifecycle.ViewModel

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
open class BaseViewModel<N : Navigator> : ViewModel() {
    lateinit var navigator: N
}