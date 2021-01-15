package com.example.pet3.repository

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pet3.App
import com.example.pet3.repository.models.ConfigModel
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel
import com.example.pet3.repository.room.Database
import com.example.pet3.repository.room.ProgramDao

class RepositoryImpl(app: App): Repository {

    lateinit var programDao: ProgramDao
    lateinit var database: Database

    init {
        database = Room.databaseBuilder(app.applicationContext, Database::class.java, "Database").fallbackToDestructiveMigration().build()
        programDao = database.getProgramDao()
    }

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

}