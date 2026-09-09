package com.agri.costtracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
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
    var searchQuery by remember { mutableStateOf("") }
    var recordForPayment by remember { mutableStateOf<ActivityRecord?>(null) }
    var paymentAmountInput by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var paymentNotes by remember { mutableStateOf("") }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var recordToDelete by remember { mutableStateOf<ActivityRecord?>(null) }

    val displayedRecords = remember(records, searchQuery) {
        if (searchQuery.isBlank()) records
        else {
            records.filter {
                it.farmerName.contains(searchQuery, ignoreCase = true) ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true) ||
                it.date.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Quick Payment Settlement Dialog
    if (recordForPayment != null) {
        val activeRec = recordForPayment!!
        val remaining = (activeRec.cost - activeRec.paidAmount).coerceAtLeast(0.0)

        Dialog(onDismissRequest = { recordForPayment = null }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceContainerLowest,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.recordPayment,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${activeRec.farmerName} • ${activeRec.title}",
                                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                            )
                        }
                        IconButton(onClick = { recordForPayment = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.pendingBalanceDue, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Outline))
                            Text("₹${String.format(Locale.US, "%,.2f", remaining)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Secondary))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = paymentAmountInput,
                        onValueChange = {
                            paymentAmountInput = it
                            paymentError = null
                        },
                        label = { Text(strings.enterPaymentAmount) },
                        placeholder = { Text("e.g. 5000.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (paymentError != null) {
                        Text(
                            text = paymentError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("CASH", "UPI", "BANK").forEach { mode ->
                            FilterChip(
                                selected = paymentMode == mode,
                                onClick = { paymentMode = mode },
                                label = { Text(mode, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = paymentNotes,
                        onValueChange = { paymentNotes = it },
                        label = { Text(strings.cropsNotes) },
                        placeholder = { Text("e.g. Paid via PhonePe / cash in field") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val amt = paymentAmountInput.toDoubleOrNull() ?: 0.0
                            paymentError = when {
                                amt <= 0 -> "Enter a payment greater than zero"
                                amt > remaining -> "Payment cannot exceed the pending balance"
                                else -> null
                            }
                            if (paymentError == null) {
                                val today = SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date())
                                viewModel.recordPayment(activeRec, amt, today, paymentMode, paymentNotes)
                                Toast.makeText(context, "${strings.paymentRecordedSuccess}: ₹${String.format(Locale.US, "%,.2f", amt)}", Toast.LENGTH_SHORT).show()
                                recordForPayment = null
                                paymentAmountInput = ""
                                paymentNotes = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(strings.save, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    recordToDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("Delete booking?") },
            text = { Text("This will permanently remove ${record.title} and its payment history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecord(record)
                        Toast.makeText(context, "${record.title} ${strings.delete}", Toast.LENGTH_SHORT).show()
                        recordToDelete = null
                    }
                ) { Text(strings.delete, color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { recordToDelete = null }) { Text("Cancel") } }
        )
    }

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

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by farmer, title, location...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Farmer Filter Dropdown & Status Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Farmer Filter Menu
                    Box {
                        val activeFarmer = farmers.find { it.id == filterFarmerId }
                        OutlinedButton(
                            onClick = { showFarmerFilterMenu = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp)
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

                    if (filterFarmerId != null) {
                        TextButton(
                            onClick = { viewModel.setFilterFarmerId(null) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Clear", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Horizontal Status Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterStatus == null,
                        onClick = { viewModel.setFilterStatus(null) },
                        label = { Text(strings.allStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryContainer.copy(alpha = 0.25f), selectedLabelColor = Primary)
                    )
                    FilterChip(
                        selected = filterStatus == RecordStatus.INVOICED,
                        onClick = { viewModel.setFilterStatus(RecordStatus.INVOICED) },
                        label = { Text(strings.unpaid, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeCardBadgeBg, selectedLabelColor = OrangeCardBadgeText)
                    )
                    FilterChip(
                        selected = filterStatus == RecordStatus.PARTIAL,
                        onClick = { viewModel.setFilterStatus(RecordStatus.PARTIAL) },
                        label = { Text(strings.partial, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangeCardBadgeBg, selectedLabelColor = OrangeCardBadgeText)
                    )
                    FilterChip(
                        selected = filterStatus == RecordStatus.COMPLETED,
                        onClick = { viewModel.setFilterStatus(RecordStatus.COMPLETED) },
                        label = { Text(strings.paid, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GreenCardBadgeBg, selectedLabelColor = GreenCardBadgeText)
                    )
                    FilterChip(
                        selected = filterStatus == RecordStatus.ARCHIVED,
                        onClick = { viewModel.setFilterStatus(RecordStatus.ARCHIVED) },
                        label = { Text("ARCHIVED", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GrayCardBadgeBg, selectedLabelColor = GrayCardBadgeText)
                    )
                }
            }
        }

        // List of Activity Items
        if (displayedRecords.isEmpty()) {
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
                        Text(
                            text = if (searchQuery.isNotBlank()) "No records match '$searchQuery'" else strings.noBookingsFound,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(displayedRecords, key = { it.id }) { record ->
                ActivityRecordItem(
                    record = record,
                    strings = strings,
                    onRecordPayment = {
                        val due = (record.cost - record.paidAmount).coerceAtLeast(0.0)
                        paymentAmountInput = String.format(Locale.US, "%.2f", due)
                        recordForPayment = record
                    },
                    onDelete = {
                        recordToDelete = record
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
    onRecordPayment: () -> Unit,
    onDelete: () -> Unit
) {
    val remainingDue = (record.cost - record.paidAmount).coerceAtLeast(0.0)

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
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "₹${String.format(Locale.US, "%,.2f", record.cost)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = OnSurface
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (remainingDue > 0) {
                        FilledTonalButton(
                            onClick = onRecordPayment,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = SecondaryFixed)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = "Pay", tint = OnSecondaryFixed, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Pay", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSecondaryFixed)
                        }
                    }

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
