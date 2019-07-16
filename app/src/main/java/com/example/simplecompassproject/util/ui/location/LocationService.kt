package com.example.simplecompassproject.util.ui.location

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.example.simplecompassproject.data.LatLng
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import timber.log.Timber

/**
 * Created by Kostiantyn Prysiazhnyi on 7/15/2019.
 */
class LocationService(private val context: Context) : LocationCallback(), ILocationService {
    companion object {
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000L

        /**
         * location updates will be received if another app is requesting
         * the locations faster than your app can handle
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000L
    }

    override var listener: LocationServiceListener? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private val mLocationCallback: LocationCallback = this
    private var mIsLocationActive = false
    private var lastKnownLocation = LatLng(0.0, 0.0)

    init {
        mIsLocationActive = false
        initLocationClient()
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
        locationResult?.lastLocation?.let {
            lastKnownLocation.latitude = it.latitude
            lastKnownLocation.longitude = it.longitude
            listener?.onLocationUpdates(lastKnownLocation)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun startLocationUpdates() {
        if (mIsLocationActive.not()) {
            startListeningLocationUpdates()
        }
    }

    override fun stopLocationsUpdates() {
        if (mIsLocationActive) {
            stopListeningLocationUpdates()
        }
    }

    private fun initLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mSettingsClient = LocationServices.getSettingsClient(context)
        mLocationRequest = LocationRequest().apply {
            interval =
                    UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval =
                    FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mLocationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .build()
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun startListeningLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener {
                    mIsLocationActive = true
                    mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                    )
                }
                .addOnFailureListener { exception ->
                    when ((exception as ApiException).statusCode) {
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            Timber.e(exception, "Error in listening for location updates")
                            listener?.locationListenerFailure()
                        }
                        else -> Timber.e(exception, "Error in listening for location updates")
                    }
                }
    }

    private fun stopListeningLocationUpdates() {
        mIsLocationActive = false
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    interface LocationServiceListener {
        fun locationListenerFailure()
        fun onLocationUpdates(location: LatLng)
    }
}