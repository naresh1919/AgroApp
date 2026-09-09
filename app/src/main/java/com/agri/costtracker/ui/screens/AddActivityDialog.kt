package com.agri.costtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.ServiceRates
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    farmers: List<Farmer>,
    rates: ServiceRates,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onOpenAddFarmer: () -> Unit,
    onAddRecord: (
        farmerId: Long,
        farmerName: String,
        farmerMobile: String,
        title: String,
        category: RecordCategory,
        acres: Double,
        ratePerAcre: Double,
        paidAmount: Double,
        location: String,
        date: String,
        notes: String
    ) -> Unit
) {
    var selectedFarmer by remember { mutableStateOf(farmers.firstOrNull()) }
    var selectedCategory by remember { mutableStateOf(RecordCategory.SPRAYING) }
    val initialAcres = selectedFarmer?.totalAcres?.takeIf { it > 0 }?.let { String.format(Locale.US, "%.1f", it) } ?: "5.0"
    var acresInput by remember { mutableStateOf(initialAcres) }

    // Default rate depends on selected category
    val defaultRate = when (selectedCategory) {
        RecordCategory.SPRAYING -> rates.sprayingRatePerAcre
        RecordCategory.HARVESTING -> rates.cropCuttingRatePerAcre
        else -> 450.0
    }
    var rateInput by remember(selectedCategory) {
        mutableStateOf(String.format(Locale.US, "%.2f", defaultRate))
    }

    var advancePaidInput by remember { mutableStateOf("") }

    val todayFormatted = remember {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        sdf.format(Date())
    }
    var date by remember { mutableStateOf(todayFormatted) }
    var location by remember(selectedFarmer) {
        mutableStateOf(selectedFarmer?.village ?: "Main Agri Zone")
    }
    var customTitle by remember(selectedCategory, strings) {
        mutableStateOf(
            when (selectedCategory) {
                RecordCategory.SPRAYING -> strings.droneSpraying
                RecordCategory.HARVESTING -> strings.cuttingMachine
                else -> strings.newBooking
            }
        )
    }
    var notes by remember { mutableStateOf("") }

    var expandedFarmerDropdown by remember { mutableStateOf(false) }
    var farmerError by remember { mutableStateOf(false) }
    var bookingError by remember { mutableStateOf<String?>(null) }

    val acres = acresInput.toDoubleOrNull() ?: 0.0
    val rate = rateInput.toDoubleOrNull() ?: 0.0
    val calculatedTotal = acres * rate
    val advancePaid = advancePaidInput.toDoubleOrNull() ?: 0.0
    val remainingBalance = (calculatedTotal - advancePaid).coerceAtLeast(0.0)

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerLowest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.newServiceRental,
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = strings.bookDroneMachine,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = OnSurface
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select Farmer Dropdown
                Text(
                    text = strings.selectRegisteredFarmer,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedFarmerDropdown,
                    onExpandedChange = { expandedFarmerDropdown = !expandedFarmerDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedFarmer?.let { "${it.name} (${it.mobile})" } ?: "No Farmer Selected",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFarmerDropdown) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Farmer", tint = Primary)
                        },
                        isError = farmerError && selectedFarmer == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedFarmerDropdown,
                        onDismissRequest = { expandedFarmerDropdown = false }
                    ) {
                        if (farmers.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text(strings.registerNewFarmer, color = Primary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    expandedFarmerDropdown = false
                                    onOpenAddFarmer()
                                }
                            )
                        } else {
                            farmers.forEach { farmer ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(farmer.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${farmer.mobile} • ${farmer.village.ifEmpty { "General" }}",
                                                fontSize = 11.sp,
                                                color = OnSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedFarmer = farmer
                                        location = farmer.village.ifEmpty { "Main Agri Zone" }
                                        if (farmer.totalAcres > 0) {
                                            acresInput = String.format(Locale.US, "%.1f", farmer.totalAcres)
                                        }
                                        farmerError = false
                                        expandedFarmerDropdown = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(strings.registerNewFarmer, color = Primary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    expandedFarmerDropdown = false
                                    onOpenAddFarmer()
                                }
                            )
                        }
                    }
                }

                if (farmerError && selectedFarmer == null) {
                    Text(
                        strings.selectFarmerError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Service Category Selector (Drone vs Cutting Machine)
                Text(
                    text = strings.serviceType,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == RecordCategory.SPRAYING,
                        onClick = { selectedCategory = RecordCategory.SPRAYING },
                        label = { Text(strings.droneSpraying, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryContainer.copy(alpha = 0.3f),
                            selectedLabelColor = Primary
                        )
                    )
                    FilterChip(
                        selected = selectedCategory == RecordCategory.HARVESTING,
                        onClick = { selectedCategory = RecordCategory.HARVESTING },
                        label = { Text(strings.cuttingMachine, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SecondaryContainer.copy(alpha = 0.5f),
                            selectedLabelColor = Secondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Acres & Rate Per Acre Side by Side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = acresInput,
                        onValueChange = { acresInput = it },
                        label = { Text(strings.totalAcres) },
                        placeholder = { Text("0.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = rateInput,
                        onValueChange = { rateInput = it },
                        label = { Text(strings.ratePerAcreInput) },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Partial / Advance Payment Field
                OutlinedTextField(
                    value = advancePaidInput,
                    onValueChange = { advancePaidInput = it },
                    label = { Text(strings.advancePaidOptional) },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Live Cost Calculation Banner with Balance
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.calculatedTotalBill,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Outline)
                            )
                            Text(
                                text = "₹${String.format(Locale.US, "%,.2f", calculatedTotal)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Primary)
                            )
                        }

                        if (advancePaid > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.amountPaidLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Primary)
                                )
                                Text(
                                    text = "₹${String.format(Locale.US, "%,.2f", advancePaid)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Primary)
                                )
                            }
                            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.remainingBalance,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Secondary)
                                )
                                Text(
                                    text = "₹${String.format(Locale.US, "%,.2f", remainingBalance)}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = Secondary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(strings.fieldLocation) },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Outline)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Date selection with quick presets (Today, Yesterday)
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text(strings.paymentDateLabel) },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = Outline)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val yesterdayFormatted = remember {
                        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
                        SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(cal.time)
                    }
                    FilterChip(
                        selected = date == todayFormatted,
                        onClick = { date = todayFormatted },
                        label = { Text("Today", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = date == yesterdayFormatted,
                        onClick = { date = yesterdayFormatted },
                        label = { Text("Yesterday", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes / Remarks
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.cropsNotes) },
                    placeholder = { Text("e.g. Field #2, Nano urea spraying") },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = "Notes", tint = Outline)
                    },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save Record Button
                Button(
                    onClick = {
                        if (selectedFarmer == null) {
                            farmerError = true
                            return@Button
                        }
                        val farmer = selectedFarmer!!
                        bookingError = when {
                            acres <= 0 -> "Enter an area greater than zero"
                            rate <= 0 -> "Enter a rate greater than zero"
                            advancePaid < 0 -> "Payment cannot be negative"
                            advancePaid > calculatedTotal -> "Payment cannot exceed the total bill"
                            else -> null
                        }
                        if (bookingError == null) {
                            onAddRecord(
                                farmer.id,
                                farmer.name,
                                farmer.mobile,
                                customTitle,
                                selectedCategory,
                                acres,
                                rate,
                                advancePaid,
                                location,
                                date,
                                notes
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
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
                        Text(
                            text = strings.saveBooking,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnPrimary
                            )
                        )
                    }
                }
                if (bookingError != null) {
                    Text(
                        text = bookingError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
