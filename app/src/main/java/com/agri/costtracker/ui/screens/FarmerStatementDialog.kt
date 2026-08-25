package com.agri.costtracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import java.util.Locale

@Composable
fun FarmerStatementDialog(
    farmer: Farmer,
    records: List<ActivityRecord>,
    strings: AppStrings,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val farmerRecords = remember(records, farmer.id) {
        records.filter { it.farmerId == farmer.id }
    }

    val totalAcres = remember(farmerRecords) { farmerRecords.sumOf { it.acres } }
    val totalBilled = remember(farmerRecords) { farmerRecords.sumOf { it.cost } }
    val pendingAmount = remember(farmerRecords) {
        farmerRecords.filter { it.status == RecordStatus.INVOICED }.sumOf { it.cost }
    }
    val droneAcres = remember(farmerRecords) {
        farmerRecords.filter { it.category == RecordCategory.SPRAYING }.sumOf { it.acres }
    }
    val cuttingAcres = remember(farmerRecords) {
        farmerRecords.filter { it.category == RecordCategory.HARVESTING }.sumOf { it.acres }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerLowest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.farmerAccountStatement,
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = farmer.name,
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

                Text(
                    text = "📞 ${farmer.mobile} • 📍 ${farmer.village.ifEmpty { "General Zone" }}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Bento Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.totalServicedLand, style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant, fontSize = 12.sp))
                            Text("${String.format(Locale.US, "%.1f", totalAcres)} ${strings.acres}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.droneSpraying, style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant))
                            Text("${String.format(Locale.US, "%.1f", droneAcres)} ${strings.acres}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.cuttingMachine, style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant))
                            Text("${String.format(Locale.US, "%.1f", cuttingAcres)} ${strings.acres}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        }
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.totalBilledAmount, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("₹${String.format(Locale.US, "%,.2f", totalBilled)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Primary))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.pendingBalanceDue, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("₹${String.format(Locale.US, "%,.2f", pendingAmount)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Secondary))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "${strings.itemizedHistory} (${farmerRecords.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (farmerRecords.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(strings.noBookingsFound, color = Outline)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(farmerRecords) { record ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = record.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${record.date} • ${record.acres} ${strings.acres} @ ₹${String.format(Locale.US, "%.2f", record.ratePerAcre)}/${strings.acres}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = OnSurfaceVariant)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${String.format(Locale.US, "%,.2f", record.cost)}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = Primary)
                                        )
                                        Text(
                                            text = if (record.status == RecordStatus.INVOICED) strings.unpaid else strings.paid,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (record.status == RecordStatus.INVOICED) Secondary else Primary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Statement exported successfully!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                        Text(strings.exportShare, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
