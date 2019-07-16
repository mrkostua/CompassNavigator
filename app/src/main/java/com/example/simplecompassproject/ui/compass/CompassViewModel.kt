package com.example.simplecompassproject.ui.compass

import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import com.example.simplecompassproject.data.LatLng
import com.example.simplecompassproject.util.ui.BaseViewModel
import com.example.simplecompassproject.util.ui.compass.CompassMode
import com.example.simplecompassproject.util.ui.compass.CompassUtil
import com.example.simplecompassproject.util.ui.compass.ICompassUtil
import com.example.simplecompassproject.util.ui.location.ILocationService
import com.example.simplecompassproject.util.ui.location.LocationService

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

class CompassViewModel(private val compassUtil: ICompassUtil, private val locationUtil: ILocationService) :
        BaseViewModel<CompassNavigator>(),
        CompassUtil.CompassListener, LocationService.LocationServiceListener {
    val azimuthLiveData = MutableLiveData<Pair<Float, Float>>()

    private var mPreviousAzimuth = 0f
    private var currentLocation = LatLng(0.0, 0.0)
    private var destinationLocation = LatLng(0.0, 0.0)
    private var mCompassMode = CompassMode.NORTH
    private var coordinatesCalculatedAzimuth = 0f

    override fun newAzimuthResponse(azimuth: Float) {
        when (mCompassMode) {
            CompassMode.NORTH -> {
                azimuthLiveData.postValue(Pair(mPreviousAzimuth, azimuth))
                mPreviousAzimuth = azimuth
            }
            CompassMode.COORDINATES -> {
                coordinatesCalculatedAzimuth = getCoordinatesAzimuth(azimuth)
                azimuthLiveData.postValue(Pair(mPreviousAzimuth, coordinatesCalculatedAzimuth))
                mPreviousAzimuth = coordinatesCalculatedAzimuth
            }
        }

    }

    override fun locationListenerFailure() {
        navigator.showErrorLocationSetting()
    }

    override fun onLocationUpdates(location: LatLng) {
        currentLocation = location
    }

    fun startCompassSensors() {
        compassUtil.listener = this
        compassUtil.startListeningSensors()
    }

    fun stopCompassSensors() {
        compassUtil.stopListeningSensors()
    }

    @SuppressLint("MissingPermission")
    @UiThread
    fun showNavigateLatLngDialog() {
        if (navigator.checkLocationPermissionGranted()) {
            navigator.showNavigateLatLngDialog()
            locationUtil.startLocationUpdates()
        } else {
            navigator.askForLocationPermission()
        }
    }

    @RequiresPermission(value = "android.permission.ACCESS_FINE_LOCATION")
    fun startListeningToLocation() {
        if (mCompassMode == CompassMode.COORDINATES) {
            locationUtil.startLocationUpdates()
        }
    }

    fun stopListeningToLocation() {
        locationUtil.stopLocationsUpdates()
    }

    fun changeCompassModeToNorth() {
        mCompassMode = CompassMode.NORTH
    }

    fun changeCompassModeToCoordinates(latLng: LatLng) {
        currentLocation = latLng
        mCompassMode = CompassMode.COORDINATES
    }

    private fun getCoordinatesAzimuth(northAzimuth: Float) = compassUtil.calculateCoordinatesAzimuth(
            northAzimuth,
            currentLocation.latitude,
            currentLocation.longitude,
            destinationLocation.latitude,
            destinationLocation.longitude
    ).toFloat()
}