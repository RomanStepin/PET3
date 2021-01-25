package com.example.pet3.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pet3.repository.models.ConfigModel
import com.example.pet3.repository.models.LampModel
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel

@Database(entities = [ProgramModel::class, PresetModel::class, ConfigModel::class, LampModel::class], version = 2)
public abstract class Database: RoomDatabase() {
    public abstract fun getProgramDao(): ProgramDao
    public abstract fun getLampDao(): LampDao
}