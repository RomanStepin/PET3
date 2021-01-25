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

    private var loadingPresetNumber = 0
    private var targetID = 1

    private var downloadPresetLiveData: MutableLiveData<PresetModel> = MutableLiveData()

    private var downloadProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var createdProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var uploadProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()



    @Volatile
    var programModel: ProgramModel? = null
    var presetsArray: MutableList<Boolean> = mutableListOf()

    init {
            val disposable1 = udpService.downloadPresetPublishSubject().subscribeOn(Schedulers.newThread()).subscribe {
                if (it.second == targetID) {
                    programModel!!.addPreset(it.first)
                    presetsArray[it.first.number_in_program - 1] = true

                    if (it.first.number_in_program == it.first.presets_count) {
                        App.STATE = States.HEARTBEAT
                        saveProgram()
                        downloadProgramLiveData.postValue(true)
                    } else {
                        App.STATE = States.DOWNLOADING_PROGRAM
                        loadingPresetNumber = it.first.number_in_program + 1
                        presetsArray.add(false)

                        downloadPreset(targetID)
                    }
                }
            }
            disposables.add(disposable1)


        val disposable2 = udpService.uploadPresetPublishSubject().subscribeOn(Schedulers.newThread()).subscribe {
            if (it.second == targetID) {
                presetsArray[it.first.number_in_program - 1] = true

                if (it.first.number_in_program == it.first.presets_count) {
                    App.STATE = States.HEARTBEAT
                    Log.d("LOGGG", "все загрузили")
                    uploadProgramLiveData.postValue(true)
                } else {
                    App.STATE = States.UPLOADING_PROGRAM
                    loadingPresetNumber = it.first.number_in_program + 1
                    presetsArray.add(false)

                    uploadPreset(targetID)
                }
            }
        }
        disposables.add(disposable2)
    }



    override fun downloadProgram(targetID: Int) {
        loadingPresetNumber = 1
        App.STATE = States.DOWNLOADING_PROGRAM
        this.targetID = targetID
        presetsArray = mutableListOf(false)
        downloadPreset(targetID)
    }

    override fun uploadProgram(targetID: Int, programName: String) {
        Thread {
            programModel = repository.getProgramByName(programName)
            if (programModel == null) {
                uploadProgramLiveData.postValue(false)
            } else {
                loadingPresetNumber = 1
                App.STATE = States.UPLOADING_PROGRAM
                this.targetID = targetID
                presetsArray = mutableListOf(false)
                uploadPreset(targetID)
            }
        }.start()
    }

    override fun uploadPreset(targetID: Int) {
        Thread{
            val expectationPresetNumber = loadingPresetNumber
            while(App.STATE == States.UPLOADING_PROGRAM) {
                Thread.sleep(answerDelay)
                if (presetsArray[expectationPresetNumber - 1] == false) {
                    Log.d("LOGGG", "нет ответа, что пресет с номером $expectationPresetNumber дошел")
                    udpService.uploadPreset(programModel!!.presets[loadingPresetNumber-1], targetID)
                } else break
            }
        }.start()
        udpService.uploadPreset(programModel!!.presets[loadingPresetNumber-1], targetID)
    }

    override fun downloadPreset(targetID: Int)
    {
        Thread{
            val expectationPresetNumber = loadingPresetNumber
            while(App.STATE == States.DOWNLOADING_PROGRAM) {
                Thread.sleep(answerDelay)
                if (presetsArray[expectationPresetNumber - 1] == false) {
                    Log.d("LOGGG", "пресет с номером $expectationPresetNumber не пришел")
                    udpService.downloadPreset(loadingPresetNumber, targetID)
                } else break
            }
        }.start()
        udpService.downloadPreset(loadingPresetNumber, targetID)
    }

    @Synchronized
    override fun saveProgram() {
        Log.d("LOGGG", "saveProgram " + programModel!!.name)
        repository.updateProgram(programModel!!)
    }

    override fun createProgram(name: String) {
        Thread{
            if (repository.getProgramByName(name) == null){
                createdProgramLiveData.postValue(true)
                programModel = ProgramModel().also { it.name = name }
            } else {
                createdProgramLiveData.postValue(false)
                programModel = repository.getProgramByName(name)
                programModel!!.presets = arrayOf()
            }

        }.start()
    }

    override fun createdProgramLiveData(): LiveData<Boolean> {
        return createdProgramLiveData
    }


    override fun downloadPresetLiveData(): LiveData<PresetModel> {
        return downloadPresetLiveData
    }

    override fun downloadProgramLiveData(): LiveData<Boolean> {
        return downloadProgramLiveData
    }

    override fun uploadPresetLiveData(): LiveData<PresetModel> {
        TODO("Not yet implemented")
    }

    override fun uploadProgramLiveData(): LiveData<Boolean> {
        TODO("Not yet implemented")
    }
}