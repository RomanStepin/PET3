package com.example.pet3.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.pet3.App
import com.example.pet3.repository.models.PresetModel
import io.reactivex.disposables.CompositeDisposable

abstract class MainViewModel(app: App) : AndroidViewModel(app) {
    val disposables = CompositeDisposable()

    abstract fun loadProgram()
    abstract fun saveProgram(program_name: String)

    abstract fun loadPresetLiveData(): LiveData<PresetModel>
    abstract fun loadProgramLiveData(): LiveData<Boolean>
}