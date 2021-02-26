package com.example.pet3.presentation

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pet3.App
import com.example.pet3.StatesObservable
import com.example.pet3.repository.models.LampModel
import com.example.pet3.repository.models.LanSettingModel
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel
import fito.Fito
import io.reactivex.disposables.CompositeDisposable

abstract class MainViewModel(app: App) : AndroidViewModel(app) {
    val disposables = CompositeDisposable()

    abstract fun downloadProgram(targetID: Int)
    abstract fun downloadPreset(targetID: Int)
    abstract fun downloadTime(targetID: Int)
    abstract fun downloadWifiAuth(targetID: Int)
    abstract fun downloadLanSetting(targetID: Int)
    abstract fun downloadMQTTAuth(targetID: Int)

    abstract fun uploadPreset(targetID: Int)
    abstract fun uploadTime(time: Int, targetID: Int)
    abstract fun uploadWifiAuth(ssid: String, password: String, targetID: Int)
    abstract fun uploadLanSetting(lanSettingModel: LanSettingModel, targetID: Int)
    abstract fun uploadMQTTAuth(mqttLogin: String, mqttPassword: String, targetID: Int)
    abstract fun uploadProgramIntoGardenLamps(programName: String, gardenNumber: Long)


    abstract fun createProgram(name: String)
    abstract fun saveProgram()
    abstract fun getLampsByGardenNumber(gardenNumber: Long)

    var createdProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()

    var getLampsByGardenNumberLiveData: MutableLiveData<Pair<List<LampModel>?, StatesObservable>> = MutableLiveData()

    var downloadPresetLiveData: MutableLiveData<PresetModel> = MutableLiveData()
    var downloadProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var downloadTimeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var downloadWifiAuthLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var downloadLanSettingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var downloadMQTTAuthLiveData: MutableLiveData<Boolean> = MutableLiveData()

    var uploadProgramLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var uploadPresetLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var uploadTimeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var uploadWifiAuthLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var uploadLanSettingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var uploadMQTTAuthLiveData: MutableLiveData<Boolean> = MutableLiveData()

    var toastLiveData: MutableLiveData<String> = MutableLiveData()

    var uploadProgramIntoGardenLampsLiveData: MutableLiveData<StatesObservable> = MutableLiveData()
}