package com.agri.costtracker.data.local

import androidx.room.*
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.FarmerProfile
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

    @Query("DELETE FROM activity_records")
    suspend fun clearAllRecords()
}
