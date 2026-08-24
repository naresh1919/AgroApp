package com.agri.costtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmer_profile")
data class FarmerProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Elias Thorne",
    val totalOwnedAcres: Double = 1240.0,
    val cultivatedAcres: Double = 842.0,
    val fallowAcres: Double = 398.0,
    val sector: String = "Central Valley Sector 7",
    val region: String = "Central Plains",
    val avatarUrl: String = ""
)
