package com.example.pet3.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pet3.App
import com.example.pet3.R
import com.example.pet3.domain.udp.UdpService
import com.example.pet3.domain.udp.UdpServiceImpl
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    var viewModel: MainViewModel = MainViewModelImpl(App.instance)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.downloadPresetLiveData().observe(this, {
            Log.d("LOGGG", "приняли пакет")
        })

        viewModel.downloadProgramLiveData().observe(this, {
            Log.d("LOGGG", "приняли всю прогу")
            viewModel.createProgram("какое-то имя")

        })

        button.setOnClickListener {
            Log.d("LOGGG", "|||||||")
            Log.d("LOGGG", "viewModel.loadProgram()")
            viewModel.downloadProgram()

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOGGG", "MainActivity    onDestroy")
    }




}
