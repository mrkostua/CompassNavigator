package com.example.simplecompassproject

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.example.simplecompassproject.util.ui.compass.CompassSensorsService
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by Kostiantyn Prysiazhnyi on 7/16/2019.
 */

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CompassSensorsServiceTest {
    private lateinit var mContext: Context
    private lateinit var mCompassSensorsService: CompassSensorsService

    private val mNorthAzimuth = 0.0f
    private val mKrakowLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 50.0647
        longitude = 19.9450
    }
    private val mKievLocation = Location(LocationManager.GPS_PROVIDER).apply {
        latitude = 50.4501
        longitude = 30.5234
    }

    @Before
    fun setUp() {
        mContext = RuntimeEnvironment.application.applicationContext
        mCompassSensorsService = CompassSensorsService(mContext)
    }


    @Test
    fun calculateCoordinatesAzFromNorthAzTest() {
        val expectedMaxResultKrakowKievAz = -86.44f + 5
        val expectedMinResultKrakowKievAz = -86.44f - 5
        val result = mCompassSensorsService.calculateCoordinatesAzFromNorthAz(
            mNorthAzimuth,
            mKrakowLocation,
            mKievLocation
        )
        assertThat(
            "azimuth for krakow kiev location calculation results are wrong",
            result,
            allOf(greaterThanOrEqualTo(expectedMinResultKrakowKievAz), lessThanOrEqualTo(expectedMaxResultKrakowKievAz))
        )

    }


}