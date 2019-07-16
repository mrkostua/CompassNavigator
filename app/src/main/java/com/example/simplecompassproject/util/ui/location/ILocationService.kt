package com.example.simplecompassproject.util.ui.location

import android.location.Location
import androidx.annotation.RequiresPermission

/**
 * Created by Kostiantyn Prysiazhnyi on 7/16/2019.
 */
interface ILocationService {
    @RequiresPermission(value = "android.permission.ACCESS_FINE_LOCATION")
    fun startLocationUpdates(listener: LocationService.LocationServiceListener)

    fun stopLocationsUpdates()
    fun convertLocationToString(loc: Location): String
    fun getDefaultLocationOb(): Location
}