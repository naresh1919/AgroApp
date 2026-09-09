package com.agri.costtracker.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.PaymentRecord
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import com.agri.costtracker.data.repository.AgriRepository
import com.agri.costtracker.data.sync.FirestoreSyncManager
import com.agri.costtracker.data.sync.CloudSyncState
import com.agri.costtracker.ui.localization.AppLanguage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MonthlyExpenditure(
    val monthLabel: String,
    val sprayingCost: Double = 0.0,
    val harvestingCost: Double = 0.0,
    val totalCost: Double = 0.0
)

data class DashboardMetrics(
    val totalRevenue: Double = 136200.0,
    val totalPaid: Double = 66450.0,
    val pendingPayables: Double = 69750.0,
    val totalSprayingAcres: Double = 120.0,
    val totalCuttingAcres: Double = 60.0,
    val totalAcresServed: Double = 180.0,
    val sprayingRate: Double = 450.0,
    val cropCuttingRate: Double = 1400.0,
    val farmersCount: Int = 4,
    val recordsCount: Int = 5,
    val monthlyBreakdown: List<MonthlyExpenditure> = emptyList()
)

class AgriViewModel(
    private val repository: AgriRepository,
    private val syncManager: FirestoreSyncManager? = null
) : ViewModel() {

    // Cloud Sync State & Actions
    val syncState: StateFlow<CloudSyncState> = syncManager?.syncState ?: MutableStateFlow(CloudSyncState.IDLE).asStateFlow()
    val syncMessage: StateFlow<String> = syncManager?.lastSyncMessage ?: MutableStateFlow("Ready to sync").asStateFlow()
    val signedInUser = syncManager?.signedInUser ?: MutableStateFlow(null).asStateFlow()
    val isAuthenticating = syncManager?.isAuthenticating ?: MutableStateFlow(false).asStateFlow()
    val authenticationError = syncManager?.authenticationError ?: MutableStateFlow<String?>(null).asStateFlow()

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            syncManager?.signInWithGoogle(activity)
        }
    }

    fun signOut() {
        syncManager?.signOut()
    }

    fun triggerCloudBackup() {
        viewModelScope.launch {
            syncManager?.backupToCloud(repository.agriDao)
        }
    }

    fun triggerCloudRestore() {
        viewModelScope.launch {
            syncManager?.restoreFromCloud(repository.agriDao)
        }
    }

    // Farmers State
    val allFarmers: StateFlow<List<Farmer>> = repository.allFarmersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val profile: StateFlow<FarmerProfile> = repository.profileFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FarmerProfile()
        )

    val rates: StateFlow<ServiceRates> = repository.ratesFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ServiceRates(sprayingRatePerAcre = 450.0, cropCuttingRatePerAcre = 1400.0)
        )

    val allRecords: StateFlow<List<ActivityRecord>> = repository.allRecordsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allPayments: StateFlow<List<PaymentRecord>> = repository.allPaymentsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    private val _filterStatus = MutableStateFlow<RecordStatus?>(null)
    val filterStatus: StateFlow<RecordStatus?> = _filterStatus.asStateFlow()

    private val _filterFarmerId = MutableStateFlow<Long?>(null)
    val filterFarmerId: StateFlow<Long?> = _filterFarmerId.asStateFlow()

    private val _selectedSeason = MutableStateFlow("2024")
    val selectedSeason: StateFlow<String> = _selectedSeason.asStateFlow()

    private val _selectedFarmerForStatement = MutableStateFlow<Farmer?>(null)
    val selectedFarmerForStatement: StateFlow<Farmer?> = _selectedFarmerForStatement.asStateFlow()

    // Filtered records based on season, status, and farmer
    val filteredRecords: StateFlow<List<ActivityRecord>> = combine(
        allRecords,
        _filterStatus,
        _filterFarmerId,
        _selectedSeason
    ) { records, status, farmerId, season ->
        records.filter { record ->
            val matchSeason = season.isEmpty() || record.season == season
            val matchStatus = status == null || record.status == status
            val matchFarmer = farmerId == null || record.farmerId == farmerId
            matchSeason && matchStatus && matchFarmer
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dynamic Business Dashboard Metrics
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        allFarmers,
        rates,
        allRecords,
        _selectedSeason
    ) { farmers, rts, records, currentSeason ->
        val activeSeasonRecords = records.filter { it.season == currentSeason }
        val totalRev = activeSeasonRecords.sumOf { it.cost }
        val totalPaidAmt = activeSeasonRecords.sumOf { it.paidAmount }
        val pending = (totalRev - totalPaidAmt).coerceAtLeast(0.0)
        val sprayAcres = activeSeasonRecords.filter { it.category == RecordCategory.SPRAYING }.sumOf { it.acres }
        val cuttingAcres = activeSeasonRecords.filter { it.category == RecordCategory.HARVESTING }.sumOf { it.acres }
        val totalAcres = activeSeasonRecords.sumOf { it.acres }

        val months = listOf("MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
        val monthlyFlow = months.map { m ->
            val monthRecords = activeSeasonRecords.filter { r ->
                r.date.uppercase().contains(m)
            }
            val spray = monthRecords.filter { it.category == RecordCategory.SPRAYING }.sumOf { it.cost }
            val harvest = monthRecords.filter { it.category == RecordCategory.HARVESTING }.sumOf { it.cost }
            MonthlyExpenditure(
                monthLabel = m,
                sprayingCost = spray,
                harvestingCost = harvest,
                totalCost = spray + harvest
            )
        }

        DashboardMetrics(
            totalRevenue = totalRev,
            totalPaid = totalPaidAmt,
            pendingPayables = pending,
            totalSprayingAcres = sprayAcres,
            totalCuttingAcres = cuttingAcres,
            totalAcresServed = totalAcres,
            sprayingRate = rts.sprayingRatePerAcre,
            cropCuttingRate = rts.cropCuttingRatePerAcre,
            farmersCount = farmers.size,
            recordsCount = activeSeasonRecords.size,
            monthlyBreakdown = monthlyFlow
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardMetrics()
    )

    // Farmer Operations
    fun addFarmer(
        name: String,
        mobile: String,
        village: String = "",
        totalAcres: Double = 0.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val newFarmer = Farmer(
                name = name.trim(),
                mobile = mobile.trim(),
                village = village.trim(),
                totalAcres = totalAcres,
                notes = notes.trim(),
                createdAt = System.currentTimeMillis()
            )
            repository.addFarmer(newFarmer)
        }
    }

    fun updateFarmer(farmer: Farmer) {
        viewModelScope.launch {
            repository.updateFarmer(farmer)
        }
    }

    fun deleteFarmer(farmer: Farmer) {
        viewModelScope.launch {
            repository.deleteFarmer(farmer)
        }
    }

    fun selectFarmerForStatement(farmer: Farmer?) {
        _selectedFarmerForStatement.value = farmer
    }

    fun setFilterFarmerId(farmerId: Long?) {
        _filterFarmerId.value = farmerId
    }

    fun setFilterStatus(status: RecordStatus?) {
        _filterStatus.value = status
    }

    fun setSeason(season: String) {
        _selectedSeason.value = season
    }

    // Service Rates Operations
    fun saveRates(
        sprayingRate: Double,
        cropCuttingRate: Double
    ) {
        viewModelScope.launch {
            val updated = rates.value.copy(
                sprayingRatePerAcre = sprayingRate,
                cropCuttingRatePerAcre = cropCuttingRate,
                lastUpdated = System.currentTimeMillis()
            )
            repository.updateRates(updated)
        }
    }

    // Service Booking / Activity Records
    fun addServiceRecord(
        farmerId: Long,
        farmerName: String,
        farmerMobile: String,
        title: String,
        category: RecordCategory,
        acres: Double,
        ratePerAcre: Double,
        paidAmount: Double,
        location: String,
        date: String = "Today",
        season: String = "2024",
        notes: String = ""
    ) {
        viewModelScope.launch {
            if (farmerId <= 0 || acres <= 0.0 || ratePerAcre <= 0.0) return@launch
            val calculatedCost = acres * ratePerAcre
            val cleanPaid = paidAmount.coerceIn(0.0, calculatedCost)
            val status = when {
                cleanPaid >= calculatedCost -> RecordStatus.COMPLETED
                cleanPaid > 0.0 -> RecordStatus.PARTIAL
                else -> RecordStatus.INVOICED
            }

            val record = ActivityRecord(
                farmerId = farmerId,
                farmerName = farmerName,
                farmerMobile = farmerMobile,
                title = title,
                category = category,
                acres = acres,
                ratePerAcre = ratePerAcre,
                cost = calculatedCost,
                paidAmount = cleanPaid,
                lastPaymentDate = if (cleanPaid > 0.0) date else "",
                location = location,
                status = status,
                date = date,
                season = season,
                notes = notes,
                timestamp = System.currentTimeMillis()
            )
            val newRecordId = repository.addRecord(record)

            if (cleanPaid > 0.0) {
                val advancePayment = PaymentRecord(
                    recordId = newRecordId,
                    farmerId = farmerId,
                    farmerName = farmerName,
                    amount = cleanPaid,
                    date = date,
                    paymentMode = "Advance Cash/UPI",
                    notes = "Advance payment at booking",
                    timestamp = System.currentTimeMillis()
                )
                repository.addPayment(advancePayment)
            }
        }
    }

    // Individual Partial / Installment Payment Recording
    fun recordPayment(
        record: ActivityRecord,
        additionalAmount: Double,
        paymentDate: String = "Today",
        paymentMode: String = "Cash",
        paymentNotes: String = ""
    ) {
        viewModelScope.launch {
            val outstandingBalance = (record.cost - record.paidAmount).coerceAtLeast(0.0)
            val acceptedAmount = additionalAmount.coerceIn(0.0, outstandingBalance)
            if (acceptedAmount <= 0.0) return@launch
            val newTotalPaid = record.paidAmount + acceptedAmount
            val newStatus = when {
                newTotalPaid >= record.cost -> RecordStatus.COMPLETED
                newTotalPaid > 0.0 -> RecordStatus.PARTIAL
                else -> RecordStatus.INVOICED
            }
            val paymentAuditEntry = "Paid ₹${String.format(java.util.Locale.US, "%,.0f", acceptedAmount)} on $paymentDate via $paymentMode${if (paymentNotes.isNotBlank()) " ($paymentNotes)" else ""}"
            val updatedNotes = if (record.notes.isNotBlank()) "${record.notes} | $paymentAuditEntry" else paymentAuditEntry

            val updatedRecord = record.copy(
                paidAmount = newTotalPaid,
                lastPaymentDate = paymentDate,
                status = newStatus,
                notes = updatedNotes
            )
            repository.addRecord(updatedRecord)

            // Insert individual PaymentRecord transaction
            val payment = PaymentRecord(
                recordId = record.id,
                farmerId = record.farmerId,
                farmerName = record.farmerName,
                amount = acceptedAmount,
                date = paymentDate,
                paymentMode = paymentMode,
                notes = paymentNotes,
                timestamp = System.currentTimeMillis()
            )
            repository.addPayment(payment)
        }
    }

    fun deleteRecord(record: ActivityRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }
}

class AgriViewModelFactory(
    private val repository: AgriRepository,
    private val syncManager: FirestoreSyncManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgriViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgriViewModel(repository, syncManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
