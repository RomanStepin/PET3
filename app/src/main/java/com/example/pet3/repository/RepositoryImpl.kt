package com.example.pet3.repository

import androidx.room.Room
import com.example.pet3.App
import com.example.pet3.repository.models.ConfigModel
import com.example.pet3.repository.models.LampModel
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel
import com.example.pet3.repository.room.Database
import com.example.pet3.repository.room.LampDao

class RepositoryImpl(app: App): Repository {


    var database = Room.databaseBuilder(app.applicationContext, Database::class.java, "Database").fallbackToDestructiveMigration().build()
    var programDao = database.getProgramDao()
    var lampDao: LampDao = database.getLampDao()

    override fun saveProgram(programModel: ProgramModel) {
        val programNumber = programDao.insertProgram(programModel = programModel)
        programModel.presets.forEach {
            savePreset(it, programNumber)
        }
    }

    override fun savePreset(presetModel: PresetModel, programNumber: Long) {
        presetModel.program_number = programNumber
        val presetNumber = programDao.insertPreset(presetModel = presetModel)
        presetModel.configs.forEach {
            saveConfig(it, presetNumber)
        }
    }

    override fun saveConfig(configModel: ConfigModel, presetNumber: Long) {
        configModel.preset_number = presetNumber
        programDao.insertConfig(configModel = configModel)
    }

    override fun getProgramByName(name: String): ProgramModel? {
        val programModel: ProgramModel? =  programDao.getProgramByName(name)
        if (programModel == null) {
            return null
        } else {
            programDao.getPresetsByProgramNumber(programModel.number)?.forEach { preset: PresetModel ->
                programDao.getConfigsByPresetNumber(preset.number)?.forEach { config: ConfigModel ->
                    preset.addConfig(config)
                }
                programModel.addPreset(preset)
            }
        }
        return  programModel
    }

    override fun updateProgram(newProgramModel: ProgramModel) {
        val programModel = programDao.getProgramByName(newProgramModel.name)
        if (programModel != null) {
            programDao.getPresetsByProgramNumber(programModel.number)?.forEach { preset ->
                programDao.deleteConfigsByPresetNumber(preset.number)
            }
            programDao.deletePresetsByProgramNumber(programModel.number)
            programDao.deleteProgramByName(programModel.name)
        }
            saveProgram(newProgramModel)
    }

    override fun saveLamp(lampModel: LampModel): Long {
        return lampDao.insertLamp(lampModel)
    }

    override fun updateLamp(lampModel: LampModel) {
        lampDao.updateLamp(lampModel)
    }

    override fun getLampByName(name: String): LampModel {
        return lampDao.getLampByName(name)
    }

    override fun getLampsByGardenNumber(garden_number: Long): List<LampModel> {
        return lampDao.getLampsByGardenNumber(garden_number)
    }

}