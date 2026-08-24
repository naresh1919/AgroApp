package com.agri.costtracker.data.local

import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeAgriDao : AgriDao {

    private val _profileFlow = MutableStateFlow<FarmerProfile?>(null)
    private val _ratesFlow = MutableStateFlow<ServiceRates?>(null)
    private val _recordsFlow = MutableStateFlow<List<ActivityRecord>>(emptyList())
    private var nextRecordId: Long = 1L

    override fun getFarmerProfile(): Flow<FarmerProfile?> = _profileFlow.asStateFlow()

    override suspend fun insertOrUpdateProfile(profile: FarmerProfile) {
        _profileFlow.value = profile
    }

    override fun getServiceRates(): Flow<ServiceRates?> = _ratesFlow.asStateFlow()

    override suspend fun insertOrUpdateRates(rates: ServiceRates) {
        _ratesFlow.value = rates
    }

    override fun getAllRecords(): Flow<List<ActivityRecord>> = _recordsFlow.asStateFlow()

    override fun getRecordsBySeason(season: String): Flow<List<ActivityRecord>> {
        return _recordsFlow.map { list ->
            list.filter { it.season == season }
        }
    }

    override fun getRecordsByStatus(status: RecordStatus): Flow<List<ActivityRecord>> {
        return _recordsFlow.map { list ->
            list.filter { it.status == status }
        }
    }

    override suspend fun insertRecord(record: ActivityRecord): Long {
        val idToUse = if (record.id == 0L) nextRecordId++ else record.id
        val recordToInsert = record.copy(id = idToUse)
        val current = _recordsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == idToUse }
        if (index >= 0) {
            current[index] = recordToInsert
        } else {
            current.add(0, recordToInsert) // ordered descending by timestamp
        }
        _recordsFlow.value = current
        return idToUse
    }

    override suspend fun insertAllRecords(records: List<ActivityRecord>) {
        records.forEach { insertRecord(it) }
    }

    override suspend fun deleteRecord(record: ActivityRecord) {
        val current = _recordsFlow.value.toMutableList()
        current.removeAll { it.id == record.id }
        _recordsFlow.value = current
    }

    override suspend fun clearAllRecords() {
        _recordsFlow.value = emptyList()
    }
}
