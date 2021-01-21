package com.example.pet3.domain.udp

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.pet3.App
import com.example.pet3.States
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel
import fito.Fito
import fito.FitoParam
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

public class UdpServiceImpl (val app: App, val socket: DatagramSocket): UdpService
{
    lateinit var programModel: ProgramModel
    lateinit var presetModel: PresetModel
    lateinit var messageUdp: Fito.MessageUnion
    private var isUsing = true
    var clientSocket = DatagramSocket().also { it.broadcast = true }

    var downloadPresetPublishSubject: PublishSubject<Pair<PresetModel, Int>> = PublishSubject.create()
    var uploadPresetPublishSubject: PublishSubject<Pair<PresetModel, Int>> = PublishSubject.create()

    var broadcastAddress: InetAddress
        get() {
            val wifi = App.instance.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val dhcp = wifi.dhcpInfo?: return InetAddress.getByName("255.255.255.255")
            val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
            val quads = ByteArray(4)
            for (k in 0..3) quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
            val s1: InetAddress = InetAddress.getByAddress(quads)
            return s1
        }
        set(value) {}

    override fun sendUdpMessage(byteArray: ByteArray) {
        Log.d("LOGGG", "sendUdpMessage")
    }

    init {
        startUdpReceiver()
        isUsing = true
    }

    override fun startUdpReceiver() {
       Observable.fromCallable {
           val buf = ByteArray(4096)
           val packet = DatagramPacket(buf, 4096)
           var protoPacket: Fito.MessageUnion = Fito.MessageUnion.getDefaultInstance()
           val mainHandler = Handler(Looper.getMainLooper())

           while (isUsing) {
               socket.receive(packet)
               protoPacket = Fito.MessageUnion.parseFrom(packet.data.copyOf(packet.length))
               mainHandler.post {
                   Toast.makeText(app, protoPacket.toString(), Toast.LENGTH_SHORT).show()
               }
               parsePacket(protoPacket)
           }
            }.subscribeOn(Schedulers.io()).subscribe()

    }



    override fun stopUdpReceiver() {
        Log.d("LOGGG", "stopUdpReceiver")
        isUsing = false
    }

    override fun getWifiState() {
        Log.d("LOGGG", "getWifiState")
    }

    override fun parsePacket(packet: Fito.MessageUnion) {
        if (packet.param.action == FitoParam.Param.Action.GET && packet.param.preset != FitoParam.Preset.getDefaultInstance() && packet.param.preset.presetsCount != 0 && App.STATE == States.DOWNLOADING_PROGRAM) {
            downloadPresetPublishSubject.onNext(Pair(PresetModel(packet.param.preset), packet.sysId))
        }
        if (packet.param.action == FitoParam.Param.Action.ACK && packet.param.preset != FitoParam.Preset.getDefaultInstance() && packet.param.preset.presetsCount != 0 && App.STATE == States.UPLOADING_PROGRAM)
        {

        }
    }

    override fun downloadPresetPublishSubject(): Observable<Pair<PresetModel, Int>> {
        return downloadPresetPublishSubject
    }

    override fun uploadPresetPublishSubject(): Observable<Pair<PresetModel, Int>> {
        return uploadPresetPublishSubject
    }

    override fun downloadPreset(loadingPresetNumber: Int, targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().apply {
            sysId = 0
            this.targetId = targetId
            param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.GET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                    preset = FitoParam.Preset.newBuilder().apply {
                        duration = 0
                        presetNumber = loadingPresetNumber
                        presetsCount = 0
                    }.build()
            }.build()
        }.build()

        val sendDataU1 = messageUdp.toByteArray()
        val sendPacketU1 = sendDataU1?.size?.let { DatagramPacket(
            sendDataU1,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacketU1)
        }.start()

    }

    override fun uploadPreset(presetModel: PresetModel, targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().also {
            it.sysId = 0
            it.targetId = targetId
            it.param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.SET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                preset = presetModel.toProtoClass()
            }.build()
        }.build()

        val sendDataU1 = messageUdp.toByteArray()
        val sendPacketU1 = sendDataU1?.size?.let { DatagramPacket(
            sendDataU1,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacketU1)
        }.start()
    }
}