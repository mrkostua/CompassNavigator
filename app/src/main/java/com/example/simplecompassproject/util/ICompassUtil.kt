package com.example.simplecompassproject.util

import com.example.simplecompassproject.data.LatLng

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
interface ICompassUtil {
    fun startListeningSensorsToNorth()
    fun stopListeningSensors()
    fun startListeningSensorsToCoordinates(latLong: LatLng)
}