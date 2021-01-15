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

    init {
        startUdpReceiver()
        isUsing = true
    }

    override fun startUdpReceiver() {
        Log.d("LOGGG", "|||||")
        Log.d("LOGGG", "startUdpReceiver()")
       Observable.fromCallable {
           val buf = ByteArray(4096)
           val packet = DatagramPacket(buf, 4096)
           var protoPacket: Fito.MessageUnion = Fito.MessageUnion.getDefaultInstance()
           val mainHandler = Handler(Looper.getMainLooper())

           while (isUsing) {
               socket.receive(packet)
               Log.d("LOGGG", "|||||")
               Log.d("LOGGG", "socket.receive(packet)")
               protoPacket = Fito.MessageUnion.parseFrom(packet.data.copyOf(packet.length))
               mainHandler.post {
                   Toast.makeText(app, protoPacket.toString(), Toast.LENGTH_SHORT).show()
               }
             //  receivePacket.onNext(fito)
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
            presetPublishSubject.onNext(PresetModel(packet.param.preset))
        }
    }

    override fun presetPublishSubject(): Observable<PresetModel> {
        return presetPublishSubject
    }

    override fun downloadPreset(loading_preset_number: Int) {
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
            Log.d("LOGGG", "|||||||||")
            Log.d("LOGGG", "clientSocket.send(sendPacketU1)")
            clientSocket.send(sendPacketU1)
        }.start()

    }

    override fun uploadPreset(loading_preset_number: Int) {
        TODO("Not yet implemented")
    }
}