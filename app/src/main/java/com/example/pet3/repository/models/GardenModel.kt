package com.example.pet3.repository.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class GardenModel {
    @PrimaryKey(autoGenerate = true)
    var number: Long = 0

    var name: String = "name"
}