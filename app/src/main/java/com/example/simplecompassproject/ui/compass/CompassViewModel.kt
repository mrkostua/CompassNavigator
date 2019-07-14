package com.example.simplecompassproject.ui.compass

import androidx.lifecycle.MutableLiveData
import com.example.simplecompassproject.util.CompassUtil
import com.example.simplecompassproject.util.ui.BaseViewModel

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

class CompassViewModel(private val compassUtil: CompassUtil) : BaseViewModel<CompassNavigator>(),
    CompassUtil.CompassListener {
    private var previousAzimuth = 0f
    val azimuthLiveData = MutableLiveData<Pair<Float, Float>>()

    override fun newAzimuthResponse(azimuth: Float) {
        azimuthLiveData.postValue(Pair(previousAzimuth, azimuth))
        previousAzimuth = azimuth
    }

    fun setupCompass() {
        compassUtil.listener = this
        compassUtil.startListeningSensors()
    }

    fun stopListeningToSensors() {
        compassUtil.stopListeningSensors()
    }
}