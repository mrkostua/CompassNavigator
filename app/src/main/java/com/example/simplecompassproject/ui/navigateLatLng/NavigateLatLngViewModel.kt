package com.example.simplecompassproject.ui.navigateLatLng

import android.location.Location
import android.location.LocationManager
import com.example.simplecompassproject.util.ui.BaseViewModel
import com.example.simplecompassproject.util.ui.location.CoordinatesValidator
import timber.log.Timber

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
class NavigateLatLngViewModel(private val validator: CoordinatesValidator) : BaseViewModel<NavigateLatLngNavigator>() {

    fun acceptAndNavigate() {
        Timber.i("acceptAndNavigate")
        val latitude = navigator.getLatitudeInputText()
        val longitude = navigator.getLongitudeInputText()
        if (validateLatitudeInput(latitude) && validateLongitudeInput(longitude)) {
            try {
                val destinationLocation = Location(LocationManager.GPS_PROVIDER).apply {
                    this.latitude = latitude.toDouble()
                    this.longitude = longitude.toDouble()
                }
                navigator.finishFillingInputs()
                navigator.setCompassModeCoordinates(destinationLocation)
            } catch (e: NumberFormatException) {
                Timber.e(e, "Error during parsing lat lng String to Double")
                navigator.showErrorParsingLatLng()
            }
        }
    }

    fun navigateToNorth() {
        navigator.finishFillingInputs()
        navigator.setCompassModeNorth()
    }

    fun validateLatitudeInput(latitude: String): Boolean {
        return when {
            validator.isEmpty(latitude) -> {
                navigator.showErrorLatitudeEmpty()
                false
            }
            validator.isLatitudeValid(latitude).not() -> {
                navigator.showErrorLatitudeWrongInput()
                false
            }
            else -> {
                navigator.hideLatitudeErrorText()
                true
            }
        }
    }

    fun validateLongitudeInput(longitude: String): Boolean {
        return when {
            validator.isEmpty(longitude) -> {
                navigator.showErrorLongitudeEmpty()
                false
            }
            validator.isLongitudeValid(longitude).not() -> {
                navigator.showErrorLongitudeWrongInput()
                false
            }
            else -> {
                navigator.hideLongitudeErrorText()
                true
            }
        }
    }

    fun closeDialog() {
        navigator.back()
    }
}