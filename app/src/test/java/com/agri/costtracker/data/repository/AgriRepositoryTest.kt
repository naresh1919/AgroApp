package com.agri.costtracker.data.repository

import com.agri.costtracker.data.local.FakeAgriDao
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AgriRepositoryTest {

    private lateinit var fakeDao: FakeAgriDao
    private lateinit var repository: AgriRepository

    @Before
    fun setUp() {
        fakeDao = FakeAgriDao()
        repository = AgriRepository(fakeDao)
    }

    @Test
    fun updateProfile_and_profileFlow_emitsUpdatedProfile() = runTest {
        assertNull(repository.profileFlow.first())

        val newProfile = FarmerProfile(
            id = 1,
            fullName = "Sarah Connor",
            totalOwnedAcres = 2000.0,
            cultivatedAcres = 1500.0,
            fallowAcres = 500.0,
            sector = "North Sector 9"
        )
        repository.updateProfile(newProfile)

        val emitted = repository.profileFlow.first()
        assertEquals("Sarah Connor", emitted?.fullName)
        assertEquals(2000.0, emitted?.totalOwnedAcres ?: 0.0, 0.001)
    }

    @Test
    fun updateRates_and_ratesFlow_emitsUpdatedRates() = runTest {
        assertNull(repository.ratesFlow.first())

        val newRates = ServiceRates(
            id = 1,
            sprayingRatePerAcre = 50.0,
            cropCuttingRatePerAcre = 110.0,
            globalTrendPercent = 6.5
        )
        repository.updateRates(newRates)

        val emitted = repository.ratesFlow.first()
        assertEquals(50.0, emitted?.sprayingRatePerAcre ?: 0.0, 0.001)
        assertEquals(110.0, emitted?.cropCuttingRatePerAcre ?: 0.0, 0.001)
        assertEquals(6.5, emitted?.globalTrendPercent ?: 0.0, 0.001)
    }

    @Test
    fun addRecord_and_deleteRecord_updatesAllRecordsFlow() = runTest {
        val initialRecords = repository.allRecordsFlow.first()
        assertTrue(initialRecords.isEmpty())

        val record1 = ActivityRecord(
            id = 1L,
            title = "Fertilizer Application",
            date = "Oct 10, 2024",
            location = "East Field",
            cost = 3200.0,
            status = RecordStatus.COMPLETED,
            category = RecordCategory.LAND,
            season = "2024"
        )
        val record2 = ActivityRecord(
            id = 2L,
            title = "Drone Spraying",
            date = "Oct 12, 2024",
            location = "West Field",
            cost = 4500.0,
            status = RecordStatus.INVOICED,
            category = RecordCategory.SPRAYING,
            season = "2024"
        )

        repository.addRecord(record1)
        repository.addRecord(record2)

        val recordsAfterAdd = repository.allRecordsFlow.first()
        assertEquals(2, recordsAfterAdd.size)

        repository.deleteRecord(record1)
        val recordsAfterDelete = repository.allRecordsFlow.first()
        assertEquals(1, recordsAfterDelete.size)
        assertEquals(2L, recordsAfterDelete[0].id)
    }

    @Test
    fun getRecordsBySeason_filtersCorrectly() = runTest {
        val record2024 = ActivityRecord(
            id = 1L,
            title = "Harvesting 2024",
            date = "Oct 01, 2024",
            location = "North Field",
            cost = 8000.0,
            status = RecordStatus.COMPLETED,
            category = RecordCategory.HARVESTING,
            season = "2024"
        )
        val record2023 = ActivityRecord(
            id = 2L,
            title = "Harvesting 2023",
            date = "Oct 01, 2023",
            location = "North Field",
            cost = 7500.0,
            status = RecordStatus.COMPLETED,
            category = RecordCategory.HARVESTING,
            season = "2023"
        )

        repository.addRecord(record2024)
        repository.addRecord(record2023)

        val records2024 = repository.getRecordsBySeason("2024").first()
        assertEquals(1, records2024.size)
        assertEquals("Harvesting 2024", records2024[0].title)

        val records2023 = repository.getRecordsBySeason("2023").first()
        assertEquals(1, records2023.size)
        assertEquals("Harvesting 2023", records2023[0].title)
    }

    @Test
    fun getRecordsByStatus_filtersCorrectly() = runTest {
        val completedRecord = ActivityRecord(
            id = 1L,
            title = "Maintenance Work",
            date = "Oct 01, 2024",
            location = "Barn A",
            cost = 1200.0,
            status = RecordStatus.COMPLETED,
            category = RecordCategory.MAINTENANCE
        )
        val invoicedRecord = ActivityRecord(
            id = 2L,
            title = "Seed Order",
            date = "Oct 02, 2024",
            location = "Vendor Supply",
            cost = 5000.0,
            status = RecordStatus.INVOICED,
            category = RecordCategory.SEEDS
        )

        repository.addRecord(completedRecord)
        repository.addRecord(invoicedRecord)

        val completed = repository.getRecordsByStatus(RecordStatus.COMPLETED).first()
        assertEquals(1, completed.size)
        assertEquals("Maintenance Work", completed[0].title)

        val invoiced = repository.getRecordsByStatus(RecordStatus.INVOICED).first()
        assertEquals(1, invoiced.size)
        assertEquals("Seed Order", invoiced[0].title)
    }
}
