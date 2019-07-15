package com.example.simplecompassproject.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.simplecompassproject.data.LatLng
import timber.log.Timber
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
class CompassUtil(context: Context) : SensorEventListener, ICompassUtil {
    companion object {
        const val ALPHA = 0.97f //TODO try 0.8
    }

    var listener: CompassListener? = null

    private val mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val mOrientationsResultMatrix = FloatArray(3)
    private val mGravityVector = FloatArray(3)
    private val mGeomagneticVector = FloatArray(3)
    private val mRotationMatrix = FloatArray(9)
    private val mInclinationMatrix = FloatArray(9)

    private var mAzimuth: Double = 0.0
    private var mLatLngCoordinates: LatLng? = null

    override fun startListeningSensorsToNorth() {
        mLatLngCoordinates = null
        val accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        mSensorManager.run {
            registerListener(
                this@CompassUtil,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            registerListener(
                this@CompassUtil,
                magneticFieldSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun startListeningSensorsToCoordinates(latLong: LatLng) {
        mLatLngCoordinates = latLong
        startListeningSensorsToNorth()
    }

    /**
     * Unregister from all the sensor listeners.
     * can be called to save the battery, when compass is not in foreground.
     */
    override fun stopListeningSensors() {
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        synchronized(this) {
            when (event.sensor.type) {
                Sensor.TYPE_MAGNETIC_FIELD -> calculateMagneticField(event.values)
                Sensor.TYPE_ACCELEROMETER -> calculateAcceleration(event.values)
                else -> return
            }

            Timber.d("onSensorChanged event.sensor.type ${event.sensor.type}")
            val isRotationMatrixReady = SensorManager.getRotationMatrix(
                mRotationMatrix,
                mInclinationMatrix,
                mGravityVector,
                mGeomagneticVector
            )
            Timber.d("onSensorChanged + isRotationMatrixReady $isRotationMatrixReady")
            if (isRotationMatrixReady) {
                SensorManager.getOrientation(mRotationMatrix, mOrientationsResultMatrix)
                mLatLngCoordinates.let {
                    when (it) {
                        null -> listener?.newAzimuthResponse(calculateNorthAzimuth(mOrientationsResultMatrix[0]).toFloat())
                        else -> listener?.newAzimuthResponse(
                            calculateCoordinatesAzimuth(
                                mOrientationsResultMatrix[0],
                                it.latitude,
                                it.longitude
                            ).toFloat()
                        )
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit


    private fun calculateNorthAzimuth(orientationAzimuth: Float): Double {
        mAzimuth = Math.toDegrees(orientationAzimuth.toDouble())
        mAzimuth = (mAzimuth + 360) % 360
        return mAzimuth
    }

    private fun calculateCoordinatesAzimuth(
        orientationAzimuth: Float,
        destinationLat: Double,
        destionaLng: Double
    ): Double {
        mAzimuth = Math.toDegrees(orientationAzimuth.toDouble())
        mAzimuth = (mAzimuth + 360) % 360
        //mAzimuth -= calculateBearing() TODO not implemented
        return mAzimuth
    }

    /**
     * Measures the ambient magnetic field in the X, Y and Z axis.
     */
    private fun calculateMagneticField(magneticFields: FloatArray) {
        val xAxisMagneticFiled = magneticFields[0]
        val yAxisMagneticFiled = magneticFields[1]
        val zAxisMagneticFiled = magneticFields[2]

        mGeomagneticVector[0] = ALPHA * mGeomagneticVector[0] + (1 - ALPHA) * xAxisMagneticFiled
        mGeomagneticVector[1] = ALPHA * mGeomagneticVector[1] + (1 - ALPHA) * yAxisMagneticFiled
        mGeomagneticVector[2] = ALPHA * mGeomagneticVector[2] + (1 - ALPHA) * zAxisMagneticFiled
    }

    /**
     * Measures the acceleration applied to the device
     */
    private fun calculateAcceleration(accelerations: FloatArray) {
        val xAxisAcceleration = accelerations[0]
        val yAxisAcceleration = accelerations[1]
        val zAxisAcceleration = accelerations[2]

        mGravityVector[0] = ALPHA * mGravityVector[0] + (1 - ALPHA) * xAxisAcceleration
        mGravityVector[1] = ALPHA * mGravityVector[1] + (1 - ALPHA) * yAxisAcceleration
        mGravityVector[2] = ALPHA * mGravityVector[2] + (1 - ALPHA) * zAxisAcceleration
    }

    private fun calculateBearing(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Double {
        val longitudesDifference = Math.toRadians(endLng - startLng)
        val startLatRadians = Math.toRadians(startLat)
        val endLatRadians = Math.toRadians(endLat)

        val x = cos(startLatRadians) * sin(endLatRadians) -
                (sin(startLatRadians) * cos(endLatRadians) * cos(longitudesDifference))

        val y = sin(longitudesDifference) * cos(endLatRadians)
        return Math.toDegrees(atan2(y, x) + 360) % 360
    }

    interface CompassListener {
        fun newAzimuthResponse(azimuth: Float)
    }
}