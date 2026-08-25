package com.agri.costtracker.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import com.agri.costtracker.ui.viewmodel.DashboardMetrics
import java.util.Locale

@Composable
fun FiscalReportDialog(
    metrics: DashboardMetrics,
    businessName: String = "AgriTech Drone & Machinery Services",
    strings: AppStrings,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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
                    .verticalScroll(scrollState)
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.generateFiscalReport.uppercase(),
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
                        ReportRow("Enterprise:", businessName)
                        ReportRow(strings.totalRegistered + ":", "${metrics.farmersCount}")
                        ReportRow("Deployments:", "${metrics.recordsCount}")
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                        ReportRow(strings.totalServiced + ":", "${String.format(Locale.US, "%.1f", metrics.totalAcresServed)} ${strings.acres}")
                        ReportRow(strings.droneSpraying + ":", "${String.format(Locale.US, "%.1f", metrics.totalSprayingAcres)} ${strings.acres}")
                        ReportRow(strings.cuttingMachine + ":", "${String.format(Locale.US, "%.1f", metrics.totalCuttingAcres)} ${strings.acres}")
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                        ReportRow("${strings.droneRate}:", "₹${String.format(Locale.US, "%.2f", metrics.sprayingRate)} / ${strings.acres}")
                        ReportRow("${strings.cuttingRate}:", "₹${String.format(Locale.US, "%.2f", metrics.cropCuttingRate)} / ${strings.acres}")
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                        ReportRow(strings.totalBilled + ":", "₹${String.format(Locale.US, "%,.2f", metrics.totalRevenue)}")
                        ReportRow(strings.totalCollected + ":", "₹${String.format(Locale.US, "%,.2f", metrics.totalPaid)}")
                        ReportRow(strings.pendingDue + ":", "₹${String.format(Locale.US, "%,.2f", metrics.pendingPayables)}", isTotal = true)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        exportFiscalReport(context, metrics, businessName, strings)
                    },
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
                        Icon(Icons.Default.Share, contentDescription = "Share")
                        Text(
                            text = strings.exportShare,
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

private fun exportFiscalReport(
    context: Context,
    metrics: DashboardMetrics,
    businessName: String,
    strings: AppStrings
) {
    val sb = StringBuilder()
    sb.append("🌾 *AGRI SERVICES - BUSINESS FISCAL REPORT* 🌾\n")
    sb.append("════════════════════════════════════\n")
    sb.append("🏢 *Enterprise:* $businessName\n")
    sb.append("📅 *Season:* 2024 Summary\n")
    sb.append("👥 *Total Registered Farmers:* ${metrics.farmersCount}\n")
    sb.append("🚁 *Deployments:* ${metrics.recordsCount}\n")
    sb.append("────────────────────────────────────\n")
    sb.append("🌱 *Total Land Serviced:* ${String.format(Locale.US, "%.1f", metrics.totalAcresServed)} ${strings.acres}\n")
    sb.append("   • Drone Spraying: ${String.format(Locale.US, "%.1f", metrics.totalSprayingAcres)} ${strings.acres}\n")
    sb.append("   • Cutting Machine: ${String.format(Locale.US, "%.1f", metrics.totalCuttingAcres)} ${strings.acres}\n")
    sb.append("────────────────────────────────────\n")
    sb.append("💵 *Financial Summary:*\n")
    sb.append("   • Drone Rate: ₹${String.format(Locale.US, "%.2f", metrics.sprayingRate)} / ${strings.acres}\n")
    sb.append("   • Cutting Rate: ₹${String.format(Locale.US, "%.2f", metrics.cropCuttingRate)} / ${strings.acres}\n")
    sb.append("   • Total Billed: ₹${String.format(Locale.US, "%,.2f", metrics.totalRevenue)}\n")
    sb.append("   • Total Collected: ₹${String.format(Locale.US, "%,.2f", metrics.totalPaid)}\n")
    sb.append("   • Total Pending Due: ₹${String.format(Locale.US, "%,.2f", metrics.pendingPayables)}\n")
    sb.append("════════════════════════════════════\n")

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, "Agri Fiscal Report - 2024")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Fiscal Report via")
    try {
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open sharing: ${e.message}", Toast.LENGTH_SHORT).show()
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
