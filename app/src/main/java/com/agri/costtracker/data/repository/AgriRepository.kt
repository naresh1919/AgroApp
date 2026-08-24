package com.agri.costtracker.data.repository

import com.agri.costtracker.data.local.AgriDao
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.flow.Flow

class AgriRepository(private val dao: AgriDao) {

    val profileFlow: Flow<FarmerProfile?> = dao.getFarmerProfile()
    val ratesFlow: Flow<ServiceRates?> = dao.getServiceRates()
    val allRecordsFlow: Flow<List<ActivityRecord>> = dao.getAllRecords()

    fun getRecordsBySeason(season: String): Flow<List<ActivityRecord>> {
        return dao.getRecordsBySeason(season)
    }

    fun getRecordsByStatus(status: RecordStatus): Flow<List<ActivityRecord>> {
        return dao.getRecordsByStatus(status)
    }

    suspend fun updateProfile(profile: FarmerProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun updateRates(rates: ServiceRates) {
        dao.insertOrUpdateRates(rates)
    }

    suspend fun addRecord(record: ActivityRecord): Long {
        return dao.insertRecord(record)
    }

    suspend fun deleteRecord(record: ActivityRecord) {
        dao.deleteRecord(record)
    }
}
