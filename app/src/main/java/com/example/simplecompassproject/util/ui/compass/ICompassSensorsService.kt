package com.example.simplecompassproject.util.ui.compass

import android.location.Location

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
interface ICompassSensorsService {
    fun startListeningSensors(listener: CompassSensorsService.CompassListener)
    fun stopListeningSensors()
    fun calculateCoordinatesAzimuth(
            azimuth: Float,
            startLocation: Location,
            destinationLocation: Location
    ): Float
}