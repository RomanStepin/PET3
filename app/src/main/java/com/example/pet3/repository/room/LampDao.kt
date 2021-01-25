package com.example.pet3.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pet3.repository.models.LampModel

@Dao
interface LampDao {
    @Insert
    fun insertLamp(lampModel: LampModel): Long

    @Update
    fun updateLamp(lampModel: LampModel): Long

    @Query("SELECT * FROM LampModel WHERE name = :name LIMIT 1")
    fun getLampByName(name: String): LampModel

    @Query("SELECT * FROM LampModel WHERE garden_number = :garden_number")
    fun getLampsByGardenNumber(garden_number: Long): List<LampModel>
}