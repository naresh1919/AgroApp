package com.agri.costtracker.data.local

import androidx.room.*
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.PaymentRecord
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.flow.Flow

@Dao
interface AgriDao {

    // Farmers
    @Query("SELECT * FROM farmers ORDER BY createdAt DESC")
    fun getAllFarmers(): Flow<List<Farmer>>

    @Query("SELECT * FROM farmers WHERE id = :id")
    fun getFarmerById(id: Long): Flow<Farmer?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: Farmer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFarmers(farmers: List<Farmer>)

    @Update
    suspend fun updateFarmer(farmer: Farmer)

    @Delete
    suspend fun deleteFarmer(farmer: Farmer)

    @Query("DELETE FROM activity_records WHERE farmerId = :farmerId")
    suspend fun deleteRecordsByFarmerId(farmerId: Long)

    @Query("DELETE FROM payment_records WHERE farmerId = :farmerId")
    suspend fun deletePaymentsByFarmerId(farmerId: Long)

    @Transaction
    suspend fun deleteFarmerWithRelatedData(farmer: Farmer) {
        deletePaymentsByFarmerId(farmer.id)
        deleteRecordsByFarmerId(farmer.id)
        deleteFarmer(farmer)
    }

    // Business / Operator Profile
    @Query("SELECT * FROM farmer_profile WHERE id = 1")
    fun getFarmerProfile(): Flow<FarmerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: FarmerProfile)

    // Service Rates
    @Query("SELECT * FROM service_rates WHERE id = 1")
    fun getServiceRates(): Flow<ServiceRates?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRates(rates: ServiceRates)

    // Activity Records
    @Query("SELECT * FROM activity_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE farmerId = :farmerId ORDER BY timestamp DESC")
    fun getRecordsByFarmer(farmerId: Long): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE season = :season ORDER BY timestamp DESC")
    fun getRecordsBySeason(season: String): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE status = :status ORDER BY timestamp DESC")
    fun getRecordsByStatus(status: RecordStatus): Flow<List<ActivityRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ActivityRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecords(records: List<ActivityRecord>)

    @Delete
    suspend fun deleteRecord(record: ActivityRecord)

    @Query("DELETE FROM payment_records WHERE recordId = :recordId")
    suspend fun deletePaymentsByRecordId(recordId: Long)

    @Transaction
    suspend fun deleteRecordWithPayments(record: ActivityRecord) {
        deletePaymentsByRecordId(record.id)
        deleteRecord(record)
    }

    @Query("DELETE FROM activity_records")
    suspend fun clearAllRecords()

    // Individual Payment Records / Installments
    @Query("SELECT * FROM payment_records ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payment_records WHERE farmerId = :farmerId ORDER BY timestamp DESC")
    fun getPaymentsByFarmer(farmerId: Long): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payment_records WHERE recordId = :recordId ORDER BY timestamp ASC")
    fun getPaymentsByRecord(recordId: Long): Flow<List<PaymentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayments(payments: List<PaymentRecord>)

    @Delete
    suspend fun deletePayment(payment: PaymentRecord)

    // Snapshot queries for Cloud Sync & Backup
    @Query("SELECT * FROM farmers")
    suspend fun getAllFarmersSync(): List<Farmer>

    @Query("SELECT * FROM activity_records")
    suspend fun getAllRecordsSync(): List<ActivityRecord>

    @Query("SELECT * FROM payment_records")
    suspend fun getAllPaymentsSync(): List<PaymentRecord>

    @Query("SELECT * FROM service_rates WHERE id = 1")
    suspend fun getRatesSync(): ServiceRates?

    @Query("SELECT * FROM farmer_profile WHERE id = 1")
    suspend fun getProfileSync(): FarmerProfile?
}
