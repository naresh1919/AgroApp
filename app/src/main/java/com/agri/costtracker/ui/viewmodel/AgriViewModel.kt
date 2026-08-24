package com.agri.costtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import com.agri.costtracker.data.repository.AgriRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalAcres: Double = 1240.0,
    val cultivatedAcres: Double = 842.0,
    val fallowAcres: Double = 398.0,
    val sprayingRate: Double = 34.0,
    val cropCuttingRate: Double = 85.0,
    val estimatedSprayingCost: Double = 42160.0,
    val estimatedHarvestingCost: Double = 105400.0,
    val totalSeasonalInvestment: Double = 142850.0,
    val pendingPayables: Double = 4210.0,
    val recordsCount: Int = 42
)

class AgriViewModel(private val repository: AgriRepository) : ViewModel() {

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

    private val _selectedSeason = MutableStateFlow("2024")
    val selectedSeason: StateFlow<String> = _selectedSeason.asStateFlow()

    // Filtered records based on season and status
    val filteredRecords: StateFlow<List<ActivityRecord>> = combine(
        allRecords,
        _filterStatus,
        _selectedSeason
    ) { records, status, season ->
        records.filter { record ->
            val matchSeason = season.isEmpty() || record.season == season
            val matchStatus = status == null || record.status == status
            matchSeason && matchStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dynamic Dashboard Metrics
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        profile,
        rates,
        allRecords
    ) { prof, rts, records ->
        val totalAcres = prof.totalOwnedAcres
        val sprayingCost = totalAcres * rts.sprayingRatePerAcre
        val harvestingCost = totalAcres * rts.cropCuttingRatePerAcre

        val activeSeasonRecords = records.filter { it.season == "2024" }
        val totalInvestment = activeSeasonRecords.sumOf { it.cost }
        val pendingPayables = activeSeasonRecords.filter { it.status == RecordStatus.INVOICED }.sumOf { it.cost }

        DashboardMetrics(
            totalAcres = totalAcres,
            cultivatedAcres = prof.cultivatedAcres,
            fallowAcres = prof.fallowAcres,
            sprayingRate = rts.sprayingRatePerAcre,
            cropCuttingRate = rts.cropCuttingRatePerAcre,
            estimatedSprayingCost = sprayingCost,
            estimatedHarvestingCost = harvestingCost,
            totalSeasonalInvestment = if (totalInvestment > 0) totalInvestment else 142850.0,
            pendingPayables = if (pendingPayables > 0) pendingPayables else 4210.0,
            recordsCount = if (activeSeasonRecords.isNotEmpty()) activeSeasonRecords.size else 42
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardMetrics()
    )

    fun saveProfile(
        fullName: String,
        totalOwnedAcres: Double,
        cultivatedAcres: Double? = null,
        fallowAcres: Double? = null,
        sector: String = "Central Valley Sector 7"
    ) {
        viewModelScope.launch {
            val cult = cultivatedAcres ?: (totalOwnedAcres * 0.68)
            val fall = fallowAcres ?: (totalOwnedAcres - cult)
            val updated = profile.value.copy(
                fullName = fullName,
                totalOwnedAcres = totalOwnedAcres,
                cultivatedAcres = cult,
                fallowAcres = fall,
                sector = sector
            )
            repository.updateProfile(updated)
        }
    }

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

    fun addRecord(
        title: String,
        category: RecordCategory,
        cost: Double,
        location: String,
        status: RecordStatus,
        date: String = "Today",
        season: String = "2024"
    ) {
        viewModelScope.launch {
            val record = ActivityRecord(
                title = title,
                category = category,
                cost = cost,
                location = location,
                status = status,
                date = date,
                season = season,
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

    fun setFilterStatus(status: RecordStatus?) {
        _filterStatus.value = status
    }

    fun setSeason(season: String) {
        _selectedSeason.value = season
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
