package com.agri.costtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class Farmer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobile: String,
    val village: String = "",
    val totalAcres: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
