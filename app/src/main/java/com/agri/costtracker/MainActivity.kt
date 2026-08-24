package com.agri.costtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.agri.costtracker.ui.components.AgriBottomNavBar
import com.agri.costtracker.ui.components.AgriScreen
import com.agri.costtracker.ui.components.AgriTopAppBar
import com.agri.costtracker.ui.screens.*
import com.agri.costtracker.ui.theme.AgriCostTrackerTheme
import com.agri.costtracker.ui.theme.Surface
import com.agri.costtracker.ui.viewmodel.AgriViewModel
import com.agri.costtracker.ui.viewmodel.AgriViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: AgriViewModel by viewModels {
        val app = application as AgriApplication
        AgriViewModelFactory(app.repository)
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
    var currentScreen by remember { mutableStateOf(AgriScreen.DASHBOARD) }
    var showFiscalReport by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val profile by viewModel.profile.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()

    if (showFiscalReport) {
        FiscalReportDialog(
            metrics = metrics,
            farmerName = profile.fullName,
            onDismiss = { showFiscalReport = false }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("App Configuration") },
            text = {
                Text("The Modern Agronomist v1.0.0\nOperating Region: ${profile.region}\nSector: ${profile.sector}\nAudit Logging: Enabled")
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
                farmerName = profile.fullName,
                onSettingsClick = { showSettingsDialog = true }
            )
        },
        bottomBar = {
            AgriBottomNavBar(
                currentScreen = currentScreen,
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
                AgriScreen.LAND -> FarmerProfileScreen(viewModel = viewModel)
                AgriScreen.RATES -> ServiceRatesScreen(viewModel = viewModel)
                AgriScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> currentScreen = screen },
                    onGenerateFiscalReport = { showFiscalReport = true }
                )
                AgriScreen.HISTORY -> ActivityHistoryScreen(viewModel = viewModel)
            }
        }
    }
}
