package com.example.simplecompassproject.util.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

fun Context.hideKeyboardFrom(view: View) {
    val imm = (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}