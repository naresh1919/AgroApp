package com.agri.costtracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.agri.costtracker.ui.components.AgriBottomNavBar
import com.agri.costtracker.ui.components.AgriScreen
import com.agri.costtracker.ui.components.AgriTopAppBar
import com.agri.costtracker.ui.localization.getAppStrings
import com.agri.costtracker.ui.screens.*
import com.agri.costtracker.ui.theme.AgriCostTrackerTheme
import com.agri.costtracker.ui.theme.Surface
import com.agri.costtracker.ui.viewmodel.AgriViewModel
import com.agri.costtracker.ui.viewmodel.AgriViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: AgriViewModel by viewModels {
        val app = application as AgriApplication
        AgriViewModelFactory(app.repository, app.syncManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriCostTrackerTheme {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: AgriViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AgriScreen.DASHBOARD) }
    var showFiscalReport by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAddFarmerDialog by remember { mutableStateOf(false) }
    var showAddBookingDialog by remember { mutableStateOf(false) }
    var showCloudBackupDialog by remember { mutableStateOf(false) }

    val currentLanguage by viewModel.selectedLanguage.collectAsState()
    val strings = remember(currentLanguage) { getAppStrings(currentLanguage) }

    val profile by viewModel.profile.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val farmers by viewModel.allFarmers.collectAsState()
    val rates by viewModel.rates.collectAsState()

    // Add Farmer Dialog
    if (showAddFarmerDialog) {
        AddFarmerDialog(
            strings = strings,
            onDismiss = { showAddFarmerDialog = false },
            onAddFarmer = { name, mobile, village, totalAcres, notes ->
                viewModel.addFarmer(name, mobile, village, totalAcres, notes)
                Toast.makeText(context, "${strings.registerNewFarmer.replace("+ ", "")}: \"$name\"", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Add Service Booking Dialog
    if (showAddBookingDialog) {
        AddActivityDialog(
            farmers = farmers,
            rates = rates,
            strings = strings,
            onDismiss = { showAddBookingDialog = false },
            onOpenAddFarmer = {
                showAddBookingDialog = false
                showAddFarmerDialog = true
            },
            onAddRecord = { farmerId, farmerName, farmerMobile, title, category, acres, ratePerAcre, paidAmount, location, date, notes ->
                viewModel.addServiceRecord(
                    farmerId = farmerId,
                    farmerName = farmerName,
                    farmerMobile = farmerMobile,
                    title = title,
                    category = category,
                    acres = acres,
                    ratePerAcre = ratePerAcre,
                    paidAmount = paidAmount,
                    location = location,
                    date = date,
                    notes = notes
                )
                Toast.makeText(context, "${strings.saveBooking}: $farmerName", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Fiscal Report Dialog
    if (showFiscalReport) {
        FiscalReportDialog(
            metrics = metrics,
            businessName = profile.fullName,
            strings = strings,
            onDismiss = { showFiscalReport = false }
        )
    }

    // Firebase Cloud Sync Dialog
    if (showCloudBackupDialog) {
        com.agri.costtracker.ui.screens.CloudBackupDialog(
            viewModel = viewModel,
            strings = strings,
            onDismiss = { showCloudBackupDialog = false }
        )
    }

    // Settings / App Info Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Agri Rental System v2.0") },
            text = {
                Text("Enterprise: ${profile.fullName}\nOperating Hub: ${profile.sector}\nRegion: ${profile.region}\nRegistered Clients: ${farmers.size} Farmers\nService Modes: Drone Spraying & Harvesters\nSelected Language: ${currentLanguage.nativeName} (${currentLanguage.displayName})")
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
        topBar = {
            AgriTopAppBar(
                strings = strings,
                currentLanguage = currentLanguage,
                onLanguageSelected = { lang ->
                    viewModel.setLanguage(lang)
                },
                onAddFarmerClick = { showAddFarmerDialog = true },
                onCloudSyncClick = { showCloudBackupDialog = true },
                onSettingsClick = { showSettingsDialog = true }
            )
        },
        bottomBar = {
            AgriBottomNavBar(
                currentScreen = currentScreen,
                strings = strings,
                onScreenSelected = { screen ->
                    currentScreen = screen
                }
            )
        },
        containerColor = Surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AgriScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onNavigate = { screen -> currentScreen = screen },
                    onOpenAddBooking = { showAddBookingDialog = true },
                    onOpenAddFarmer = { showAddFarmerDialog = true },
                    onGenerateFiscalReport = { showFiscalReport = true }
                )
                AgriScreen.FARMERS -> FarmersDirectoryScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onOpenAddFarmer = { showAddFarmerDialog = true }
                )
                AgriScreen.RATES -> ServiceRatesScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                AgriScreen.HISTORY -> ActivityHistoryScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onOpenAddBooking = { showAddBookingDialog = true }
                )
            }
        }
    }
}
