package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fito.FitoParam

@Entity
class PresetModel constructor() {
    constructor(preset: FitoParam.Preset) : this() {
        duration = preset.duration
        number_in_program = preset.presetNumber
        presets_count= preset.presetsCount
        preset.configsList.forEach {
            configs = configs.plus(ConfigModel(it))
        }
    }
    @PrimaryKey(autoGenerate = true)
    var number: Long = 0

    var program_number: Long = 0

    var duration: Int = 0

    var number_in_program: Int = 0

    var presets_count: Int = 0

    @Ignore
    var configs: Array<ConfigModel> = arrayOf()

    public fun addConfig(config: ConfigModel) {
        configs = configs.plus(config)
    }
}