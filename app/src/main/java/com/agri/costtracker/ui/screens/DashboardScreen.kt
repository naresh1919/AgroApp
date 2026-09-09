package com.agri.costtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.ui.components.AgriScreen
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import com.agri.costtracker.ui.viewmodel.AgriViewModel
import com.agri.costtracker.ui.viewmodel.MonthlyExpenditure
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: AgriViewModel,
    strings: AppStrings,
    onNavigate: (AgriScreen) -> Unit,
    onOpenAddBooking: () -> Unit,
    onOpenAddFarmer: () -> Unit,
    onGenerateFiscalReport: () -> Unit
) {
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val allRecords by viewModel.allRecords.collectAsState()
    val season by viewModel.selectedSeason.collectAsState()
    val scrollState = rememberScrollState()
    var showSeasonDropdown by remember { mutableStateOf(false) }

    val decimalFormatter = remember {
        java.text.NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        // Editorial Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BUSINESS OPERATIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    color = Outline,
                    fontWeight = FontWeight.Bold
                )
            )

            // Season Selector Dropdown
            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showSeasonDropdown = true },
                    shape = RoundedCornerShape(20.dp),
                    color = SecondaryFixed
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "SEASON $season",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = OnSecondaryFixed
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Season",
                            tint = OnSecondaryFixed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSeasonDropdown,
                    onDismissRequest = { showSeasonDropdown = false }
                ) {
                    listOf("2023", "2024", "2025", "2026").forEach { yr ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Season $yr",
                                    fontWeight = if (yr == season) FontWeight.Black else FontWeight.Normal,
                                    color = if (yr == season) Primary else OnSurface
                                )
                            },
                            onClick = {
                                viewModel.setSeason(yr)
                                showSeasonDropdown = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${strings.navDashboard} ",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = OnSurface
                )
            )
            Text(
                text = "Overview",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontSize = 28.sp,
                    color = Primary
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = strings.dashboardSubtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = OnSurfaceVariant,
                lineHeight = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Action Buttons (Add Service Booking & Register Farmer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenAddBooking,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Booking", tint = OnPrimary, modifier = Modifier.size(18.dp))
                    Text(
                        text = strings.newBooking,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = OnPrimary)
                    )
                }
            }

            OutlinedButton(
                onClick = onOpenAddFarmer,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Primary, PrimaryContainer)))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Add Farmer", tint = Primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = strings.registerNewFarmer.replace("+ ", ""),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Primary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Total Land Serviced Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = strings.totalServicedLand,
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        color = Outline,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = decimalFormatter.format(metrics.totalAcresServed),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Primary
                        )
                    )
                    Text(
                        text = strings.acres.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown: Spraying vs Cutting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "🚁 ${strings.droneSprayed}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Outline
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${decimalFormatter.format(metrics.totalSprayingAcres)} ${strings.acres}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = OnSurface
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "🚜 ${strings.harvestedCut}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Outline
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${decimalFormatter.format(metrics.totalCuttingAcres)} ${strings.acres}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = OnSurface
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Financial Overview Cards (Total Revenue & Pending Receivables)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Billed Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Primary, PrimaryContainer))
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = strings.totalBilled,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnPrimary.copy(alpha = 0.85f)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹${String.format(Locale.US, "%,.0f", metrics.totalRevenue)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = OnPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${metrics.recordsCount} ${strings.recentDeployments}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = OnPrimary.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            // Pending Collections Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SecondaryFixedDim)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = strings.pendingDue,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSecondaryFixed
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹${String.format(Locale.US, "%,.0f", metrics.pendingPayables)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = OnSecondaryFixed
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.unpaidOnly,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = OnSecondaryFixedVariant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seasonal Expenditure Flow Bar Chart
        SeasonalExpenditureFlowChart(monthlyBreakdown = metrics.monthlyBreakdown)

        Spacer(modifier = Modifier.height(16.dp))

        // Service Rates Snapshot Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Drone Spraying Rate Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(AgriScreen.RATES) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.droneRate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline
                            )
                        )
                        Icon(Icons.Default.WaterDrop, contentDescription = "Drone", tint = Primary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${String.format(Locale.US, "%.2f", metrics.sprayingRate)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Primary
                        )
                    )
                    Text(
                        text = "/ ${strings.acres}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = OnSurfaceVariant)
                    )
                }
            }

            // Cutting Machine Rate Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(AgriScreen.RATES) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.cuttingRate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline
                            )
                        )
                        Icon(Icons.Default.Agriculture, contentDescription = "Harvester", tint = Secondary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${String.format(Locale.US, "%.2f", metrics.cropCuttingRate)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Secondary
                        )
                    )
                    Text(
                        text = "/ ${strings.acres}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = OnSurfaceVariant)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Bookings Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.recentDeployments,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            )

            TextButton(onClick = { onNavigate(AgriScreen.HISTORY) }) {
                Text(
                    text = "${strings.viewAll} (${allRecords.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Recent Bookings List
        val recentRecords = allRecords.take(4)
        if (recentRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(strings.noBookingsFound, color = Outline)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                recentRecords.forEach { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                val isDrone = record.category == RecordCategory.SPRAYING
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDrone) PrimaryContainer.copy(alpha = 0.2f) else SecondaryContainer.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (isDrone) "🚁" else "🚜", fontSize = 18.sp)
                                }

                                Column {
                                    Text(
                                        text = record.farmerName.ifEmpty { "Farmer" },
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${record.title} • ${record.acres} ${strings.acres}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${String.format(Locale.US, "%,.0f", record.cost)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Primary
                                    )
                                )
                                val (statusText, statusColor) = when (record.status) {
                                    RecordStatus.COMPLETED -> Pair(strings.paid, Primary)
                                    RecordStatus.PARTIAL -> Pair("${strings.partial} (₹${String.format(Locale.US, "%,.0f", record.paidAmount)})", Secondary)
                                    else -> Pair(strings.unpaid, Secondary)
                                }
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dual Bottom Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onGenerateFiscalReport,
                modifier = Modifier
                    .weight(1.3f)
                    .height(56.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Primary, PrimaryContainer)))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.generateFiscalReport,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnPrimary
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Report",
                            tint = OnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { onNavigate(AgriScreen.RATES) },
                modifier = Modifier
                    .weight(0.9f)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rates",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.navRates,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SeasonalExpenditureFlowChart(
    monthlyBreakdown: List<MonthlyExpenditure>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Seasonal Expenditure Flow",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "MONTHLY ACTIVITY & HARVEST DISPATCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Outline,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                        Text("Spraying", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Secondary)
                        )
                        Text("Cutting", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val defaultMonths = listOf("MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
            val maxCost = monthlyBreakdown.maxOfOrNull { it.totalCost }?.coerceAtLeast(1.0) ?: 1.0
            val hasData = monthlyBreakdown.any { it.totalCost > 0 }
            val sampleFractions = listOf(0.40f, 0.65f, 0.45f, 0.90f, 0.30f, 0.75f, 0.55f, 0.85f, 0.95f, 0.40f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                defaultMonths.forEachIndexed { index, month ->
                    val data = monthlyBreakdown.find { it.monthLabel == month }
                    val fraction = if (hasData) {
                        val cost = data?.totalCost ?: 0.0
                        if (cost > 0) (cost / maxCost).toFloat().coerceIn(0.18f, 1f) else 0.08f
                    } else {
                        sampleFractions[index % sampleFractions.size]
                    }

                    val isSprayingHeavier = if (hasData && data != null && data.totalCost > 0) {
                        data.sprayingCost >= data.harvestingCost
                    } else {
                        index % 2 == 0 || index == 3 || index == 7 || index == 8
                    }

                    val barColor = if (isSprayingHeavier) Primary else Secondary
                    val secondaryColor = if (isSprayingHeavier) PrimaryContainer.copy(alpha = 0.8f) else SecondaryContainer

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(secondaryColor, barColor)
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Month labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                defaultMonths.forEach { month ->
                    Text(
                        text = month,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
