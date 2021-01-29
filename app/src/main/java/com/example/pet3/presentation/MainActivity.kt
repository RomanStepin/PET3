package com.example.pet3.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.pet3.App
import com.example.pet3.R
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var viewModel: MainViewModel = MainViewModelImpl(App.instance)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.downloadPresetLiveData.observe(this, {
            Log.d("LOGGG", "приняли пакет")
        })

        viewModel.downloadProgramLiveData.observe(this, {
            Log.d("LOGGG", "приняли всю прогу")
        })

        viewModel.downloadTimeLiveData.observe(this, {
            Log.d("LOGGG", "приняли время")
        })

        viewModel.createdProgramLiveData.observe(this, {
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

        button2.setOnClickListener {
            viewModel.uploadProgram(editTextTextLampId.text.toString().toInt(), editTextTextProgramName.text.toString())
        }

        buttonDownloadTime.setOnClickListener {
            viewModel.downloadTime(editTextTextLampId.text.toString().toInt())
        }

        buttonUploadTime.setOnClickListener{
            val dateFormat = SimpleDateFormat("dd:MM:yyyy:HH:mm:ss", Locale("en"))
            val date: Date = dateFormat.parse(time_param_text.text.toString())!!
            val longTime: Long = date.time
            val intTime: Int = (longTime / 1000).toInt()

            viewModel.uploadTime(intTime, editTextTextLampId.text.toString().toInt())
        }

        buttonDownloadWifiAuth.setOnClickListener {
            viewModel.downloadWifiAuth(editTextTextLampId.text.toString().toInt())
        }

        buttonUploadWifiAuth.setOnClickListener {
            viewModel.uploadWifiAuth(editTextWifiAuth.text.toString(), "pass", editTextTextLampId.text.toString().toInt())
        }

        buttonDownloadLanSetting.setOnClickListener {
            viewModel.downloadLanSetting(editTextTextLampId.text.toString().toInt())
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOGGG", "MainActivity    onDestroy")
    }




}
