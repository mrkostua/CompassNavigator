package com.example.simplecompassproject.ui.compass

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.simplecompassproject.R
import com.example.simplecompassproject.databinding.ActivityCompassBinding
import com.example.simplecompassproject.ui.navigateLatLng.NavigateLatLngDialog
import com.example.simplecompassproject.util.ui.UiNavigator
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class CompassActivity : AppCompatActivity(), CompassNavigator, PermissionListener {
    private lateinit var mBinding: ActivityCompassBinding
    private lateinit var mViewModel: CompassViewModel
    private val mUiNavigator by inject<UiNavigator>()

    //location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mSettingsClient: SettingsClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mIsListeningToLocaton = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_compass)
        mBinding.lifecycleOwner = this
        mViewModel = getViewModel<CompassViewModel>().apply {
            mBinding.viewModel = this
            navigator = this@CompassActivity
        }
        init()
        mBinding.executePendingBindings()
    }

    private fun init() {
        mIsListeningToLocaton = false
        observeAzimuthChanges()
        initLocation()
    }


    override fun onResume() {
        super.onResume()
        mViewModel.setupCompass()
        if (mIsListeningToLocaton) {
            startListeningLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.stopListeningToSensors()
        if (mIsListeningToLocaton) {
            stopListeningLocationUpdates()
        }
    }

    override fun showNavigateLatLngDialog() {
        val navigationLatLngDialog = NavigateLatLngDialog()
        if (!navigationLatLngDialog.isAdded && !navigationLatLngDialog.isVisible) {
            navigationLatLngDialog.show(supportFragmentManager, "")
        }
    }

    override fun checkLocationPermissionGranted(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        startListeningLocationUpdates()
        showNavigateLatLngDialog()
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
        token?.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        mBinding.containerCL.longSnackbar(R.string.location_permissions_description, R.string.settings) {
            startActivity(mUiNavigator.getPermissionSettingsIntent())
        }.also {
            it.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        }
    }

    override fun askForLocationPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this)
            .onSameThread()
            .withErrorListener { Timber.d("Dexter permission check error $it") }
            .check()
    }

    private fun observeAzimuthChanges() {
        mViewModel.azimuthLiveData.observe(this, Observer(::animateCompassHandsTo))
    }

    private fun animateCompassHandsTo(azimuths: Pair<Float, Float>) {
        Timber.d("animateCompassHandsTo to previousAzimuth ${azimuths.first} newAzimuth ${azimuths.second}")
        val animation = RotateAnimation(
            -azimuths.first,
            -azimuths.second,
            Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 500
            repeatCount = 0
            fillAfter = true
        }
        mBinding.compassHandsIv.startAnimation(animation)
    }

    private fun initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                updateCurrentLocationView()
                //TODO update UI send data compassUtil...
            }
        }
        mLocationRequest = LocationRequest().apply {
            //TODO create const for those values and move all this logic outside of activity how ???
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(this)
            mLocationSettingsRequest = builder.build()
        }
    }

    private fun updateCurrentLocationView() {
        /*      if (mCurrentLocation != null) {
                  txtLocationResult.setText(
                      "Lat: " + mCurrentLocation.getLatitude() + ", " +
                              "Lng: " + mCurrentLocation.getLongitude()
                  );

                  // giving a blink animation on TextView
                  txtLocationResult.setAlpha(0);
                  txtLocationResult.animate().alpha(1).setDuration(300);*/
    }

    override fun startListeningLocationUpdates() {
        mSettingsClient?.let {
            it.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener {
                    mIsListeningToLocaton = true
                    val permState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    if (permState == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    when ((exception as ApiException).statusCode) {
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            Timber.e(exception, "Error in listening for location updates")
                            toast(R.string.compass_location_error_settings_inadequate)
                        }
                        else -> Timber.e(exception, "Error in listening for location updates")
                    }
                }

        }
    }

    fun stopListeningLocationUpdates() {
        mIsListeningToLocaton = false
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }


    override fun back() {
        onBackPressed()
    }
}
