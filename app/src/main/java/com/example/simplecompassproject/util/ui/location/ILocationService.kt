package com.example.simplecompassproject.util.ui.location

import androidx.annotation.RequiresPermission

/**
 * Created by Kostiantyn Prysiazhnyi on 7/16/2019.
 */
interface ILocationService {
    var listener: LocationService.LocationServiceListener?

    @RequiresPermission(value = "android.permission.ACCESS_FINE_LOCATION")
    fun startLocationUpdates()

    fun stopLocationsUpdates()
}