package com.agri.costtracker

import android.app.Application
import com.agri.costtracker.data.local.AgriDatabase
import com.agri.costtracker.data.repository.AgriRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AgriApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AgriDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { AgriRepository(database.agriDao()) }
}
