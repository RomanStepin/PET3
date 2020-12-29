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
    private var loadPresetLiveData: MutableLiveData<PresetModel> = MutableLiveData()
    private var loadProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var state: States = States.HEARTBEAT

    init {
            val disposable1 = udpService.presetPublishSubject().subscribeOn(Schedulers.io()).subscribe {
                if (state == States.LOAD_PROGRAM) {
                    loadingProgram.presets.plus(it)
                    if (it.number_in_program == it.presets_count) {
                        loadPresetLiveData.postValue(it)
                    } else {
                        loadProgramLiveData.postValue(true)
                    }
                }
            }
            disposables.add(disposable1)
    }


    override fun loadProgram() {
        state = States.LOAD_PROGRAM
        loadingProgram = ProgramModel()
        udpService.loadProgram()
    }

    override fun saveProgram() {
        TODO("saving program")
        state = States.HEARTBEAT
    }
}