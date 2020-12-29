package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fito.FitoParam

@Entity
class ConfigModel(config: FitoParam.LampConfig) {
    @PrimaryKey
    var number: Int = 0

    var preset_number: Int = 1

    var ch_1: Int = config.channelsList[0]

    val ch_2: Int = config.channelsList[1]

    val ch_3: Int = config.channelsList[2]

    val ch_4: Int = config.channelsList[3]

    val ch_5: Int = config.channelsList[4]

    val time_stamp: Int = config.timestamp

    val smoothing_duration: Int = config.smoothingDuration
}