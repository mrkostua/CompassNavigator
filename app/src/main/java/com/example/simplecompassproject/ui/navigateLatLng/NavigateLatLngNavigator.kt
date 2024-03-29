package com.example.simplecompassproject.ui.navigateLatLng

import android.location.Location
import com.example.simplecompassproject.util.ui.Navigator

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
interface NavigateLatLngNavigator : Navigator {
    fun finishFillingInputs()
    fun showErrorLongitudeEmpty()
    fun showErrorLatitudeEmpty()
    fun showErrorLatitudeWrongInput()
    fun showErrorLongitudeWrongInput()
    fun hideLatitudeErrorText()
    fun hideLongitudeErrorText()
    fun getLongitudeInputText(): String
    fun getLatitudeInputText(): String
    fun setCompassModeNorth()
    fun setCompassModeCoordinates(location: Location)
    fun showErrorParsingLatLng()
}