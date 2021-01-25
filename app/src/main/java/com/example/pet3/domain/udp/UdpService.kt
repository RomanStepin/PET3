package com.example.pet3.domain.udp

import com.example.pet3.repository.models.PresetModel
import fito.Fito
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface UdpService
{
    fun startUdpReceiver()
    fun stopUdpReceiver()

    fun parsePacket(packet: Fito.MessageUnion)

    fun downloadPreset(loadingPresetNumber: Int, targetId: Int)
    fun uploadPreset(presetModel: PresetModel, targetId: Int)
    fun setTime(time: Int, targetId: Int)
    fun getTime(targetId: Int)
    fun setWifiAuth()

    fun downloadPresetPublishSubject(): Observable<Pair<PresetModel, Int>>
    fun uploadPresetPublishSubject(): Observable<Pair<PresetModel, Int>>

}