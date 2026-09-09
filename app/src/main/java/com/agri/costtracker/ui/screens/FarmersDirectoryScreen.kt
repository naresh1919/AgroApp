package com.agri.costtracker.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import com.agri.costtracker.ui.viewmodel.AgriViewModel
import java.util.Locale

@Composable
fun FarmersDirectoryScreen(
    viewModel: AgriViewModel,
    strings: AppStrings,
    onOpenAddFarmer: () -> Unit
) {
    val context = LocalContext.current
    val farmers by viewModel.allFarmers.collectAsState()
    val allRecords by viewModel.allRecords.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()
    val selectedFarmerForStatement by viewModel.selectedFarmerForStatement.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterPendingOnly by remember { mutableStateOf(false) }
    var farmerToDelete by remember { mutableStateOf<Farmer?>(null) }

    val filteredFarmers = remember(farmers, allRecords, searchQuery, filterPendingOnly) {
        farmers.filter { farmer ->
            val matchesSearch = searchQuery.isBlank() ||
                farmer.name.contains(searchQuery, ignoreCase = true) ||
                farmer.mobile.contains(searchQuery, ignoreCase = true) ||
                farmer.village.contains(searchQuery, ignoreCase = true)

            val matchesPending = if (filterPendingOnly) {
                val farmerRecords = allRecords.filter { it.farmerId == farmer.id }
                val totalCost = farmerRecords.sumOf { it.cost }
                val totalPaid = farmerRecords.sumOf { it.paidAmount }
                (totalCost - totalPaid) > 0.0
            } else true

            matchesSearch && matchesPending
        }
    }

    if (selectedFarmerForStatement != null) {
        FarmerStatementDialog(
            farmer = selectedFarmerForStatement!!,
            records = allRecords,
            payments = allPayments,
            strings = strings,
            onRecordPayment = { record, amount, date, mode, notes ->
                viewModel.recordPayment(record, amount, date, mode, notes)
            },
            onDismiss = { viewModel.selectFarmerForStatement(null) }
        )
    }

    farmerToDelete?.let { farmer ->
        AlertDialog(
            onDismissRequest = { farmerToDelete = null },
            title = { Text("Delete farmer?") },
            text = { Text("This will permanently remove ${farmer.name}, all of their bookings, and payment history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFarmer(farmer)
                        Toast.makeText(context, "${farmer.name} ${strings.delete}", Toast.LENGTH_SHORT).show()
                        farmerToDelete = null
                    }
                ) { Text(strings.delete, color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { farmerToDelete = null }) { Text("Cancel") } }
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
                    text = strings.farmersDirectoryTitle.uppercase(),
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
                        text = strings.farmersDirectoryTitle,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            color = OnSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.farmersDirectorySubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariant,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        // Action: Register New Farmer Card / Button
        item {
            Button(
                onClick = onOpenAddFarmer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Primary, PrimaryContainer))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add", tint = OnPrimary)
                        Text(
                            text = strings.registerNewFarmer,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnPrimary
                            )
                        )
                    }
                }
            }
        }

        // Summary Metric Banner
        item {
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
                    Column {
                        Text(
                            text = strings.totalRegistered,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${farmers.size}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Primary
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(OutlineVariant)
                    )

                    val totalAcresAll = allRecords.sumOf { it.acres }
                    Column {
                        Text(
                            text = strings.totalServiced,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.0f", totalAcresAll)} ${strings.acres}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = OnSurface
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(OutlineVariant)
                    )

                    val totalPendingAll = (allRecords.sumOf { it.cost } - allRecords.sumOf { it.paidAmount }).coerceAtLeast(0.0)
                    Column {
                        Text(
                            text = strings.pendingBills,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${String.format(Locale.US, "%,.0f", totalPendingAll)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Secondary
                            )
                        )
                    }
                }
            }
        }

        // Search Bar & Filter Tabs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search farmer, phone, village...") },
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

                // Quick Filter Tabs: All vs Pending Dues
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val pendingCount = remember(farmers, allRecords) {
                        farmers.count { f ->
                            val r = allRecords.filter { it.farmerId == f.id }
                            (r.sumOf { it.cost } - r.sumOf { it.paidAmount }) > 0
                        }
                    }

                    FilterChip(
                        selected = !filterPendingOnly,
                        onClick = { filterPendingOnly = false },
                        label = { Text("All Farmers (${farmers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryContainer.copy(alpha = 0.25f),
                            selectedLabelColor = Primary
                        )
                    )

                    FilterChip(
                        selected = filterPendingOnly,
                        onClick = { filterPendingOnly = true },
                        label = { Text("Pending Dues ($pendingCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SecondaryFixed,
                            selectedLabelColor = OnSecondaryFixed
                        )
                    )
                }
            }
        }

        if (filteredFarmers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "No Farmers",
                            tint = Outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No farmers match '$searchQuery'" else strings.noFarmersYet,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onOpenAddFarmer) {
                            Text(strings.registerNewFarmer, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredFarmers, key = { it.id }) { farmer ->
                val farmerRecords = allRecords.filter { it.farmerId == farmer.id }
                val farmerAcres = farmerRecords.sumOf { it.acres }
                val farmerTotalCost = farmerRecords.sumOf { it.cost }
                val farmerTotalPaid = farmerRecords.sumOf { it.paidAmount }
                val farmerPending = (farmerTotalCost - farmerTotalPaid).coerceAtLeast(0.0)

                FarmerCardItem(
                    farmer = farmer,
                    servicedAcres = farmerAcres,
                    totalBilled = farmerTotalCost,
                    pendingAmount = farmerPending,
                    bookingCount = farmerRecords.size,
                    strings = strings,
                    onViewStatement = {
                        viewModel.selectFarmerForStatement(farmer)
                    },
                    onCallFarmer = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${farmer.mobile}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Calling ${farmer.mobile}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onWhatsAppFarmer = {
                        val cleanNumber = farmer.mobile.filter { it.isDigit() }
                        val phoneParam = if (cleanNumber.length == 10) "91$cleanNumber" else cleanNumber
                        val message = "🌾 Agri Services Statement Update:\nFarmer: ${farmer.name}\nTotal Serviced: ${String.format(Locale.US, "%.1f", farmerAcres)} acres\nPending Balance Due: ₹${String.format(Locale.US, "%,.2f", farmerPending)}"
                        val url = "https://api.whatsapp.com/send?phone=$phoneParam&text=${Uri.encode(message)}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open WhatsApp for ${farmer.mobile}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDeleteFarmer = {
                        farmerToDelete = farmer
                    }
                )
            }
        }
    }
}

@Composable
fun FarmerCardItem(
    farmer: Farmer,
    servicedAcres: Double,
    totalBilled: Double,
    pendingAmount: Double,
    bookingCount: Int,
    strings: AppStrings,
    onViewStatement: () -> Unit,
    onCallFarmer: () -> Unit,
    onWhatsAppFarmer: () -> Unit,
    onDeleteFarmer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clickable { onViewStatement() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Row 1: Name, Avatar, and Quick Call / WhatsApp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer.copy(alpha = 0.2f))
                            .border(1.5.dp, Primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = farmer.name.firstOrNull()?.toString()?.uppercase() ?: "F",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Primary
                            )
                        )
                    }

                    Column {
                        Text(
                            text = farmer.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Outline,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = farmer.village.ifEmpty { "General Location" },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Call, WhatsApp & Delete Icons
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onCallFarmer) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Primary)
                    }
                    IconButton(onClick = onWhatsAppFarmer) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                    }
                    IconButton(onClick = onDeleteFarmer) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Outline.copy(alpha = 0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Metrics pill tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Serviced Land Tag
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = strings.servicedLand,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline
                            )
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.1f", servicedAcres)} ${strings.acres}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = OnSurface,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Total Billed Tag
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = strings.totalBilled,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline
                            )
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%,.0f", totalBilled)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Primary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Pending Due Tag
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (pendingAmount > 0) SecondaryContainer.copy(alpha = 0.3f) else SurfaceContainerLow)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = strings.pendingDue,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pendingAmount > 0) Secondary else Outline
                            )
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%,.0f", pendingAmount)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = if (pendingAmount > 0) Secondary else OnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 3: Action to view statement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewStatement() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$bookingCount ${strings.recentDeployments}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = strings.viewStatement,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Statement",
                        tint = Primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
