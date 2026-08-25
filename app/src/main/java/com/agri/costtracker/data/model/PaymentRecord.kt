package com.agri.costtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_records")
data class PaymentRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordId: Long = 0,
    val farmerId: Long = 0,
    val farmerName: String = "",
    val amount: Double,
    val date: String,
    val paymentMode: String = "Cash", // Cash, UPI, Bank Transfer
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
