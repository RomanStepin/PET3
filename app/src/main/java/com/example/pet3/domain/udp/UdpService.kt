package com.example.pet3.domain.udp

import com.example.pet3.repository.models.PresetModel
import fito.Fito
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface UdpService
{
    fun sendUdpMessage(byteArray: ByteArray)

    fun startUdpReceiver()

    fun stopUdpReceiver()

    fun getWifiState()

    fun parsePacket(packet: Fito.MessageUnion)

    fun downloadPresetPublishSubject(): Observable<PresetModel>
    fun uploadPresetPublishSubject(): Observable<PresetModel>


    fun downloadPreset(loading_preset_number: Int)
    fun uploadPreset(presetModel: PresetModel, targetId: Int)

}