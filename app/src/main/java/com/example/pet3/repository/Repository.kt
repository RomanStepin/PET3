package com.example.pet3.repository

import com.example.pet3.App
import com.example.pet3.repository.models.*

interface Repository{
    fun saveProgram(programModel: ProgramModel)
    fun savePreset(presetModel: PresetModel, programNumber: Long)
    fun saveConfig(configModel: ConfigModel, presetNumber: Long)
    fun getProgramByName(name: String): ProgramModel?
    fun updateProgram(newProgramModel: ProgramModel)


    fun saveLamp(lampModel: LampModel): Long
    fun updateLamp(lampModel: LampModel)
    fun getLampByName(name: String): LampModel
    fun getLampsByGardenNumber(garden_number: Long): List<LampModel>?
    fun saveGarden(gardenModel: GardenModel): Long
}