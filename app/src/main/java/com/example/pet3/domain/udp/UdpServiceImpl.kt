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
import com.example.pet3.repository.models.*
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
    lateinit var messageUdp: Fito.MessageUnion
    private var isUsing = true
    var clientSocket = DatagramSocket().also { it.broadcast = true }

    override var downloadPresetPublishSubject: PublishSubject<Pair<PresetModel, Int>> = PublishSubject.create()
    override var downloadTimePublishSubject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
    override var downloadWifiAuthPublishSubject: PublishSubject<Pair<WifiAuthModel, Int>> = PublishSubject.create()
    override var downloadLanSettingPublishSubject: PublishSubject<Pair<LanSettingModel, Int>> = PublishSubject.create()
    override var downloadMQTTAuthPublishSubject: PublishSubject<Pair<PresetModel, Int>> = PublishSubject.create()

    override var uploadPresetPublishSubject: PublishSubject<Pair<PresetModel, Int>> = PublishSubject.create()
    override var uploadTimePublishSubject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
    override var uploadWifiAuthPublishSubject: PublishSubject<Pair<WifiAuthModel, Int>> = PublishSubject.create()
    override var uploadLanSettingPublishSubject: PublishSubject<Pair<LanSettingModel, Int>> = PublishSubject.create()
    override var uploadMQTTAuthPublishSubject: PublishSubject<Pair<PresetModel, Int>> = PublishSubject.create()
    override var ackFailedPublishSubject: PublishSubject<Int> = PublishSubject.create()

    override var toastPublishSubject: PublishSubject<String> = PublishSubject.create()

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
               toastPublishSubject.onNext(protoPacket.toString())
               parsePacket(protoPacket)
           }
            }.subscribeOn(Schedulers.io()).subscribe()

    }

    override fun stopUdpReceiver() {
        Log.d("LOGGG", "stopUdpReceiver")
        isUsing = false
    }



    override fun parsePacket(packet: Fito.MessageUnion) {

        if (packet.param.action == FitoParam.Param.Action.VALUE && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED && packet.param.preset != FitoParam.Preset.getDefaultInstance() && packet.param.preset.presetsCount != 0 && App.STATE == States.DOWNLOADING_PROGRAM) {
            downloadPresetPublishSubject.onNext(Pair(PresetModel(packet.param.preset), packet.sysId))
        }

        if (packet.param.action == FitoParam.Param.Action.ACK && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED  && packet.param.preset != FitoParam.Preset.getDefaultInstance() && packet.param.preset.presetsCount != 0 && App.STATE == States.UPLOADING_PROGRAM) {
            uploadPresetPublishSubject.onNext(Pair(PresetModel(packet.param.preset), packet.sysId))
        }

        if (packet.param.action == FitoParam.Param.Action.VALUE && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED && packet.param.time != FitoParam.Time.getDefaultInstance() && App.STATE == States.DOWNLOADING_TIME) {
            downloadTimePublishSubject.onNext(Pair(packet.param.time.time, packet.sysId))
        }

        if (packet.param.action == FitoParam.Param.Action.ACK && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED && packet.param.time != FitoParam.Time.getDefaultInstance() && App.STATE == States.UPLOADING_TIME) {
            uploadTimePublishSubject.onNext(Pair(packet.param.time.time, packet.sysId))
        }

        if (packet.param.action == FitoParam.Param.Action.VALUE && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED && packet.param.wifiAuth != FitoParam.WiFiAuth.getDefaultInstance() && App.STATE == States.DOWNLOADING_WIFI_AUTH) {
            downloadWifiAuthPublishSubject.onNext(Pair(WifiAuthModel(packet.param.wifiAuth.ssid, packet.param.wifiAuth.password), packet.sysId))
        }
        if (packet.param.action == FitoParam.Param.Action.ACK && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED && packet.param.wifiAuth != FitoParam.WiFiAuth.getDefaultInstance() && App.STATE == States.UPLOADING_WIFI_AUTH) {
            uploadWifiAuthPublishSubject.onNext(Pair(WifiAuthModel(packet.param.wifiAuth.ssid, packet.param.wifiAuth.password), packet.sysId))
        }

        if (packet.param.action == FitoParam.Param.Action.VALUE && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED && packet.param.lanSetting != FitoParam.LanSetting.getDefaultInstance() && App.STATE == States.DOWNLOADING_LAN_SETTING) {
            downloadLanSettingPublishSubject.onNext(Pair(LanSettingModel(packet.param.lanSetting.useDHCP, packet.param.lanSetting.ip, packet.param.lanSetting.mask, packet.param.lanSetting.gateware), packet.sysId))
        }

        if (packet.param.action == FitoParam.Param.Action.ACK && packet.param.ack == FitoParam.Param.Ack.ACK_ACCEPTED && packet.param.lanSetting != FitoParam.LanSetting.getDefaultInstance() && App.STATE == States.UPLOADING_LAN_SETTING) {
            uploadLanSettingPublishSubject.onNext(Pair(LanSettingModel(packet.param.lanSetting.useDHCP, packet.param.lanSetting.ip, packet.param.lanSetting.mask, packet.param.lanSetting.gateware), packet.sysId))
        }

        if (packet.param.ack == FitoParam.Param.Ack.ACK_FAILED) {
            ackFailedPublishSubject.onNext(packet.sysId)
        }
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

    override fun downloadTime(targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().apply {
            sysId = 0
            this.targetId = targetId
            param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.GET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                time = FitoParam.Time.newBuilder().apply { time = 1 }.build()
            }.build()
        }.build()

        val sendData = messageUdp.toByteArray()
        val sendPacket = sendData?.size?.let { DatagramPacket(
            sendData,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacket)
        }.start()
    }

    override fun downloadWifiAuth(targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().apply {
            sysId = 0
            this.targetId = targetId
            param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.GET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                wifiAuth = FitoParam.WiFiAuth.newBuilder().apply {
                    ssid = ""
                    password = ""
                }.build()
            }.build()
        }.build()

        val sendData = messageUdp.toByteArray()
        val sendPacket = sendData?.size?.let { DatagramPacket(
            sendData,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacket)
        }.start()
    }

    override fun downloadMQTTAuth(targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().apply {
            sysId = 0
            this.targetId = targetId
            param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.GET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                mqttAuth = FitoParam.MQTTAuth.newBuilder().apply {
                    login = ""
                    password = ""
                }.build()
            }.build()
        }.build()

        val sendData = messageUdp.toByteArray()
        val sendPacket = sendData?.size?.let { DatagramPacket(
            sendData,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacket)
        }.start()
    }

    override fun downloadLanSetting(targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().apply {
            sysId = 0
            this.targetId = targetId
            param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.GET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                lanSetting = FitoParam.LanSetting.newBuilder().apply {
                    useDHCP = true
                    ip = "1.2"
                    mask = "1.2"
                    gateware = "1.2"
                }.build()
            }.build()
        }.build()

        val sendData = messageUdp.toByteArray()
        val sendPacket = sendData?.size?.let { DatagramPacket(
            sendData,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacket)
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

    override fun uploadTime(time: Int, targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().also {
            it.sysId = 0
            it.targetId = targetId
            it.param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.SET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                this.time = FitoParam.Time.newBuilder().apply { this.time = time }.build()
            }.build()
        }.build()

        val sendDataU = messageUdp.toByteArray()
        val sendPacketU = sendDataU?.size?.let { DatagramPacket(
            sendDataU,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacketU)
        }.start()
    }

    override fun uploadWifiAuth(wifiAuthModel: WifiAuthModel, targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().also {
            it.sysId = 0
            it.targetId = targetId
            it.param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.SET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                wifiAuth = FitoParam.WiFiAuth.newBuilder().apply {
                    this.ssid = wifiAuthModel.ssid
                    this.password = wifiAuthModel.password
                }.build()
            }.build()
        }.build()

        val sendDataU = messageUdp.toByteArray()
        val sendPacketU = sendDataU?.size?.let { DatagramPacket(
            sendDataU,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacketU)
        }.start()
    }

    override fun uploadMQTTAuth(mqttAuthModel: MQTTAuthModel, targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().also {
            it.sysId = 0
            it.targetId = targetId
            it.param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.SET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                mqttAuth = FitoParam.MQTTAuth.newBuilder().apply {
                    this.login = mqttAuthModel.login
                    this.password = mqttAuthModel.password
                }.build()
            }.build()
        }.build()

        val sendDataU = messageUdp.toByteArray()
        val sendPacketU = sendDataU?.size?.let { DatagramPacket(
            sendDataU,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacketU)
        }.start()
    }

    override fun uploadLanSetting(lanSettingModel: LanSettingModel, targetId: Int) {
        messageUdp = fito.Fito.MessageUnion.newBuilder().also {
            it.sysId = 0
            it.targetId = targetId
            it.param = FitoParam.Param.newBuilder().apply {
                action = FitoParam.Param.Action.SET
                ack = FitoParam.Param.Ack.ACK_ACCEPTED
                lanSetting = FitoParam.LanSetting.newBuilder().apply {
                    this.useDHCP = lanSettingModel.useDHCP
                    this.ip = lanSettingModel.ip
                    this.mask = lanSettingModel.mask
                    this.gateware = lanSettingModel.gateware
                }.build()
            }.build()
        }.build()

        val sendDataU = messageUdp.toByteArray()
        val sendPacketU = sendDataU?.size?.let { DatagramPacket(
            sendDataU,
            it,
            broadcastAddress,
            4096
        ) }
        Thread {
            clientSocket.send(sendPacketU)
        }.start()
    }
}