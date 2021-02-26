package com.example.pet3.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pet3.repository.models.*

@Database(entities = [ProgramModel::class, PresetModel::class, ConfigModel::class, LampModel::class, GardenModel::class], version = 3)
public abstract class Database: RoomDatabase() {
    public abstract fun getProgramDao(): ProgramDao
    public abstract fun getLampDao(): LampDao
}