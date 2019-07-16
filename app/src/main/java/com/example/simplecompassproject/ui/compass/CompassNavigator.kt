package com.example.simplecompassproject.ui.compass

import com.example.simplecompassproject.util.ui.Navigator

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
interface CompassNavigator : Navigator {
    fun showNavigateLatLngDialog()
    fun askForLocationPermission()
    fun checkLocationPermission(): Boolean
    fun showErrorLocationSetting()
    fun setCurrentLocationText(location: String)
    fun showLocationStateNotRead()
    fun showDistanceToDestinationText(distance: Float, isGettingCloser: Boolean)
    fun showDistanceCalculationNotReady()
    fun setLocationViewsVisibility(isVisible: Boolean)
}