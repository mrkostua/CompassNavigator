package com.example.simplecompassproject.ui.navigateLatLng

import com.example.simplecompassproject.data.LatLng
import com.example.simplecompassproject.util.ui.BaseViewModel
import com.example.simplecompassproject.util.ui.location.CoordinatesValidator
import timber.log.Timber

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
class NavigateLatLngViewModel(private val validator: CoordinatesValidator) : BaseViewModel<NavigateLatLngNavigator>() {

    fun closeDialog() {
        navigator.back()
    }

    fun acceptAndNavigate() {
        val latitude = navigator.getLatitudeInputText()
        val longitude = navigator.getLongitudeInputText()
        if (validateLatitudeInput(latitude) && validateLongitudeInput(longitude)) {
            try {
                navigator.finishFillingInputs()
                navigator.setCompassModeToCoordinates(LatLng(latitude.toDouble(), longitude.toDouble()))
            } catch (e: NumberFormatException) {
                Timber.e(e, "Error during parsing lat lng String to Double")
                //TODO show some error message
            }
        }
    }

    fun navigateToNorth() {
        navigator.finishFillingInputs()
        navigator.setCompassModeToNorth()
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
}