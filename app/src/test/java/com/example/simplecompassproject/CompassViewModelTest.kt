package com.example.simplecompassproject

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.simplecompassproject.ui.compass.CompassNavigator
import com.example.simplecompassproject.ui.compass.CompassViewModel
import com.example.simplecompassproject.util.ui.compass.ICompassSensorsService
import com.example.simplecompassproject.util.ui.location.ILocationService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by Kostiantyn Prysiazhnyi on 7/17/2019.
 */

class CompassViewModelTest {

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    private val mCompassUtilMock: ICompassSensorsService = mock()
    private val mLocationUtilMock: ILocationService = mock()
    private val mCompassNavigatorMock: CompassNavigator = mock()
    private val mCompassViewModel = CompassViewModel(mCompassUtilMock, mLocationUtilMock)

    @Before
    fun setUp() {
        mCompassViewModel.navigator = mCompassNavigatorMock
    }

    @Test
    fun `compass sensor update mode north`() {
        var previousAzimuth = Float.MAX_VALUE
        var testAzimuth = 11.11f
        mCompassViewModel.changeCompassModeToNorth()
        mCompassViewModel.onCompassSensorsUpdate(testAzimuth)

        val mockObserver: Observer<Pair<Float, Float>> = mock()
        mCompassViewModel.azimuthLd.observeForever(mockObserver)

        verify(mockObserver).onChanged(Pair(previousAzimuth, testAzimuth))

        //second compass sensors update
        previousAzimuth = testAzimuth
        testAzimuth = 12.12f
        mCompassViewModel.onCompassSensorsUpdate(testAzimuth)
        verify(mockObserver).onChanged(Pair(previousAzimuth, testAzimuth))
    }

    @Test
    fun `compass sensor update mode coordinates location not ready`() {
        val locationMock: Location = mock()
        val testAzimuth = 11.11f

        mCompassViewModel.changeCompassModeToCoordinates(locationMock)
        mCompassViewModel.onCompassSensorsUpdate(testAzimuth)

        val mockObserver: Observer<Pair<Float, Float>> = mock()
        mCompassViewModel.azimuthLd.observeForever(mockObserver)

        verify(mockObserver, never()).onChanged(any())
    }

    @Test
    fun `compass sensor update mode coordinates location ready`() {
        val locationMock: Location = mock()
        val testAzimuth = 11.11f

        with(mCompassViewModel) {
            changeCompassModeToCoordinates(locationMock)
            onLocationUpdates(locationMock)
            onCompassSensorsUpdate(testAzimuth)
        }
        val mockObserver: Observer<Pair<Float, Float>> = mock()
        mCompassViewModel.azimuthLd.observeForever(mockObserver)

        verify(mockObserver).onChanged(any())
    }

    @Test
    fun `destination location set when compass mode set`() {
        val locationMock: Location = mock()
        mCompassViewModel.changeCompassModeToCoordinates(locationMock)
        val mockObserver: Observer<String> = mock()
        mCompassViewModel.destinationLocationLd.observeForever(mockObserver)

        verify(mockObserver, never()).onChanged(any())
    }
}