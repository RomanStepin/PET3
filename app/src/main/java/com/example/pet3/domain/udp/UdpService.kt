package com.example.pet3.domain.udp

import com.example.pet3.repository.models.LanSettingModel
import com.example.pet3.repository.models.MQTTAuthModel
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.WifiAuthModel
import fito.Fito
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface UdpService
{
    var downloadPresetPublishSubject: PublishSubject<Pair<PresetModel, Int>>
    var downloadTimePublishSubject: PublishSubject<Pair<Int, Int>>
    var downloadWifiAuthPublishSubject: PublishSubject<Pair<WifiAuthModel, Int>>
    var downloadLanSettingPublishSubject: PublishSubject<Pair<LanSettingModel, Int>>
    var downloadMQTTAuthPublishSubject: PublishSubject<Pair<PresetModel, Int>>

    var uploadPresetPublishSubject: PublishSubject<Pair<PresetModel, Int>>
    var uploadTimePublishSubject: PublishSubject<Pair<Int, Int>>
    var uploadWifiAuthPublishSubject: PublishSubject<Pair<WifiAuthModel, Int>>
    var uploadLanSettingPublishSubject: PublishSubject<Pair<PresetModel, Int>>
    var uploadMQTTAuthPublishSubject: PublishSubject<Pair<PresetModel, Int>>

    fun startUdpReceiver()
    fun stopUdpReceiver()

    fun parsePacket(packet: Fito.MessageUnion)

    fun downloadPreset(loadingPresetNumber: Int, targetId: Int)
    fun downloadTime(targetId: Int)
    fun downloadWifiAuth(targetId: Int)
    fun downloadMQTTAuth(targetId: Int)
    fun downloadLanSetting(targetId: Int)

    fun uploadPreset(presetModel: PresetModel, targetId: Int)
    fun uploadTime(time: Int, targetId: Int)
    fun uploadWifiAuth(wifiAuthModel: WifiAuthModel, targetId: Int)
    fun uploadMQTTAuth(mqttAuthModel: MQTTAuthModel, targetId: Int)
    fun uploadLanSetting(lanSettingModel: LanSettingModel, targetId: Int)

}