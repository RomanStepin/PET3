package com.example.pet3.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM ProgramModel WHERE name = :name")
    fun getProgramByName(name: String): ProgramModel?

    @Query("SELECT * FROM PresetModel WHERE program_number = :program_number")
    fun getPresetsByProgramNumber(program_number: Long): List<PresetModel>?

    @Query("SELECT * FROM ConfigModel WHERE preset_number = :preset_number")
    fun getConfigsByPresetNumber(preset_number: Long): List<ConfigModel>?

    @Update
    fun updateProgram(programModel: ProgramModel)


    @Query("DELETE FROM ProgramModel WHERE name = :name")
    public fun deleteProgramByName(name: String)

    @Query("DELETE FROM PresetModel WHERE program_number = :program_number")
    public fun deletePresetsByProgramNumber(program_number: Long)

    @Query("DELETE FROM ConfigModel WHERE preset_number = :preset_number")
    public fun deleteConfigsByPresetNumber(preset_number: Long)
}