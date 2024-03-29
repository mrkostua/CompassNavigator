package com.example.simplecompassproject.stabs

import com.example.simplecompassproject.util.ui.compass.CompassSensorsService
import org.jetbrains.annotations.TestOnly

/**
 * Created by Kostiantyn Prysiazhnyi on 7/17/2019.
 */
@TestOnly
class CompassListenerStab : CompassSensorsService.CompassListener {
    var updatedAzimuth: Float? = null
    var isSensorEventOccurred: Boolean = false

    override fun onCompassSensorsUpdate(azimuth: Float) {
        updatedAzimuth = azimuth
        isSensorEventOccurred = true
    }

    fun resetResults() {
        updatedAzimuth = null
        isSensorEventOccurred = false
    }
}