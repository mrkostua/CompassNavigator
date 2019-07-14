package com.example.simplecompassproject.ui.compass

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.simplecompassproject.R
import com.example.simplecompassproject.databinding.ActivityCompassBinding
import com.example.simplecompassproject.ui.navigateLatLng.NavigateLatLngDialog
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class CompassActivity : AppCompatActivity(), CompassNavigator {
    private lateinit var binding: ActivityCompassBinding
    private lateinit var viewModel: CompassViewModel
    private val navigationLatLngDialog by lazy {
        NavigateLatLngDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compass)
        viewModel = getViewModel<CompassViewModel>().apply {
            binding.viewModel = this
            navigator = this@CompassActivity
        }
        binding.lifecycleOwner = this

        observeAzimuthChanges()
        binding.executePendingBindings()
    }


    override fun onResume() {
        super.onResume()
        viewModel.setupCompass()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopListeningToSensors()
    }

    override fun showNavigateLatLngDialog() {
        if (!navigationLatLngDialog.isAdded && !navigationLatLngDialog.isVisible) {
            navigationLatLngDialog.show(supportFragmentManager, "")
        }
    }

    private fun observeAzimuthChanges() {
        viewModel.azimuthLiveData.observe(this, Observer(::animateCompassHandsTo))
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
        binding.compassHandsIv.startAnimation(animation)
    }

    override fun back() {
        onBackPressed()
    }
}
