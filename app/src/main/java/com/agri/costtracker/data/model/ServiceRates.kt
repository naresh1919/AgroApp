package com.agri.costtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_rates")
data class ServiceRates(
    @PrimaryKey val id: Int = 1,
    val sprayingRatePerAcre: Double = 34.0, // Default in dashboard ($34.00), configurable in rates
    val cropCuttingRatePerAcre: Double = 85.0, // Default in dashboard ($85.00), configurable in rates
    val globalTrendPercent: Double = 4.2,
    val lastUpdated: Long = System.currentTimeMillis()
)
