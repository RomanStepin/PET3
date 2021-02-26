package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class LampModel {
    @PrimaryKey(autoGenerate = true)
    var number: Long = 0
    var id: Int = 0;
    var name: String = "name"
    var garden_number: Long = 1
    var program_number: Long = 1


    var wifiSSID: String = "WIFIFITO"
    var wifiPassword: String = "WIFIFITO"

    var useDHCP: Int = 0
    var ipAdress: String = "0.0.0.0"
    var mask: String = "255.255.255.255"
    var gateware: String = "0.0.0.0"

    var mqttLogin: String = "WIFIFITO"
    var mqttPassword: String = "WIFIFITO"

    var underWIFIControl: Int = 1
    var underMQTTControl: Int = 0


    public fun setLanSetting(lanSettingModel: LanSettingModel)
    {

    }
}