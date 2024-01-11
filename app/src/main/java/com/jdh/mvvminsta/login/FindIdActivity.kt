package com.jdh.mvvminsta.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.jdh.mvvminsta.R
import com.jdh.mvvminsta.databinding.ActivityFindIdBinding

class FindIdActivity : AppCompatActivity() {
    lateinit var binding : ActivityFindIdBinding
    val findIdViewModel: FindIdViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_id)
        binding.activity2 = this
        binding.viewModel2 = findIdViewModel
        binding.lifecycleOwner = this

        setObserve()
    }

    fun setObserve() {
        findIdViewModel.toastMessage.observe(this) {
            if(it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}