package com.example.simplecompassproject.ui.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.simplecompassproject.R
import com.example.simplecompassproject.data.LatLng
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

        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            mViewModel.startListeningToLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.stopCompassSensors()
        mViewModel.stopListeningToLocation()
    }

    //TODO nice  section separations with comments sections (some tool or styles)

    override fun setCompassModeCoordinates(latLng: LatLng) {
        mViewModel.changeCompassModeToCoordinates(latLng)
    }

    override fun setCompassModeNorth() {
        mViewModel.changeCompassModeToNorth()
    }

    override fun showNavigateLatLngDialog() {
        val navigationLatLngDialog = NavigateLatLngDialog().apply {
            mActivityCallback = this@CompassActivity
        }
        if (!navigationLatLngDialog.isAdded && !navigationLatLngDialog.isVisible) {
            navigationLatLngDialog.show(supportFragmentManager, "")
        }
    }

    override fun checkLocationPermissionGranted(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

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

    override fun askForLocationPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .onSameThread()
                .withErrorListener { Timber.d("Dexter permission check error $it") }
                .check()
    }

    override fun showErrorLocationSetting() {
        toast(R.string.compass_location_error_settings_inadequate)
    }

    private fun init() {
        observeAzimuthChanges()
    }

    private fun observeAzimuthChanges() {
        mViewModel.azimuthLiveData.observe(this, Observer(::animateCompassHandsTo))
    }

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

    private fun updateCurrentLocationTv() {
        //TODO create textView and handle visibility
    }

    override fun back() {
        onBackPressed()
    }
}
