package com.example.simplecompassproject.util.ui.compass

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
interface ICompassUtil {
    fun startListeningSensorsToNorth()
    fun stopListeningSensors()
    var listener: CompassUtil.CompassListener?
    fun calculateCoordinatesAzimuth(
        azimuth: Float,
        startLat: Double,
        startLng: Double,
        destinationLat: Double,
        destinationLng: Double
    ): Double
}