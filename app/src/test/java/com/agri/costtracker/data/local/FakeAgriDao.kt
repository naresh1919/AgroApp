package com.agri.costtracker.data.local

import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.PaymentRecord
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeAgriDao : AgriDao {

    private val _farmersFlow = MutableStateFlow<List<Farmer>>(emptyList())
    private val _profileFlow = MutableStateFlow<FarmerProfile?>(null)
    private val _ratesFlow = MutableStateFlow<ServiceRates?>(null)
    private val _recordsFlow = MutableStateFlow<List<ActivityRecord>>(emptyList())
    private val _paymentsFlow = MutableStateFlow<List<PaymentRecord>>(emptyList())
    private var nextRecordId: Long = 1L
    private var nextFarmerId: Long = 1L
    private var nextPaymentId: Long = 1L

    override fun getAllFarmers(): Flow<List<Farmer>> = _farmersFlow.asStateFlow()

    override fun getFarmerById(id: Long): Flow<Farmer?> = _farmersFlow.map { list ->
        list.find { it.id == id }
    }

    override suspend fun insertFarmer(farmer: Farmer): Long {
        val idToUse = if (farmer.id == 0L) nextFarmerId++ else farmer.id
        val farmerToInsert = farmer.copy(id = idToUse)
        val current = _farmersFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == idToUse }
        if (index >= 0) {
            current[index] = farmerToInsert
        } else {
            current.add(0, farmerToInsert)
        }
        _farmersFlow.value = current
        return idToUse
    }

    override suspend fun insertAllFarmers(farmers: List<Farmer>) {
        farmers.forEach { insertFarmer(it) }
    }

    override suspend fun updateFarmer(farmer: Farmer) {
        val current = _farmersFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == farmer.id }
        if (index >= 0) {
            current[index] = farmer
            _farmersFlow.value = current
        }
    }

    override suspend fun deleteFarmer(farmer: Farmer) {
        val current = _farmersFlow.value.toMutableList()
        current.removeAll { it.id == farmer.id }
        _farmersFlow.value = current
    }

    override fun getFarmerProfile(): Flow<FarmerProfile?> = _profileFlow.asStateFlow()

    override suspend fun insertOrUpdateProfile(profile: FarmerProfile) {
        _profileFlow.value = profile
    }

    override fun getServiceRates(): Flow<ServiceRates?> = _ratesFlow.asStateFlow()

    override suspend fun insertOrUpdateRates(rates: ServiceRates) {
        _ratesFlow.value = rates
    }

    override fun getAllRecords(): Flow<List<ActivityRecord>> = _recordsFlow.asStateFlow()

    override fun getRecordsByFarmer(farmerId: Long): Flow<List<ActivityRecord>> {
        return _recordsFlow.map { list ->
            list.filter { it.farmerId == farmerId }
        }
    }

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
            current.add(0, recordToInsert)
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

    override fun getAllPayments(): Flow<List<PaymentRecord>> = _paymentsFlow.asStateFlow()

    override fun getPaymentsByFarmer(farmerId: Long): Flow<List<PaymentRecord>> {
        return _paymentsFlow.map { list ->
            list.filter { it.farmerId == farmerId }
        }
    }

    override fun getPaymentsByRecord(recordId: Long): Flow<List<PaymentRecord>> {
        return _paymentsFlow.map { list ->
            list.filter { it.recordId == recordId }
        }
    }

    override suspend fun insertPayment(payment: PaymentRecord): Long {
        val idToUse = if (payment.id == 0L) nextPaymentId++ else payment.id
        val paymentToInsert = payment.copy(id = idToUse)
        val current = _paymentsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == idToUse }
        if (index >= 0) {
            current[index] = paymentToInsert
        } else {
            current.add(0, paymentToInsert)
        }
        _paymentsFlow.value = current
        return idToUse
    }

    override suspend fun insertAllPayments(payments: List<PaymentRecord>) {
        payments.forEach { insertPayment(it) }
    }

    override suspend fun deletePayment(payment: PaymentRecord) {
        val current = _paymentsFlow.value.toMutableList()
        current.removeAll { it.id == payment.id }
        _paymentsFlow.value = current
    }
}
