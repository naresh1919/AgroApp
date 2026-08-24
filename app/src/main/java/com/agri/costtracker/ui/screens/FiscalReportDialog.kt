package com.agri.costtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.agri.costtracker.ui.theme.*
import com.agri.costtracker.ui.viewmodel.DashboardMetrics
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FiscalReportDialog(
    metrics: DashboardMetrics,
    farmerName: String,
    onDismiss: () -> Unit
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 2
        }
    }
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerLowest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FISCAL REPORT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Season 2024 Summary",
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportRow("Farmer Account:", farmerName)
                        ReportRow("Total Acreage:", "${metrics.totalAcres.toInt()} AC")
                        ReportRow("Cultivated Land:", "${metrics.cultivatedAcres.toInt()} AC")
                        ReportRow("Fallow Land:", "${metrics.fallowAcres.toInt()} AC")
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                        ReportRow("Spraying Rate:", "$${String.format(Locale.US, "%.2f", metrics.sprayingRate)} / AC")
                        ReportRow("Estimated Spraying:", currencyFormatter.format(metrics.estimatedSprayingCost))
                        ReportRow("Harvesting Rate:", "$${String.format(Locale.US, "%.2f", metrics.cropCuttingRate)} / AC")
                        ReportRow("Estimated Harvesting:", currencyFormatter.format(metrics.estimatedHarvestingCost))
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                        ReportRow("Total Projected Cost:", currencyFormatter.format(metrics.estimatedSprayingCost + metrics.estimatedHarvestingCost), isTotal = true)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                        Text(
                            text = "Export Statement",
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

@Composable
private fun ReportRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = Primary) else MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
