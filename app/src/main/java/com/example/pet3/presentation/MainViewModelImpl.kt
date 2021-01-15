package com.example.pet3.presentation

import android.icu.util.TimeUnit
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TimeUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pet3.App
import com.example.pet3.States
import com.example.pet3.domain.udp.UdpService
import com.example.pet3.repository.Repository
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel
import io.reactivex.schedulers.Schedulers

class MainViewModelImpl(app: App) : MainViewModel(app) {

    private var udpService: UdpService = App.udpServiceComponent.getUdpService()
    private var repository: Repository = App.repositoryComponent.getRepository()


    private var answerDelay: Long = 3000
    private var maxAttemptCount = 3
    private var attempt = 0

    @Volatile private var loadingPresetNumber = 0

    private var loadPresetLiveData: MutableLiveData<PresetModel> = MutableLiveData()
    private var loadProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()

    lateinit var programModel: ProgramModel
    @Volatile var presetsArray: MutableList<Boolean> = mutableListOf()

    init {
            val disposable1 = udpService.presetPublishSubject().subscribeOn(Schedulers.newThread()).subscribe {
                programModel.addPreset(it)
                presetsArray[it.number_in_program - 1] = true

                if (it.number_in_program == it.presets_count) {
                    App.STATE = States.HEARTBEAT
                    saveProgram()
                }
                else {
                    App.STATE = States.DOWNLOADING_PROGRAM
                    loadingPresetNumber = it.number_in_program + 1
                    presetsArray.add(false)

                    downloadPreset()
                }
            }
            disposables.add(disposable1)
    }

    override fun downloadPreset()
    {
        Thread{
            val expectationPresetNumber = loadingPresetNumber
                    while(true) {
                        Thread.sleep(answerDelay)
                        if (presetsArray[expectationPresetNumber - 1] == false) {
                            Log.d("LOGGG", "пресет с номером $expectationPresetNumber не пришел")
                            udpService.downloadPreset(loadingPresetNumber)
                        } else break
                    }
        }.start()
        udpService.downloadPreset(loadingPresetNumber)
    }

    override fun downloadProgram() {
        loadingPresetNumber = 1
        programModel = ProgramModel()
        App.STATE = States.DOWNLOADING_PROGRAM
        presetsArray = mutableListOf(false)
        downloadPreset()
    }

    override fun uploadProgram() {
        TODO("Not yet implemented")
    }

    override fun uploadPreset() {
        TODO("Not yet implemented")
    }

    override fun saveProgram() {
        Log.d("LOGGG", "saveProgram " + programModel.name)
        repository.saveProgram(programModel)
    }

    override fun createProgram(name: String) {
        programModel.name = name
    }



    override fun downloadPresetLiveData(): LiveData<PresetModel> {
        return loadPresetLiveData
    }

    override fun downloadProgramLiveData(): LiveData<Boolean> {
        return loadProgramLiveData
    }

    override fun uploadPresetLiveData(): LiveData<PresetModel> {
        TODO("Not yet implemented")
    }

    override fun uploadProgramLiveData(): LiveData<Boolean> {
        TODO("Not yet implemented")
    }
}