package com.example.pet3.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pet3.App
import com.example.pet3.States
import com.example.pet3.domain.udp.UdpService
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel
import io.reactivex.schedulers.Schedulers

class MainViewModelImpl(app: App) : MainViewModel(app) {

    private var udpService: UdpService = App.udpServiceComponent.getUdpService()

    private var loadingProgram: ProgramModel = ProgramModel()
    private var loading_preset_number = 0

    private var loadPresetLiveData: MutableLiveData<PresetModel> = MutableLiveData()
    private var loadProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var state: States = States.HEARTBEAT

    init {
            val disposable1 = udpService.presetPublishSubject().subscribeOn(Schedulers.io()).subscribe {
                if (state == States.LOADING_PROGRAM && it.number_in_program == loading_preset_number) {
                    loadingProgram.presets.plus(it)
                    if (it.number_in_program != it.presets_count) {
                        loadPresetLiveData.postValue(it)
                        loading_preset_number++
                        udpService.loadPreset(loading_preset_number)
                    } else {
                        loadProgramLiveData.postValue(true)
                        state = States.HEARTBEAT
                        loading_preset_number = 0
                    }
                }
            }
            disposables.add(disposable1)
    }


    override fun loadProgram() {
        state = States.LOADING_PROGRAM
        loading_preset_number = 1
        loadingProgram = ProgramModel()
        udpService.loadPreset(loading_preset_number)
    }

    override fun saveProgram(program_name: String) {
        TODO("saving program")
    }

    override fun loadPresetLiveData(): LiveData<PresetModel> {
        return loadPresetLiveData
    }

    override fun loadProgramLiveData(): LiveData<Boolean> {
        return loadProgramLiveData
    }
}