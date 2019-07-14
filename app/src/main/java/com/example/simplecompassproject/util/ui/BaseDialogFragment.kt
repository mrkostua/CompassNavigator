package com.example.simplecompassproject.util.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */
abstract class BaseDialogFragment : DialogFragment() {
    abstract var items: Array<TextInputEditText>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackClick()
        initializeEditTextViews()
    }

    fun onBack() {
        dismiss()
    }

    fun hideKeyBoardAndCursors() {
        items.forEach {
            it.isFocusable = false
        }
        items.firstOrNull()?.let { view ->
            context?.hideKeyboardFrom(view)
        }
    }

    private fun initializeEditTextViews() {
        items.forEach {
            it.setOnTouchListener { v, event ->
                it.isFocusableInTouchMode = true
                return@setOnTouchListener false
            }
        }
    }

    private fun handleBackClick() {
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                onBack()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }
}