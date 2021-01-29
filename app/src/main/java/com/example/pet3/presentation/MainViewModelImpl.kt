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
import com.example.pet3.StatesObservable
import com.example.pet3.domain.udp.UdpService
import com.example.pet3.repository.Repository
import com.example.pet3.repository.models.*
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class MainViewModelImpl(app: App) : MainViewModel(app) {

    private var udpService: UdpService = App.udpServiceComponent.getUdpService()
    private var repository: Repository = App.repositoryComponent.getRepository()


    private var answerDelay: Long = 3000

    private var loadingPresetNumber = 0
    private var targetID = 1

    @Volatile
    var programModel: ProgramModel? = null
    var presetsArray: MutableList<Boolean> = mutableListOf()

    init {
            val disposable1 = udpService.downloadPresetPublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
                if (it.second == targetID) {
                    programModel!!.addPreset(it.first)
                    presetsArray[it.first.number_in_program - 1] = true

                    if (it.first.number_in_program == it.first.presets_count) {
                        App.STATE = States.HEARTBEAT
                        Log.d("LOGGG", "все скачали")
                        saveProgram()
                        downloadProgramLiveData.postValue(true)
                    } else {
                        App.STATE = States.DOWNLOADING_PROGRAM
                        loadingPresetNumber = it.first.number_in_program + 1
                        presetsArray.add(false)
                        Log.d("LOGGG", "пресет скачали")
                        downloadPreset(targetID)
                    }
                }
            }
            disposables.add(disposable1)

        val disposable2 = udpService.uploadPresetPublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
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
                    Log.d("LOGGG", "пресет загрузили")
                    uploadPreset(targetID)
                }
            }
        }
        disposables.add(disposable2)

        val disposable3 = udpService.downloadTimePublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
            if (it.second == targetID) {
                App.STATE = States.HEARTBEAT
                val format = SimpleDateFormat("dd:MM:yyyy:HH:mm:ss", Locale("en"))
                val ll: Long = it.first.toLong() * 1000
                val date = Date(ll)
                var s =  format.format(date)
                Log.d("LOGGG", "время у нас $s    а в цифрах ${it.first}" )
            }
        }
        disposables.add(disposable3)

        val disposable4 = udpService.uploadTimePublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
            if (it.second == targetID) {
                App.STATE = States.HEARTBEAT
                val format = SimpleDateFormat("dd:MM:yyyy:HH:mm:ss", Locale("en"))
                val ll: Long = it.first.toLong() * 1000
                val date = Date(ll)
                var s =  format.format(date)
                Log.d("LOGGG", "загрузили время $s   а в цифрах ${it.first}")
            }
        }
        disposables.add(disposable4)

        val disposable5 = udpService.downloadWifiAuthPublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
            if (it.second == targetID) {
                App.STATE = States.HEARTBEAT
                Log.d("LOGGG", "скачали данные WI-FI ${it.first.ssid}   ${it.first.password}" )
            }
        }
        disposables.add(disposable5)

        val disposable6 = udpService.uploadWifiAuthPublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
            if (it.second == targetID) {
                App.STATE = States.HEARTBEAT
                Log.d("LOGGG", "загрузили данные WI-FI ${it.first.ssid}   ${it.first.password}" )
            }
        }
        disposables.add(disposable6)

        val disposable7 = udpService.downloadLanSettingPublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
            if (it.second == targetID) {
                App.STATE = States.HEARTBEAT
                Log.d("LOGGG", "скачали данные LANSETTING ${it.first.useDHCP}   ${it.first.ip}  ${it.first.mask}  ${it.first.gateware}")
            }
        }
        disposables.add(disposable7)

        val disposable8 = udpService.uploadLanSettingPublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
            if (it.second == targetID) {
                App.STATE = States.HEARTBEAT
                Log.d("LOGGG", "загрузили данные LANSETTING ${it.first.useDHCP}   ${it.first.ip}  ${it.first.mask}  ${it.first.gateware}" )
            }
        }
        disposables.add(disposable8)

        val disposable9 = udpService.ackFailedPublishSubject.subscribeOn(Schedulers.newThread()).subscribe {
            if (it == true) {
                App.STATE = States.HEARTBEAT
                Log.d("LOGGG", "чета ошибочка кек мня мня" )
            }
        }
        disposables.add(disposable9)
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

    override fun downloadTime(targetID: Int) {
        App.STATE = States.DOWNLOADING_TIME
        this.targetID = targetID
        Thread{
            while (true)
            {
                Thread.sleep(1000)
                if (App.STATE == States.DOWNLOADING_TIME) {
                    Log.d("LOGGG", "время с лампы $targetID не пришло")
                    udpService.downloadTime(targetID)
                } else break
            }
        }.start()
        udpService.downloadTime(targetID)
    }

    override fun downloadWifiAuth(targetID: Int) {
        App.STATE = States.DOWNLOADING_WIFI_AUTH
        this.targetID = targetID
        Thread{
            while (true)
            {
                Thread.sleep(1000)
                if (App.STATE == States.DOWNLOADING_WIFI_AUTH) {
                    Log.d("LOGGG", "данные WI-FI с лампы $targetID не пришли")
                    udpService.downloadWifiAuth(targetID)
                } else break
            }
        }.start()
        udpService.downloadWifiAuth(targetID)
    }

    override fun downloadLanSetting(targetID: Int) {
        App.STATE = States.DOWNLOADING_LAN_SETTING
        this.targetID = targetID
        Thread{
            while (true)
            {
                Thread.sleep(1000)
                if (App.STATE == States.DOWNLOADING_LAN_SETTING) {
                    Log.d("LOGGG", "данные lanSetting с лампы $targetID не пришли")
                    udpService.downloadLanSetting(targetID)
                } else break
            }
        }.start()
        udpService.downloadLanSetting(targetID)
    }

    override fun downloadMQTTAuth(targetID: Int) {
        TODO("Not yet implemented")
    }

    override fun uploadTime(time: Int, targetID: Int) {
        App.STATE = States.UPLOADING_TIME
        this.targetID = targetID
        Thread{
            while (true)
            {
                Thread.sleep(1000)
                if (App.STATE == States.UPLOADING_TIME) {
                    Log.d("LOGGG", "время на лампу $targetID не пришло")
                    udpService.uploadTime(time, targetID)
                } else break
            }
        }.start()
        udpService.uploadTime(time, targetID)
    }

    override fun uploadWifiAuth(ssid: String, password: String, targetID: Int) {
        App.STATE = States.UPLOADING_WIFI_AUTH
        this.targetID = targetID
        Thread{
            while (true)
            {
                Thread.sleep(1000)
                if (App.STATE == States.UPLOADING_WIFI_AUTH) {
                    Log.d("LOGGG", "wi-fi настройки на лампу $targetID не пришли")
                    udpService.uploadWifiAuth(WifiAuthModel(ssid, password), targetID)
                } else break
            }
        }.start()
        udpService.uploadWifiAuth(WifiAuthModel(ssid, password), targetID)
    }

    override fun uploadLanSetting(lanSettingModel: LanSettingModel, targetID: Int) {
        App.STATE = States.UPLOADING_LAN_SETTING
        this.targetID = targetID
        Thread{
            while (true)
            {
                Thread.sleep(1000)
                if (App.STATE == States.UPLOADING_LAN_SETTING) {
                    Log.d("LOGGG", "LAN_SETTING настройки на лампу $targetID не пришли")
                    udpService.uploadLanSetting(lanSettingModel, targetID)
                } else break
            }
        }.start()
        udpService.uploadLanSetting(lanSettingModel, targetID)
    }

    override fun uploadMQTTAuth(mqttLogin: String, mqttPassword: String, targetID: Int) {
        TODO("Not yet implemented")
    }

    override fun uploadProgramIntoGardenLamps(targetID: Int, programNumber: Long, gardenNumber: Long) {
        Thread{
            val lamps: List<LampModel>? = repository.getLampsByGardenNumber(gardenNumber)
            if (lamps == null) {
                uploadProgramIntoGardenLampsLiveData.postValue(StatesObservable.REPOSITORY_FAILED)
            }

        }.start()

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

    override fun getLampsByGardenNumber(gardenNumber: Long) {
        Thread{
            val list = repository.getLampsByGardenNumber(gardenNumber)
            if (list == null) getLampsByGardenNumberLiveData.postValue(Pair(list, StatesObservable.REPOSITORY_FAILED))
        }.start()

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
}