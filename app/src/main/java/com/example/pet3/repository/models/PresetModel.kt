package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fito.FitoParam

@Entity
class PresetModel(var preset: FitoParam.Preset) {

    @PrimaryKey(autoGenerate = true)
    var number: Int = 0

    var program_number: Int = 0

    var duration: Int = preset.duration

    var number_in_program: Int = preset.presetNumber

    var presets_count: Int = preset.presetsCount

    init {
        preset.configsList.forEach { configs.plus(ConfigModel(it)) }
    }

    @Ignore
    var configs: Array<ConfigModel> = arrayOf()

    public fun addConfig(config: ConfigModel) {
        configs.plus(config)
    }
}