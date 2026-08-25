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
    fun seedInitialData_populatesFarmersRatesAndRecords() = runTest {
        AgriDatabase.seedInitialData(fakeDao)

        // Verify seeded farmers
        val farmers = fakeDao.getAllFarmers().first()
        assertTrue(farmers.isNotEmpty())
        val ramesh = farmers.find { it.name == "Ramesh Patel" }
        assertNotNull(ramesh)
        assertEquals("9876543210", ramesh?.mobile)

        // Verify seeded rates in INR
        val rates = fakeDao.getServiceRates().first()
        assertNotNull(rates)
        assertEquals(450.0, rates?.sprayingRatePerAcre ?: 0.0, 0.001)
        assertEquals(1400.0, rates?.cropCuttingRatePerAcre ?: 0.0, 0.001)
        assertEquals(4.2, rates?.globalTrendPercent ?: 0.0, 0.001)

        // Verify seeded records
        val records = fakeDao.getAllRecords().first()
        assertTrue(records.isNotEmpty())
        val droneRecord = records.find { it.title.contains("Drone Spraying") }
        assertNotNull(droneRecord)
        assertEquals(11250.00, droneRecord?.cost ?: 0.0, 0.001)
        assertEquals(11250.00, droneRecord?.paidAmount ?: 0.0, 0.001)

        // Verify seeded payment installments
        val payments = fakeDao.getAllPayments().first()
        assertTrue(payments.isNotEmpty())
        val rameshPayments = payments.filter { it.farmerName == "Ramesh Patel" }
        assertEquals(3, rameshPayments.size) // 1 full payment + 2 installments for cutting
    }
}
