package com.agri.costtracker.data.repository

import com.agri.costtracker.data.local.AgriDao
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.PaymentRecord
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.flow.Flow

class AgriRepository(private val dao: AgriDao) {

    val agriDao: AgriDao = dao

    // Farmers
    val allFarmersFlow: Flow<List<Farmer>> = dao.getAllFarmers()

    fun getFarmerById(id: Long): Flow<Farmer?> = dao.getFarmerById(id)

    suspend fun addFarmer(farmer: Farmer): Long = dao.insertFarmer(farmer)

    suspend fun updateFarmer(farmer: Farmer) = dao.updateFarmer(farmer)

    suspend fun deleteFarmer(farmer: Farmer) = dao.deleteFarmerWithRelatedData(farmer)

    // Business Profile & Rates
    val profileFlow: Flow<FarmerProfile?> = dao.getFarmerProfile()
    val ratesFlow: Flow<ServiceRates?> = dao.getServiceRates()
    val allRecordsFlow: Flow<List<ActivityRecord>> = dao.getAllRecords()

    fun getRecordsByFarmer(farmerId: Long): Flow<List<ActivityRecord>> {
        return dao.getRecordsByFarmer(farmerId)
    }

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
        dao.deleteRecordWithPayments(record)
    }

    // Individual Payment Records / Installments
    val allPaymentsFlow: Flow<List<PaymentRecord>> = dao.getAllPayments()

    fun getPaymentsByFarmer(farmerId: Long): Flow<List<PaymentRecord>> {
        return dao.getPaymentsByFarmer(farmerId)
    }

    fun getPaymentsByRecord(recordId: Long): Flow<List<PaymentRecord>> {
        return dao.getPaymentsByRecord(recordId)
    }

    suspend fun addPayment(payment: PaymentRecord): Long {
        return dao.insertPayment(payment)
    }

    suspend fun deletePayment(payment: PaymentRecord) {
        dao.deletePayment(payment)
    }
}
