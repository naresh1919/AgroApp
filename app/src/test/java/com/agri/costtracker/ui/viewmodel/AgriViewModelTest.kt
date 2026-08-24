package com.agri.costtracker.ui.viewmodel

import com.agri.costtracker.data.local.FakeAgriDao
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import com.agri.costtracker.data.repository.AgriRepository
import com.agri.costtracker.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AgriViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeAgriDao
    private lateinit var repository: AgriRepository
    private lateinit var viewModel: AgriViewModel

    @Before
    fun setUp() {
        fakeDao = FakeAgriDao()
        repository = AgriRepository(fakeDao)
        viewModel = AgriViewModel(repository)
    }

    @Test
    fun initialStates_haveExpectedDefaults() = runTest {
        // Collect state flows to activate WhileSubscribed
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.profile.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.rates.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.allRecords.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.filteredRecords.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.dashboardMetrics.collect() }

        assertEquals("2024", viewModel.selectedSeason.value)
        assertNull(viewModel.filterStatus.value)
        assertEquals("Elias Thorne", viewModel.profile.value.fullName)
        assertEquals(34.0, viewModel.rates.value.sprayingRatePerAcre, 0.001)
        assertEquals(85.0, viewModel.rates.value.cropCuttingRatePerAcre, 0.001)
        assertTrue(viewModel.allRecords.value.isEmpty())
        assertTrue(viewModel.filteredRecords.value.isEmpty())
    }

    @Test
    fun saveProfile_withNullCultivatedAndFallow_computesDefaults() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.profile.collect() }

        viewModel.saveProfile(
            fullName = "Arthur Pendelton",
            totalOwnedAcres = 1000.0,
            sector = "Sector Alpha"
        )
        advanceUntilIdle()

        val updatedProfile = viewModel.profile.value
        assertEquals("Arthur Pendelton", updatedProfile.fullName)
        assertEquals(1000.0, updatedProfile.totalOwnedAcres, 0.001)
        // 68% of 1000 = 680.0
        assertEquals(680.0, updatedProfile.cultivatedAcres, 0.001)
        // 1000 - 680 = 320.0
        assertEquals(320.0, updatedProfile.fallowAcres, 0.001)
        assertEquals("Sector Alpha", updatedProfile.sector)
    }

    @Test
    fun saveProfile_withExplicitCultivatedAndFallow_usesExplicitValues() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.profile.collect() }

        viewModel.saveProfile(
            fullName = "Arthur Pendelton",
            totalOwnedAcres = 1000.0,
            cultivatedAcres = 750.0,
            fallowAcres = 250.0,
            sector = "Sector Beta"
        )
        advanceUntilIdle()

        val updatedProfile = viewModel.profile.value
        assertEquals("Arthur Pendelton", updatedProfile.fullName)
        assertEquals(750.0, updatedProfile.cultivatedAcres, 0.001)
        assertEquals(250.0, updatedProfile.fallowAcres, 0.001)
    }

    @Test
    fun saveRates_updatesRateValues() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.rates.collect() }

        viewModel.saveRates(
            sprayingRate = 45.0,
            cropCuttingRate = 92.0
        )
        advanceUntilIdle()

        val updatedRates = viewModel.rates.value
        assertEquals(45.0, updatedRates.sprayingRatePerAcre, 0.001)
        assertEquals(92.0, updatedRates.cropCuttingRatePerAcre, 0.001)
    }

    @Test
    fun addRecord_and_deleteRecord_updatesFlows() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.allRecords.collect() }

        viewModel.addRecord(
            title = "Pest Control",
            category = RecordCategory.SPRAYING,
            cost = 2400.0,
            location = "East Field",
            status = RecordStatus.COMPLETED,
            date = "Oct 24, 2024",
            season = "2024"
        )
        advanceUntilIdle()

        val records = viewModel.allRecords.value
        assertEquals(1, records.size)
        val added = records[0]
        assertEquals("Pest Control", added.title)
        assertEquals(2400.0, added.cost, 0.001)
        assertEquals(RecordCategory.SPRAYING, added.category)
        assertEquals(RecordStatus.COMPLETED, added.status)

        viewModel.deleteRecord(added)
        advanceUntilIdle()

        assertTrue(viewModel.allRecords.value.isEmpty())
    }

    @Test
    fun filterStatus_and_seasonFilter_correctlyFiltersRecords() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.allRecords.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.filteredRecords.collect() }

        // Seed multiple records with different seasons and statuses
        viewModel.addRecord(
            title = "Record 1 (2024 COMPLETED)",
            category = RecordCategory.LAND,
            cost = 1000.0,
            location = "A",
            status = RecordStatus.COMPLETED,
            season = "2024"
        )
        viewModel.addRecord(
            title = "Record 2 (2024 INVOICED)",
            category = RecordCategory.SEEDS,
            cost = 2000.0,
            location = "B",
            status = RecordStatus.INVOICED,
            season = "2024"
        )
        viewModel.addRecord(
            title = "Record 3 (2023 COMPLETED)",
            category = RecordCategory.MAINTENANCE,
            cost = 3000.0,
            location = "C",
            status = RecordStatus.COMPLETED,
            season = "2023"
        )
        advanceUntilIdle()

        assertEquals(3, viewModel.allRecords.value.size)

        // Default season is "2024", filterStatus is null -> should match Records 1 & 2
        assertEquals(2, viewModel.filteredRecords.value.size)

        // Filter by INVOICED status
        viewModel.setFilterStatus(RecordStatus.INVOICED)
        advanceUntilIdle()
        assertEquals(1, viewModel.filteredRecords.value.size)
        assertEquals("Record 2 (2024 INVOICED)", viewModel.filteredRecords.value[0].title)

        // Filter by COMPLETED status
        viewModel.setFilterStatus(RecordStatus.COMPLETED)
        advanceUntilIdle()
        assertEquals(1, viewModel.filteredRecords.value.size)
        assertEquals("Record 1 (2024 COMPLETED)", viewModel.filteredRecords.value[0].title)

        // Change season to "2023" with COMPLETED status
        viewModel.setSeason("2023")
        advanceUntilIdle()
        assertEquals(1, viewModel.filteredRecords.value.size)
        assertEquals("Record 3 (2023 COMPLETED)", viewModel.filteredRecords.value[0].title)

        // Clear filter status
        viewModel.setFilterStatus(null)
        advanceUntilIdle()
        assertEquals(1, viewModel.filteredRecords.value.size)

        // Empty season string matches all seasons
        viewModel.setSeason("")
        advanceUntilIdle()
        assertEquals(3, viewModel.filteredRecords.value.size)
    }

    @Test
    fun dashboardMetrics_calculationsWithRecords_areAccurate() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.profile.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.rates.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.allRecords.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.dashboardMetrics.collect() }

        // Set profile and rates
        viewModel.saveProfile(
            fullName = "Elias Thorne",
            totalOwnedAcres = 1000.0,
            cultivatedAcres = 600.0,
            fallowAcres = 400.0
        )
        viewModel.saveRates(
            sprayingRate = 30.0,
            cropCuttingRate = 80.0
        )

        // Add 2024 records
        viewModel.addRecord(
            title = "Completed Land Work",
            category = RecordCategory.LAND,
            cost = 10000.0,
            location = "Loc 1",
            status = RecordStatus.COMPLETED,
            season = "2024"
        )
        viewModel.addRecord(
            title = "Invoiced Seed Supply",
            category = RecordCategory.SEEDS,
            cost = 5000.0,
            location = "Loc 2",
            status = RecordStatus.INVOICED,
            season = "2024"
        )
        // Add 2023 record (should not affect 2024 metrics)
        viewModel.addRecord(
            title = "Past Year Record",
            category = RecordCategory.OTHER,
            cost = 20000.0,
            location = "Loc 3",
            status = RecordStatus.INVOICED,
            season = "2023"
        )
        advanceUntilIdle()

        val metrics = viewModel.dashboardMetrics.value
        assertEquals(1000.0, metrics.totalAcres, 0.001)
        assertEquals(600.0, metrics.cultivatedAcres, 0.001)
        assertEquals(400.0, metrics.fallowAcres, 0.001)
        assertEquals(30.0, metrics.sprayingRate, 0.001)
        assertEquals(80.0, metrics.cropCuttingRate, 0.001)

        // estimatedSprayingCost = 1000 * 30 = 30000.0
        assertEquals(30000.0, metrics.estimatedSprayingCost, 0.001)
        // estimatedHarvestingCost = 1000 * 80 = 80000.0
        assertEquals(80000.0, metrics.estimatedHarvestingCost, 0.001)
        // totalSeasonalInvestment = 10000 + 5000 = 15000.0
        assertEquals(15000.0, metrics.totalSeasonalInvestment, 0.001)
        // pendingPayables = 5000.0 (only the INVOICED record in 2024)
        assertEquals(5000.0, metrics.pendingPayables, 0.001)
        // recordsCount = 2 (2024 records count)
        assertEquals(2, metrics.recordsCount)
    }

    @Test
    fun dashboardMetrics_withNoRecords_returnsFallbackValues() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.profile.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.rates.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.allRecords.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.dashboardMetrics.collect() }

        val metrics = viewModel.dashboardMetrics.value
        // Fallback default total investment is 142850.0
        assertEquals(142850.0, metrics.totalSeasonalInvestment, 0.001)
        // Fallback default pending payables is 4210.0
        assertEquals(4210.0, metrics.pendingPayables, 0.001)
        // Fallback default records count is 42
        assertEquals(42, metrics.recordsCount)
    }
}
