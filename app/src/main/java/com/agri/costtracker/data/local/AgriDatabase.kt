package com.agri.costtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.FarmerProfile
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.data.model.ServiceRates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [FarmerProfile::class, ServiceRates::class, ActivityRecord::class],
    version = 1,
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
            // Initial Profile
            dao.insertOrUpdateProfile(
                FarmerProfile(
                    id = 1,
                    fullName = "Elias Thorne",
                    totalOwnedAcres = 1240.0,
                    cultivatedAcres = 842.0,
                    fallowAcres = 398.0,
                    sector = "Central Valley Sector 7",
                    region = "Central Plains"
                )
            )

            // Initial Rates
            dao.insertOrUpdateRates(
                ServiceRates(
                    id = 1,
                    sprayingRatePerAcre = 34.00,
                    cropCuttingRatePerAcre = 85.00,
                    globalTrendPercent = 4.2
                )
            )

            // Initial Activity Records matching design
            val records = listOf(
                ActivityRecord(
                    title = "Soil Analysis & Fertilization",
                    date = "March 14, 2024",
                    location = "North Acre Ridge Field",
                    cost = 12450.00,
                    status = RecordStatus.COMPLETED,
                    category = RecordCategory.LAND,
                    season = "2024",
                    timestamp = 1710374400000L
                ),
                ActivityRecord(
                    title = "Seed Procurement - Maize",
                    date = "March 08, 2024",
                    location = "Seasonal Supply Order",
                    cost = 45200.00,
                    status = RecordStatus.INVOICED,
                    category = RecordCategory.SEEDS,
                    season = "2024",
                    timestamp = 1709856000000L
                ),
                ActivityRecord(
                    title = "Equipment Maintenance",
                    date = "February 22, 2024",
                    location = "Fleet Service Center",
                    cost = 2180.50,
                    status = RecordStatus.ARCHIVED,
                    category = RecordCategory.MAINTENANCE,
                    season = "2024",
                    timestamp = 1708560000000L
                ),
                ActivityRecord(
                    title = "Irrigation System Upgrade",
                    date = "February 15, 2024",
                    location = "South Reservoir Link",
                    cost = 18900.00,
                    status = RecordStatus.COMPLETED,
                    category = RecordCategory.IRRIGATION,
                    season = "2024",
                    timestamp = 1707955200000L
                ),
                ActivityRecord(
                    title = "Selective Herbicide Application",
                    date = "January 28, 2024",
                    location = "East Delta Sector 3",
                    cost = 4210.00,
                    status = RecordStatus.INVOICED,
                    category = RecordCategory.SPRAYING,
                    season = "2024",
                    timestamp = 1706400000000L
                ),
                ActivityRecord(
                    title = "Deep Tillage & Furrowing",
                    date = "January 10, 2024",
                    location = "West Boundary Fields",
                    cost = 59909.50,
                    status = RecordStatus.COMPLETED,
                    category = RecordCategory.LAND,
                    season = "2024",
                    timestamp = 1704844800000L
                )
            )
            dao.insertAllRecords(records)
        }
    }
}
