package com.example.simplecompassproject.ui.navigateLatLng

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.simplecompassproject.R
import com.example.simplecompassproject.databinding.DialogNavigationLatLngBinding
import com.example.simplecompassproject.util.ui.BaseDialogFragment
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Created by Kostiantyn Prysiazhnyi on 7/14/2019.
 */

class NavigateLatLngDialog : BaseDialogFragment() {
    override var items: Array<TextInputEditText> = emptyArray()
    private lateinit var binding: DialogNavigationLatLngBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_navigation_lat_lng, container, false)
        binding.viewModel = getViewModel()
        binding.lifecycleOwner = this

        items = arrayOf()
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}