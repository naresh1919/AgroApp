package com.agri.costtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFarmerDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onAddFarmer: (name: String, mobile: String, village: String, totalAcres: Double, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var acresInput by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var mobileError by remember { mutableStateOf(false) }

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
                            text = strings.clientRegistration,
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = strings.registerNewFarmer.replace("+ ", ""),
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

                // Farmer Name (Mandatory)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text(strings.farmerFullName) },
                    placeholder = { Text("e.g. Ramesh Patel / రమేష్") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Name", tint = Primary)
                    },
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text(strings.nameMandatory, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Mobile Number (Mandatory)
                OutlinedTextField(
                    value = mobile,
                    onValueChange = {
                        mobile = it
                        if (it.isNotBlank()) mobileError = false
                    },
                    label = { Text(strings.mobileNumber) },
                    placeholder = { Text("e.g. 9876543210") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Mobile", tint = Primary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = mobileError,
                    supportingText = {
                        if (mobileError) {
                            Text(strings.mobileMandatory, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Village / Location
                OutlinedTextField(
                    value = village,
                    onValueChange = { village = it },
                    label = { Text(strings.villageSector) },
                    placeholder = { Text("e.g. Rampur / గ్రీన్ వ్యాలీ") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = "Village", tint = Outline)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Total Farm Land (Acres) - Optional
                OutlinedTextField(
                    value = acresInput,
                    onValueChange = { acresInput = it },
                    label = { Text(strings.totalFarmLand) },
                    placeholder = { Text("e.g. 25.0") },
                    leadingIcon = {
                        Icon(Icons.Default.Landscape, contentDescription = "Acres", tint = Outline)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes / Crop Details
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.cropsNotes) },
                    placeholder = { Text("e.g. Cotton, Paddy, Spraying") },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = "Notes", tint = Outline)
                    },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save Button
                Button(
                    onClick = {
                        val validName = name.isNotBlank()
                        val validMobile = mobile.isNotBlank()
                        if (!validName) nameError = true
                        if (!validMobile) mobileError = true

                        if (validName && validMobile) {
                            val acres = acresInput.toDoubleOrNull() ?: 0.0
                            onAddFarmer(name, mobile, village, acres, notes)
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
                            text = strings.registerNewFarmer.replace("+ ", ""),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}
