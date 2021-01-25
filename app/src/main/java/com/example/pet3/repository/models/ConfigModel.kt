package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fito.FitoParam

@Entity
class ConfigModel() {

    constructor(config: FitoParam.LampConfig) : this() {
        ch_1 = config.channelsList[0]

        ch_2 = config.channelsList[1]

        ch_3 = config.channelsList[2]

        ch_4 = config.channelsList[3]

        ch_5 = config.channelsList[4]

        time_stamp = config.timestamp

        smoothing_duration = config.smoothingDuration
    }


    @PrimaryKey(autoGenerate = true)
    var number: Long = 0

    var preset_number: Long = 0

    var ch_1: Int = 0

    var ch_2: Int = 0

    var ch_3: Int = 0

    var ch_4: Int = 0

    var ch_5: Int = 0

    var time_stamp: Int = 0

    var smoothing_duration: Int = 0

    public fun toProtoClass(): FitoParam.LampConfig {
        val config: FitoParam.LampConfig = FitoParam.LampConfig.newBuilder().also {
            it.addChannels(ch_1)
            it.addChannels(ch_2)
            it.addChannels(ch_3)
            it.addChannels(ch_4)
            it.addChannels(ch_5)
            it.smoothingDuration = smoothing_duration
            it.timestamp = time_stamp
        }.build()

        return config
    }
}