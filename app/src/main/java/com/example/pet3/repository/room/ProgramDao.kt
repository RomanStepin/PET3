package com.example.pet3.repository.room

import androidx.room.Dao
import androidx.room.Insert
import com.example.pet3.repository.models.ConfigModel
import com.example.pet3.repository.models.PresetModel
import com.example.pet3.repository.models.ProgramModel


@Dao
public interface ProgramDao {
    @Insert
    public fun insertProgram(programModel: ProgramModel): Long

    @Insert
    public fun insertPreset(presetModel: PresetModel): Long

    @Insert
    public fun insertConfig(configModel: ConfigModel): Long
}