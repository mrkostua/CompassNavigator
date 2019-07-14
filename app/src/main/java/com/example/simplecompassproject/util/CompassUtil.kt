package com.example.simplecompassproject.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

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

    private var azimuth = 0f

    override fun startListeningSensors() {
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

    /**
     * Unregister from all the sensor listeners.
     * can be called to save the battery, when compass is not in foreground.
     */
    override fun stopListeningSensors() {
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        synchronized(this) {
            //TODO test it
            when (event.sensor.type) {
                Sensor.TYPE_MAGNETIC_FIELD -> calculateMagneticField(event.values)
                Sensor.TYPE_ACCELEROMETER -> calculateAcceleration(event.values)
                else -> return
            }
            val isRotationMatrixReady = SensorManager.getRotationMatrix(
                mRotationMatrix,
                mInclinationMatrix,
                mGravityVector,
                mGeomagneticVector)
            if (isRotationMatrixReady) {
                SensorManager.getOrientation(mRotationMatrix, mOrientationsResultMatrix)
                azimuth = Math.toDegrees(mOrientationsResultMatrix[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360
                listener?.newAzimuthResponse(azimuth)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit

    /**
     * Measures the ambient magnetic field in the X, Y and Z axis.
     */
    private fun calculateMagneticField(magneticFields: FloatArray) {
        val xAxisMagneticFiled = magneticFields[0] //TODO test for performance, creating val every time sensors triggers listeners
        val yAxisMagneticFiled = magneticFields[1]
        val zAxisMagneticFiled = magneticFields[2]

        mGravityVector[0] = ALPHA * mGravityVector[0] + (1 - ALPHA) * xAxisMagneticFiled
        mGravityVector[1] = ALPHA * mGravityVector[1] + (1 - ALPHA) * yAxisMagneticFiled
        mGravityVector[2] = ALPHA * mGravityVector[2] + (1 - ALPHA) * zAxisMagneticFiled
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

    interface CompassListener {
        fun newAzimuthResponse(azimuth: Float)
    }
}