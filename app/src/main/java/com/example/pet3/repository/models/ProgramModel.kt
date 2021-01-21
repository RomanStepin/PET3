package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fito.FitoParam
import java.util.*
import kotlin.collections.ArrayList


// класс для хранения программы в базе

@Entity
class ProgramModel() {
    @PrimaryKey(autoGenerate = true)
    var number: Long = 0
    @Ignore
    private val r: Random = Random(100)
    var name: String = "name" + r.nextInt(2)

    @Transient
    var presets: Array<PresetModel> = arrayOf()

    @Ignore
    public fun addPreset(preset: PresetModel) {
        presets = presets.plus(preset)
    }
}