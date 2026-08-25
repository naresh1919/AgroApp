package com.agri.costtracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import com.agri.costtracker.ui.viewmodel.AgriViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHistoryScreen(
    viewModel: AgriViewModel,
    strings: AppStrings,
    onOpenAddBooking: () -> Unit
) {
    val context = LocalContext.current
    val records by viewModel.filteredRecords.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val filterFarmerId by viewModel.filterFarmerId.collectAsState()
    val farmers by viewModel.allFarmers.collectAsState()
    val season by viewModel.selectedSeason.collectAsState()

    var showFarmerFilterMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = strings.serviceHistory.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.serviceHistory,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            color = OnSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SecondaryFixed)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "SEASON: $season",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSecondaryFixed
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${records.size} RECORDS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Bento Summary Cards (Total Revenue & Pending Receivables)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Revenue Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Primary, PrimaryContainer)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = strings.totalBilled,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.5.sp,
                                    color = OnPrimary.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "₹${String.format(Locale.US, "%,.2f", metrics.totalRevenue)}",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = OnPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${String.format(Locale.US, "%.1f", metrics.totalAcresServed)} ${strings.acres} Serviced",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryFixed
                                )
                            )
                        }
                    }
                }

                // Pending Collections Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SecondaryFixedDim)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.pendingDue,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.5.sp,
                                    color = OnSecondaryFixed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${String.format(Locale.US, "%,.2f", metrics.pendingPayables)}",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = OnSecondaryFixed
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Pending",
                            tint = OnSecondaryFixed,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // Filter and Add Controls
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.bookingsFeed,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )

                    FilledTonalButton(
                        onClick = onOpenAddBooking,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimaryContainer.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.newBooking,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                    }
                }

                // Filter Row (Status & Farmer filter)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Filter Button
                    OutlinedButton(
                        onClick = {
                            val nextFilter = when (filterStatus) {
                                null -> RecordStatus.INVOICED
                                RecordStatus.INVOICED -> RecordStatus.PARTIAL
                                RecordStatus.PARTIAL -> RecordStatus.COMPLETED
                                RecordStatus.COMPLETED -> RecordStatus.ARCHIVED
                                RecordStatus.ARCHIVED -> null
                            }
                            viewModel.setFilterStatus(nextFilter)
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "Status", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (filterStatus) {
                                RecordStatus.INVOICED -> strings.unpaid
                                RecordStatus.PARTIAL -> strings.partial
                                RecordStatus.COMPLETED -> strings.paid
                                RecordStatus.ARCHIVED -> "ARCHIVED"
                                null -> strings.allStatus
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Farmer Filter Menu
                    Box {
                        val activeFarmer = farmers.find { it.id == filterFarmerId }
                        OutlinedButton(
                            onClick = { showFarmerFilterMenu = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Farmer", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = activeFarmer?.name ?: strings.allFarmers,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showFarmerFilterMenu,
                            onDismissRequest = { showFarmerFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(strings.allFarmers, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.setFilterFarmerId(null)
                                    showFarmerFilterMenu = false
                                }
                            )
                            HorizontalDivider()
                            farmers.forEach { farmer ->
                                DropdownMenuItem(
                                    text = { Text(farmer.name) },
                                    onClick = {
                                        viewModel.setFilterFarmerId(farmer.id)
                                        showFarmerFilterMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // List of Activity Items
        if (records.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = "Empty", tint = Outline, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(strings.noBookingsFound, color = OnSurfaceVariant)
                    }
                }
            }
        } else {
            items(records, key = { it.id }) { record ->
                ActivityRecordItem(
                    record = record,
                    strings = strings,
                    onDelete = {
                        viewModel.deleteRecord(record)
                        Toast.makeText(context, "${record.title} ${strings.delete}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun ActivityRecordItem(
    record: ActivityRecord,
    strings: AppStrings,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Category Icon
                val (icon, iconTint, iconBg) = getCategoryStyling(record.category)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = record.category.name,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = record.farmerName.ifEmpty { "Farmer" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    )

                    Text(
                        text = "${record.title} • ${record.acres} ${strings.acres} @ ₹${String.format(Locale.US, "%.2f", record.ratePerAcre)}/${strings.acres}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusBadge(record.status, strings)
                        Text(
                            text = "${record.date}${if (record.paidAmount > 0 && record.lastPaymentDate.isNotBlank()) " • Paid: ${record.lastPaymentDate}" else ""} • ${record.location}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Outline,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${String.format(Locale.US, "%,.2f", record.cost)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = OnSurface
                    )
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Outline.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: RecordStatus, strings: AppStrings) {
    val (bg, text, label) = when (status) {
        RecordStatus.COMPLETED -> Triple(GreenCardBadgeBg, GreenCardBadgeText, strings.paid)
        RecordStatus.PARTIAL -> Triple(OrangeCardBadgeBg, OrangeCardBadgeText, strings.partial)
        RecordStatus.INVOICED -> Triple(OrangeCardBadgeBg, OrangeCardBadgeText, strings.unpaid)
        RecordStatus.ARCHIVED -> Triple(GrayCardBadgeBg, GrayCardBadgeText, "ARCHIVED")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = text
            )
        )
    }
}

private fun getCategoryStyling(category: RecordCategory): Triple<ImageVector, Color, Color> {
    return when (category) {
        RecordCategory.SPRAYING -> Triple(Icons.Default.WaterDrop, Primary, PrimaryFixedDim.copy(alpha = 0.3f))
        RecordCategory.HARVESTING -> Triple(Icons.Default.Agriculture, Secondary, SecondaryFixedDim)
        RecordCategory.LAND -> Triple(Icons.Default.Landscape, Primary, SurfaceContainerLow)
        RecordCategory.SEEDS -> Triple(Icons.Default.Eco, Tertiary, TertiaryContainer.copy(alpha = 0.2f))
        RecordCategory.MAINTENANCE -> Triple(Icons.Default.PrecisionManufacturing, Secondary, SecondaryContainer)
        RecordCategory.IRRIGATION -> Triple(Icons.Default.WaterDrop, Primary, PrimaryContainer.copy(alpha = 0.15f))
        RecordCategory.OTHER -> Triple(Icons.Default.Payments, Outline, SurfaceContainerLow)
    }
}
