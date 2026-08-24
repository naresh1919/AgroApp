package com.agri.costtracker.data.local

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AgriDatabaseSeedingTest {

    private lateinit var fakeDao: FakeAgriDao

    @Before
    fun setUp() {
        fakeDao = FakeAgriDao()
    }

    @Test
    fun seedInitialData_populatesProfileRatesAndRecords() = runTest {
        AgriDatabase.seedInitialData(fakeDao)

        // Verify seeded profile
        val profile = fakeDao.getFarmerProfile().first()
        assertNotNull(profile)
        assertEquals("Elias Thorne", profile?.fullName)
        assertEquals(1240.0, profile?.totalOwnedAcres ?: 0.0, 0.001)
        assertEquals(842.0, profile?.cultivatedAcres ?: 0.0, 0.001)
        assertEquals(398.0, profile?.fallowAcres ?: 0.0, 0.001)
        assertEquals("Central Valley Sector 7", profile?.sector)

        // Verify seeded rates
        val rates = fakeDao.getServiceRates().first()
        assertNotNull(rates)
        assertEquals(34.0, rates?.sprayingRatePerAcre ?: 0.0, 0.001)
        assertEquals(85.0, rates?.cropCuttingRatePerAcre ?: 0.0, 0.001)
        assertEquals(4.2, rates?.globalTrendPercent ?: 0.0, 0.001)

        // Verify seeded records
        val records = fakeDao.getAllRecords().first()
        assertTrue(records.isNotEmpty())
        val soilRecord = records.find { it.title == "Soil Analysis & Fertilization" }
        assertNotNull(soilRecord)
        assertEquals(12450.00, soilRecord?.cost ?: 0.0, 0.001)
    }
}
