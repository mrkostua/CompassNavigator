package com.example.simplecompassproject.ui.compass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.simplecompassproject.R
import com.example.simplecompassproject.databinding.ActivityCompassBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class CompassActivity : AppCompatActivity(), CompassNavigator {
    private lateinit var binding: ActivityCompassBinding
    private lateinit var viewModel: CompassViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compass)
        viewModel = getViewModel<CompassViewModel>().apply {
            binding.viewModel = this
            navigator = this@CompassActivity
        }
        binding.lifecycleOwner = this

        binding.executePendingBindings()
    }

    override fun back() {
        onBackPressed()
    }
}
