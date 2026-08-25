package com.agri.costtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RecordStatus {
    COMPLETED,
    INVOICED,
    ARCHIVED
}

enum class RecordCategory {
    SPRAYING,      // Drone Spraying
    HARVESTING,    // Cutting Machine / Harvester
    LAND,          // Land Prep / Tillage
    SEEDS,         // Seeds
    MAINTENANCE,   // Maintenance
    IRRIGATION,    // Irrigation
    OTHER          // Custom / Other
}

@Entity(tableName = "activity_records")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmerId: Long = 0,
    val farmerName: String = "",
    val farmerMobile: String = "",
    val title: String,
    val date: String,
    val location: String,
    val cost: Double,
    val acres: Double = 0.0,
    val ratePerAcre: Double = 0.0,
    val status: RecordStatus = RecordStatus.COMPLETED,
    val category: RecordCategory = RecordCategory.SPRAYING,
    val season: String = "2024",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
