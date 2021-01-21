package com.example.pet3.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pet3.App
import com.example.pet3.R
import kotlinx.android.synthetic.main.activity_main.*


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
            //viewModel.downloadProgram(5)

        })

        viewModel.createdProgramLiveData().observe(this, {
            if (it) {
                Log.d("LOGGG", "создали программу")
            } else {
                Log.d("LOGGG", "используем имеющуюся программу")
            }
            viewModel.downloadProgram( editTextTextLampId.text.toString().toInt())
        })

        button.setOnClickListener {
            Log.d("LOGGG", "|||||||")
            Log.d("LOGGG", "viewModel.loadProgram()")
            viewModel.createProgram(editTextTextProgramName.text.toString())

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOGGG", "MainActivity    onDestroy")
    }




}
