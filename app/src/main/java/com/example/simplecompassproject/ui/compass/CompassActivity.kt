package com.example.simplecompassproject.ui.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
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

class CompassActivity : AppCompatActivity(), CompassNavigator, PermissionListener,
    NavigateLatLngDialog.OnNavigationChangedListener {
    private lateinit var mBinding: ActivityCompassBinding
    private lateinit var mViewModel: CompassViewModel
    private val mUiNavigator by inject<UiNavigator>()

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

    override fun onResume() {
        super.onResume()
        mViewModel.startCompassSensors()
        checkPermissionAndListenLocation()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.stopCompassSensors()
        mViewModel.stopListeningToLocation()
    }

    //region NavigationLatLngDialog callbacks
    override fun setCompassModeCoordinates(location: Location) {
        mViewModel.changeCompassModeToCoordinates(location)
    }

    override fun setCompassModeNorth() {
        mViewModel.changeCompassModeToNorth()
    }
    //endregion

    //region permission listener callbacks
    @SuppressLint("MissingPermission")
    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
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
    //endregion

    //region viewModel navigation
    override fun showNavigateLatLngDialog() {
        val navigationLatLngDialog = NavigateLatLngDialog().apply {
            mActivityCallback = this@CompassActivity
        }
        if (!navigationLatLngDialog.isAdded && !navigationLatLngDialog.isVisible) {
            navigationLatLngDialog.show(supportFragmentManager, "")
        }
    }

    override fun checkLocationPermission(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun askForLocationPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this)
            .onSameThread()
            .withErrorListener { Timber.e("Dexter permission check error $it") }
            .check()
    }

    override fun showErrorLocationSetting() {
        toast(R.string.compass_location_error_settings_inadequate)
    }

    override fun setCurrentLocationText(location: String) {
        mBinding.compassCurrentLocationTv.apply {
            setTextColor(ContextCompat.getColor(this@CompassActivity, R.color.colorAccent))
            text = location
        }
    }

    override fun showLocationStateNotRead() {
        mBinding.compassCurrentLocationTv.apply {
            setTextColor(ContextCompat.getColor(this@CompassActivity, R.color.red))
            text = getString(R.string.compass_location_not_established_message)
        }
    }

    override fun showDistanceToDestinationText(distance: Float, isGettingCloser: Boolean) {
        mBinding.compassDistanceToDestinationTv.apply {
            setTextColor(
                ContextCompat.getColor(
                    this@CompassActivity,
                    if (isGettingCloser) R.color.colorAccent else R.color.red
                )
            )

            text = getString(R.string.compass_distance_to_destination, distance)
        }
    }

    override fun showDistanceCalculationNotReady() {
        mBinding.compassDistanceToDestinationTv.apply {
            setTextColor(ContextCompat.getColor(this@CompassActivity, R.color.red))
            text = getString(R.string.compass_distance_not_no_calculated)
        }
    }

    override fun setLocationViewsVisibility(isVisible: Boolean) {
        if (isVisible) {
            mBinding.compassNavigationModeViewsG.visibility = View.VISIBLE
        } else {
            mBinding.compassNavigationModeViewsG.visibility = View.GONE
        }
    }
    //endregion

    //region init
    private fun init() {
        observeAzimuthChanges()
    }

    private fun observeAzimuthChanges() {
        mViewModel.azimuthLd.observe(this, Observer(::animateCompassHandsTo))
    }
    //endregion

    private fun animateCompassHandsTo(azimuths: Pair<Float, Float>) {
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

    private fun checkPermissionAndListenLocation() {
        if (checkLocationPermission()) {
            mViewModel.startListeningToLocation()
        }
    }

    override fun back() {
        onBackPressed()
    }
}
