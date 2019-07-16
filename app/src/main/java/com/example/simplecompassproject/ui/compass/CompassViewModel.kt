package com.example.simplecompassproject.ui.compass

import android.location.Location
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import com.example.simplecompassproject.util.ui.BaseViewModel
import com.example.simplecompassproject.util.ui.compass.CompassMode
import com.example.simplecompassproject.util.ui.compass.CompassUtil
import com.example.simplecompassproject.util.ui.compass.ICompassUtil
import com.example.simplecompassproject.util.ui.location.ILocationService
import com.example.simplecompassproject.util.ui.location.LocationService
import timber.log.Timber

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

class CompassViewModel(private val compassUtil: ICompassUtil, private val locationUtil: ILocationService) :
        BaseViewModel<CompassNavigator>(),
        CompassUtil.CompassListener, LocationService.LocationServiceListener {
    val azimuthLd = MutableLiveData<Pair<Float, Float>>()
    val destinationLocationLd = MutableLiveData<String>()

    private var currentLocation = locationUtil.getDefaultLocationOb()
    private var isCurrentLocationReady = false
    private var mDestinationLocation = locationUtil.getDefaultLocationOb()
    private var mCompassMode = CompassMode.NORTH

    private var mCoordinatesCalculatedAzimuth = 0f
    private var mPreviousAzimuth = Float.MAX_VALUE
    private var mPreviousDistance = 0f

    override fun newAzimuthResponse(azimuth: Float) {
        when (mCompassMode) {
            CompassMode.NORTH -> {
                azimuthLd.postValue(Pair(mPreviousAzimuth, azimuth))
                mPreviousAzimuth = azimuth
            }
            CompassMode.COORDINATES -> {
                if (isCurrentLocationReady) {
                    mCoordinatesCalculatedAzimuth = getCoordinatesAzimuth(azimuth)
                    azimuthLd.postValue(Pair(mPreviousAzimuth, mCoordinatesCalculatedAzimuth))
                    mPreviousAzimuth = mCoordinatesCalculatedAzimuth
                }
            }
        }

    }

    override fun locationListenerFailure() {
        navigator.showErrorLocationSetting()
    }

    override fun onLocationUpdates(location: Location) {
        if (isCurrentLocationReady.not()) {
            isCurrentLocationReady = true
        }
        currentLocation = location
        navigator.setCurrentLocationText(locationUtil.convertLocationToString(location))

        val newDistance = currentLocation.distanceTo(mDestinationLocation) / 1000
        navigator.showDistanceToDestinationText(newDistance, checkIfUserGettingCloser(newDistance))
        mPreviousDistance = newDistance
    }

    fun startCompassSensors() {
        compassUtil.listener = this
        compassUtil.startListeningSensors()
    }

    fun stopCompassSensors() {
        compassUtil.stopListeningSensors()
    }

    @UiThread
    fun showNavigateLatLngDialog() {
        if (navigator.checkLocationPermission()) {
            navigator.showNavigateLatLngDialog()
        } else {
            navigator.askForLocationPermission()
        }
    }

    fun startListeningToLocation() {
        Timber.i("startListeningToLocation mCompassMode $mCompassMode")
        if (mCompassMode == CompassMode.COORDINATES && navigator.checkLocationPermission()) {
            locationUtil.startLocationUpdates(this)
            navigator.setLocationViewsVisibility(true)
            if (isCurrentLocationReady.not()) {
                navigator.showLocationStateNotRead()
                navigator.showDistanceCalculationNotReady()
            }
        }
    }

    fun stopListeningToLocation() {
        locationUtil.stopLocationsUpdates()
        isCurrentLocationReady = false
        navigator.setLocationViewsVisibility(false)

    }

    fun changeCompassModeToNorth() {
        mCompassMode = CompassMode.NORTH
        stopListeningToLocation()
    }

    fun changeCompassModeToCoordinates(location: Location) {
        Timber.i("changeCompassModeToCoordinates")
        if (mCompassMode == CompassMode.NORTH) {
            mCompassMode = CompassMode.COORDINATES
            startListeningToLocation()
            mDestinationLocation = location
            destinationLocationLd.postValue(locationUtil.convertLocationToString(location))
        }
    }

    private fun getCoordinatesAzimuth(northAzimuth: Float) = compassUtil.calculateCoordinatesAzimuth(
            northAzimuth,
            currentLocation,
            mDestinationLocation
    )

    private fun checkIfUserGettingCloser(currentDistance: Float): Boolean {
        return currentDistance < mPreviousDistance
    }
}