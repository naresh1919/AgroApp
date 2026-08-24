package com.agri.costtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RecordStatus {
    COMPLETED,
    INVOICED,
    ARCHIVED
}

enum class RecordCategory {
    LAND,
    SEEDS,
    MAINTENANCE,
    IRRIGATION,
    SPRAYING,
    HARVESTING,
    OTHER
}

@Entity(tableName = "activity_records")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,
    val location: String,
    val cost: Double,
    val status: RecordStatus,
    val category: RecordCategory,
    val season: String = "2024",
    val timestamp: Long = System.currentTimeMillis()
)
