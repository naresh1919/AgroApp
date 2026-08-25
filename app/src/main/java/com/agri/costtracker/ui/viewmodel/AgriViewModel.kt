package com.agri.costtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import com.agri.costtracker.data.repository.AgriRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalRevenue: Double = 9120.0,
    val pendingPayables: Double = 5100.0,
    val totalSprayingAcres: Double = 120.0,
    val totalCuttingAcres: Double = 60.0,
    val totalAcresServed: Double = 180.0,
    val sprayingRate: Double = 35.0,
    val cropCuttingRate: Double = 85.0,
    val farmersCount: Int = 4,
    val recordsCount: Int = 5
)

class AgriViewModel(private val repository: AgriRepository) : ViewModel() {

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
            initialValue = ServiceRates()
        )

    val allRecords: StateFlow<List<ActivityRecord>> = repository.allRecordsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _filterStatus = MutableStateFlow<RecordStatus?>(null)
    val filterStatus: StateFlow<RecordStatus?> = _filterStatus.asStateFlow()

    private val _filterFarmerId = MutableStateFlow<Long?>(null)
    val filterFarmerId: StateFlow<Long?> = _filterFarmerId.asStateFlow()

    private val _selectedSeason = MutableStateFlow("2024")
    val selectedSeason: StateFlow<String> = _selectedSeason.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(com.agri.costtracker.ui.localization.AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<com.agri.costtracker.ui.localization.AppLanguage> = _selectedLanguage.asStateFlow()

    fun setLanguage(language: com.agri.costtracker.ui.localization.AppLanguage) {
        _selectedLanguage.value = language
    }

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
        allRecords
    ) { farmers, rts, records ->
        val activeSeasonRecords = records.filter { it.season == "2024" }
        val totalRev = activeSeasonRecords.sumOf { it.cost }
        val pending = activeSeasonRecords.filter { it.status == RecordStatus.INVOICED }.sumOf { it.cost }
        val sprayAcres = activeSeasonRecords.filter { it.category == RecordCategory.SPRAYING }.sumOf { it.acres }
        val cuttingAcres = activeSeasonRecords.filter { it.category == RecordCategory.HARVESTING }.sumOf { it.acres }
        val totalAcres = activeSeasonRecords.sumOf { it.acres }

        DashboardMetrics(
            totalRevenue = totalRev,
            pendingPayables = pending,
            totalSprayingAcres = sprayAcres,
            totalCuttingAcres = cuttingAcres,
            totalAcresServed = totalAcres,
            sprayingRate = rts.sprayingRatePerAcre,
            cropCuttingRate = rts.cropCuttingRatePerAcre,
            farmersCount = farmers.size,
            recordsCount = activeSeasonRecords.size
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
        location: String,
        status: RecordStatus,
        date: String = "Today",
        season: String = "2024",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val calculatedCost = acres * ratePerAcre
            val record = ActivityRecord(
                farmerId = farmerId,
                farmerName = farmerName,
                farmerMobile = farmerMobile,
                title = title,
                category = category,
                acres = acres,
                ratePerAcre = ratePerAcre,
                cost = calculatedCost,
                location = location,
                status = status,
                date = date,
                season = season,
                notes = notes,
                timestamp = System.currentTimeMillis()
            )
            repository.addRecord(record)
        }
    }

    fun deleteRecord(record: ActivityRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }
}

class AgriViewModelFactory(private val repository: AgriRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgriViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgriViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
