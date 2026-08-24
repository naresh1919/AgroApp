package com.agri.costtracker.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelTest {

    @Test
    fun activityRecord_defaultValues_areCorrect() {
        val record = ActivityRecord(
            title = "Irrigation Setup",
            date = "October 20, 2024",
            location = "East Field",
            cost = 1500.0,
            status = RecordStatus.COMPLETED,
            category = RecordCategory.IRRIGATION
        )

        assertEquals(0L, record.id)
        assertEquals("Irrigation Setup", record.title)
        assertEquals("October 20, 2024", record.date)
        assertEquals("East Field", record.location)
        assertEquals(1500.0, record.cost, 0.001)
        assertEquals(RecordStatus.COMPLETED, record.status)
        assertEquals(RecordCategory.IRRIGATION, record.category)
        assertEquals("2024", record.season)
        assertTrue(record.timestamp > 0)
    }

    @Test
    fun activityRecord_copy_updatesFieldsCorrectly() {
        val original = ActivityRecord(
            id = 10L,
            title = "Plowing",
            date = "Sept 01, 2024",
            location = "North Sector",
            cost = 2200.0,
            status = RecordStatus.INVOICED,
            category = RecordCategory.LAND,
            season = "2024",
            timestamp = 1000L
        )

        val updated = original.copy(
            cost = 2500.0,
            status = RecordStatus.COMPLETED
        )

        assertEquals(10L, updated.id)
        assertEquals("Plowing", updated.title)
        assertEquals(2500.0, updated.cost, 0.001)
        assertEquals(RecordStatus.COMPLETED, updated.status)
        assertEquals(RecordCategory.LAND, updated.category)
    }

    @Test
    fun recordStatus_enum_containsAllValues() {
        val values = RecordStatus.values()
        assertEquals(3, values.size)
        assertTrue(values.contains(RecordStatus.COMPLETED))
        assertTrue(values.contains(RecordStatus.INVOICED))
        assertTrue(values.contains(RecordStatus.ARCHIVED))
    }

    @Test
    fun recordCategory_enum_containsAllValues() {
        val values = RecordCategory.values()
        assertEquals(7, values.size)
        assertTrue(values.contains(RecordCategory.LAND))
        assertTrue(values.contains(RecordCategory.SEEDS))
        assertTrue(values.contains(RecordCategory.MAINTENANCE))
        assertTrue(values.contains(RecordCategory.IRRIGATION))
        assertTrue(values.contains(RecordCategory.SPRAYING))
        assertTrue(values.contains(RecordCategory.HARVESTING))
        assertTrue(values.contains(RecordCategory.OTHER))
    }

    @Test
    fun farmerProfile_defaultValues_areCorrect() {
        val profile = FarmerProfile()

        assertEquals(1, profile.id)
        assertEquals("Elias Thorne", profile.fullName)
        assertEquals(1240.0, profile.totalOwnedAcres, 0.001)
        assertEquals(842.0, profile.cultivatedAcres, 0.001)
        assertEquals(398.0, profile.fallowAcres, 0.001)
        assertEquals("Central Valley Sector 7", profile.sector)
        assertEquals("Central Plains", profile.region)
        assertEquals("", profile.avatarUrl)
    }

    @Test
    fun farmerProfile_copy_updatesFields() {
        val profile = FarmerProfile()
        val customProfile = profile.copy(
            fullName = "John Doe",
            totalOwnedAcres = 500.0,
            cultivatedAcres = 350.0,
            fallowAcres = 150.0,
            sector = "South Sector 3"
        )

        assertEquals("John Doe", customProfile.fullName)
        assertEquals(500.0, customProfile.totalOwnedAcres, 0.001)
        assertEquals(350.0, customProfile.cultivatedAcres, 0.001)
        assertEquals(150.0, customProfile.fallowAcres, 0.001)
        assertEquals("South Sector 3", customProfile.sector)
    }

    @Test
    fun serviceRates_defaultValues_areCorrect() {
        val rates = ServiceRates()

        assertEquals(1, rates.id)
        assertEquals(34.0, rates.sprayingRatePerAcre, 0.001)
        assertEquals(85.0, rates.cropCuttingRatePerAcre, 0.001)
        assertEquals(4.2, rates.globalTrendPercent, 0.001)
        assertTrue(rates.lastUpdated > 0)
    }

    @Test
    fun serviceRates_copy_updatesRates() {
        val rates = ServiceRates()
        val customRates = rates.copy(
            sprayingRatePerAcre = 40.0,
            cropCuttingRatePerAcre = 95.0,
            globalTrendPercent = 5.0
        )

        assertEquals(40.0, customRates.sprayingRatePerAcre, 0.001)
        assertEquals(95.0, customRates.cropCuttingRatePerAcre, 0.001)
        assertEquals(5.0, customRates.globalTrendPercent, 0.001)
    }
}
