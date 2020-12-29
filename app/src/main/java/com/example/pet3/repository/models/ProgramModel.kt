package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fito.FitoParam


@Entity
class ProgramModel() {
    @PrimaryKey
    var number: Int = 0
    var name: String = "name1"
    @Ignore
    val presets: Array<PresetModel> = arrayOf()

    public fun addPreset(preset: PresetModel) {
        presets.plus(preset)
    }
}