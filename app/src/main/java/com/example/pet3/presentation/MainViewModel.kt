package com.example.pet3.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.pet3.App
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel
import fito.Fito
import io.reactivex.disposables.CompositeDisposable

abstract class MainViewModel(app: App) : AndroidViewModel(app) {
    val disposables = CompositeDisposable()

    abstract fun downloadProgram(targetID: Int)
    abstract fun downloadPreset(targetID: Int)

    abstract fun uploadProgram(targetID: Int, programName: String)
    abstract fun uploadPreset(targetID: Int)

    abstract fun saveProgram()
    abstract fun createProgram(name: String)

    abstract fun createdProgramLiveData(): LiveData<Boolean>

    abstract fun downloadPresetLiveData(): LiveData<PresetModel>
    abstract fun downloadProgramLiveData(): LiveData<Boolean>

    abstract fun uploadPresetLiveData(): LiveData<PresetModel>
    abstract fun uploadProgramLiveData(): LiveData<Boolean>
}