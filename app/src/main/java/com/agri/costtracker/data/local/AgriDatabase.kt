package com.agri.costtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Farmer::class, FarmerProfile::class, ServiceRates::class, ActivityRecord::class],
    version = 2,
    exportSchema = false
)
abstract class AgriDatabase : RoomDatabase() {

    abstract fun agriDao(): AgriDao

    companion object {
        @Volatile
        private var INSTANCE: AgriDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AgriDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriDatabase::class.java,
                    "agri_cost_tracker_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedInitialData(database.agriDao())
                    }
                }
            }
        }

        suspend fun seedInitialData(dao: AgriDao) {
            // Initial Business / Operator Profile
            dao.insertOrUpdateProfile(
                FarmerProfile(
                    id = 1,
                    fullName = "AgriTech Drone & Machinery Services",
                    totalOwnedAcres = 3500.0,
                    cultivatedAcres = 2800.0,
                    fallowAcres = 700.0,
                    sector = "District Central Agro Hub",
                    region = "Central Plains"
                )
            )

            // Initial Service Rates (Default Per-Acre charges)
            dao.insertOrUpdateRates(
                ServiceRates(
                    id = 1,
                    sprayingRatePerAcre = 35.00, // Drone Spraying default rate
                    cropCuttingRatePerAcre = 85.00, // Harvester/Cutting Machine default rate
                    globalTrendPercent = 4.2
                )
            )

            // Seed Initial Registered Farmers
            val farmers = listOf(
                Farmer(
                    id = 1,
                    name = "Ramesh Patel",
                    mobile = "9876543210",
                    village = "Green Valley, North Sector",
                    totalAcres = 45.0,
                    notes = "Regular customer for Drone spraying (Paddy & Cotton)"
                ),
                Farmer(
                    id = 2,
                    name = "Suresh Kumar",
                    mobile = "9812345678",
                    village = "East Ridge Village",
                    totalAcres = 80.0,
                    notes = "Requires Harvester and Drone Spraying"
                ),
                Farmer(
                    id = 3,
                    name = "Rajesh Singh",
                    mobile = "9765432109",
                    village = "Kisan Nagar, Sector 4",
                    totalAcres = 120.0,
                    notes = "Large farm - Maize and Wheat cultivation"
                ),
                Farmer(
                    id = 4,
                    name = "Vikram Sharma",
                    mobile = "9898765432",
                    village = "West Delta Field Zone",
                    totalAcres = 35.0,
                    notes = "Prefers automated Drone fertilizer application"
                )
            )
            dao.insertAllFarmers(farmers)

            // Initial Activity Records matching the rental business
            val records = listOf(
                ActivityRecord(
                    id = 1,
                    farmerId = 1,
                    farmerName = "Ramesh Patel",
                    farmerMobile = "9876543210",
                    title = "Drone Spraying - Pesticide Application",
                    date = "October 20, 2024",
                    location = "Green Valley Field A",
                    acres = 25.0,
                    ratePerAcre = 35.00,
                    cost = 875.00, // 25 * 35
                    status = RecordStatus.COMPLETED,
                    category = RecordCategory.SPRAYING,
                    season = "2024",
                    notes = "Full spray completed on Cotton field",
                    timestamp = 1729425600000L
                ),
                ActivityRecord(
                    id = 2,
                    farmerId = 2,
                    farmerName = "Suresh Kumar",
                    farmerMobile = "9812345678",
                    title = "Cutting Machine - Wheat Harvesting",
                    date = "October 18, 2024",
                    location = "East Ridge Plot 3",
                    acres = 40.0,
                    ratePerAcre = 85.00,
                    cost = 3400.00, // 40 * 85
                    status = RecordStatus.INVOICED,
                    category = RecordCategory.HARVESTING,
                    season = "2024",
                    notes = "Harvester deployed for 2 days",
                    timestamp = 1729252800000L
                ),
                ActivityRecord(
                    id = 3,
                    farmerId = 3,
                    farmerName = "Rajesh Singh",
                    farmerMobile = "9765432109",
                    title = "Drone Fertilizer Spraying",
                    date = "October 15, 2024",
                    location = "Kisan Nagar Sector 4",
                    acres = 60.0,
                    ratePerAcre = 32.00, // custom discount rate
                    cost = 1920.00,
                    status = RecordStatus.COMPLETED,
                    category = RecordCategory.SPRAYING,
                    season = "2024",
                    notes = "Volume discount applied",
                    timestamp = 1728993600000L
                ),
                ActivityRecord(
                    id = 4,
                    farmerId = 1,
                    farmerName = "Ramesh Patel",
                    farmerMobile = "9876543210",
                    title = "Cutting Machine - Paddy Harvesting",
                    date = "October 10, 2024",
                    location = "Green Valley Field B",
                    acres = 20.0,
                    ratePerAcre = 85.00,
                    cost = 1700.00,
                    status = RecordStatus.INVOICED,
                    category = RecordCategory.HARVESTING,
                    season = "2024",
                    notes = "Payment pending via UPI / Cash",
                    timestamp = 1728561600000L
                ),
                ActivityRecord(
                    id = 5,
                    farmerId = 4,
                    farmerName = "Vikram Sharma",
                    farmerMobile = "9898765432",
                    title = "Drone Spraying - Herbicide",
                    date = "October 05, 2024",
                    location = "West Delta Sector 2",
                    acres = 35.0,
                    ratePerAcre = 35.00,
                    cost = 1225.00,
                    status = RecordStatus.COMPLETED,
                    category = RecordCategory.SPRAYING,
                    season = "2024",
                    notes = "Completed in morning shift",
                    timestamp = 1728129600000L
                )
            )
            dao.insertAllRecords(records)
        }
    }
}
