package com.example.simplecompassproject.util.ui.compass

import android.location.Location

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
interface ICompassUtil {
    fun startListeningSensors()
    fun stopListeningSensors()
    var listener: CompassUtil.CompassListener?
    fun calculateCoordinatesAzimuth(
            azimuth: Float,
            startLocation: Location,
            destinationLocation: Location
    ): Float
}