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

    var presetPublishSubject: PublishSubject<PresetModel> = PublishSubject.create()

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

    override fun startUdpReceiver() {
       Observable.fromCallable {
           val buf = ByteArray(4096)
           val packet = DatagramPacket(buf, 4096)
           var fito: Fito.MessageUnion = Fito.MessageUnion.getDefaultInstance()
           val mainHandler = Handler(Looper.getMainLooper())

           while (true) {
               socket.receive(packet)
               fito = Fito.MessageUnion.parseFrom(packet.data.copyOf(packet.length))
               mainHandler.post {
                   Toast.makeText(app, fito.toString(), Toast.LENGTH_SHORT).show()
               }
           }
            }.subscribeOn(Schedulers.io()).subscribe()

    }



    override fun stopUdpReceiver() {
        Log.d("LOGGG", "startUdpReceiver")
    }

    override fun getWifiState() {
        Log.d("LOGGG", "getWifiState")
    }

    override fun parseProto(proto: Fito.MessageUnion) {
        if (proto.param.preset != FitoParam.Preset.getDefaultInstance()) {
            presetModel = PresetModel(proto.param.preset)
            presetPublishSubject.onNext(presetModel)
        }
    }

    override fun presetPublishSubject(): Observable<PresetModel> {
        return presetPublishSubject
    }

    override fun loadPreset(loading_preset_number: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().apply {
            sysId = 0
            targetId = 1
            param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.GET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                    preset = FitoParam.Preset.newBuilder().apply {
                        duration = 0
                        presetNumber = loading_preset_number
                        presetsCount = 0
                    }.build()
            }.build()
        }.build()

        val clientSocket = DatagramSocket()
        clientSocket.broadcast = true
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