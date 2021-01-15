package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fito.FitoParam


// класс для хранения программы в базе

@Entity
class ProgramModel() {
    @PrimaryKey(autoGenerate = true)
    var number: Long = 0
    var name: String = "name1"

    @Transient
    var presets: Array<PresetModel> = arrayOf()

    @Ignore
    public fun addPreset(preset: PresetModel) {
        presets = presets.plus(preset)
    }
}