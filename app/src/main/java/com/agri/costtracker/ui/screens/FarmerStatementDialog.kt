package com.agri.costtracker.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.agri.costtracker.data.model.ActivityRecord
import com.agri.costtracker.data.model.Farmer
import com.agri.costtracker.data.model.PaymentRecord
import com.agri.costtracker.data.model.RecordCategory
import com.agri.costtracker.data.model.RecordStatus
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerStatementDialog(
    farmer: Farmer,
    records: List<ActivityRecord>,
    payments: List<PaymentRecord> = emptyList(),
    strings: AppStrings,
    onRecordPayment: (record: ActivityRecord, amount: Double, paymentDate: String, paymentMode: String, notes: String) -> Unit = { _, _, _, _, _ -> },
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val farmerRecords = remember(records, farmer.id) {
        records.filter { it.farmerId == farmer.id }
    }
    val farmerPayments = remember(payments, farmer.id) {
        payments.filter { it.farmerId == farmer.id }
    }

    val totalAcres = remember(farmerRecords) { farmerRecords.sumOf { it.acres } }
    val totalBilled = remember(farmerRecords) { farmerRecords.sumOf { it.cost } }
    val totalPaid = remember(farmerRecords) { farmerRecords.sumOf { it.paidAmount } }
    val pendingAmount = remember(farmerRecords) {
        (totalBilled - totalPaid).coerceAtLeast(0.0)
    }

    var recordForPayment by remember { mutableStateOf<ActivityRecord?>(null) }
    var paymentAmountInput by remember { mutableStateOf("") }
    val todayFormatted = remember {
        SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date())
    }
    var paymentDateInput by remember { mutableStateOf(todayFormatted) }
    var selectedPaymentMode by remember { mutableStateOf("Cash") }
    var paymentNotesInput by remember { mutableStateOf("") }

    // Sub-dialog to Record Payment for an item
    if (recordForPayment != null) {
        val target = recordForPayment!!
        val remainingOnTarget = (target.cost - target.paidAmount).coerceAtLeast(0.0)

        Dialog(onDismissRequest = { recordForPayment = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceContainerLowest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.recordPayment,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Primary)
                        )
                        IconButton(onClick = { recordForPayment = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Text(
                        text = "${target.title}\n• Total: ₹${String.format(Locale.US, "%,.2f", target.cost)}\n• Already Paid: ₹${String.format(Locale.US, "%,.2f", target.paidAmount)} (Due: ₹${String.format(Locale.US, "%,.2f", remainingOnTarget)})",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant, fontSize = 11.sp)
                    )

                    // Payment Amount Input
                    OutlinedTextField(
                        value = paymentAmountInput,
                        onValueChange = { paymentAmountInput = it },
                        label = { Text(strings.enterPaymentAmount) },
                        placeholder = { Text(String.format(Locale.US, "%.2f", remainingOnTarget)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Payment Date Input
                    OutlinedTextField(
                        value = paymentDateInput,
                        onValueChange = { paymentDateInput = it },
                        label = { Text(strings.paymentDateLabel) },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = Primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Payment Mode Chips
                    Text(
                        text = "Payment Mode",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Outline)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Cash", "UPI", "Bank Transfer").forEach { mode ->
                            FilterChip(
                                selected = selectedPaymentMode == mode,
                                onClick = { selectedPaymentMode = mode },
                                label = { Text(mode, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Optional Notes
                    OutlinedTextField(
                        value = paymentNotesInput,
                        onValueChange = { paymentNotesInput = it },
                        label = { Text("Notes (e.g. Google Pay, PhonePe, Cheque)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val amount = paymentAmountInput.toDoubleOrNull() ?: remainingOnTarget
                            if (amount > 0) {
                                val cleanDate = paymentDateInput.ifBlank { todayFormatted }
                                onRecordPayment(target, amount, cleanDate, selectedPaymentMode, paymentNotesInput)
                                Toast.makeText(context, strings.paymentRecordedSuccess, Toast.LENGTH_SHORT).show()
                                recordForPayment = null
                                paymentAmountInput = ""
                                paymentNotesInput = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(strings.save, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

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

                Spacer(modifier = Modifier.height(12.dp))

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
                            Text(strings.totalServicedLand, style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant, fontSize = 11.5.sp))
                            Text("${String.format(Locale.US, "%.1f", totalAcres)} ${strings.acres}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.totalBilledAmount, style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant))
                            Text("₹${String.format(Locale.US, "%,.2f", totalBilled)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.totalPaidAmount, style = MaterialTheme.typography.bodyMedium.copy(color = Primary, fontWeight = FontWeight.Bold))
                            Text("₹${String.format(Locale.US, "%,.2f", totalPaid)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = Primary))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.pendingBalanceDue, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = if (pendingAmount > 0) Secondary else Primary))
                            Text("₹${String.format(Locale.US, "%,.2f", pendingAmount)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = if (pendingAmount > 0) Secondary else Primary))
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
                            .heightIn(max = 260.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(farmerRecords) { record ->
                            val itemRemaining = (record.cost - record.paidAmount).coerceAtLeast(0.0)
                            val itemPayments = farmerPayments.filter { it.recordId == record.id }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (itemRemaining > 0) {
                                            recordForPayment = record
                                            paymentAmountInput = String.format(Locale.US, "%.2f", itemRemaining)
                                            paymentDateInput = todayFormatted
                                        }
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    // Row 1: Title and Billed Cost
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
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
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = OnSurface)
                                            )
                                            val (badgeText, badgeColor, badgeBg) = when (record.status) {
                                                RecordStatus.COMPLETED -> Triple(strings.paid, Primary, GreenCardBadgeBg)
                                                RecordStatus.PARTIAL -> Triple("${strings.partial}: ₹${String.format(Locale.US, "%.0f", record.paidAmount)}", Secondary, OrangeCardBadgeBg)
                                                else -> Triple(strings.unpaid, Secondary, OrangeCardBadgeBg)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(badgeBg)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = badgeText,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = badgeColor
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    // Row 2: Itemized Individual Payment Installments
                                    if (itemPayments.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SurfaceContainerLow.copy(alpha = 0.6f))
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                Text(
                                                    text = "💵 Installment History (${itemPayments.size}):",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Primary)
                                                )
                                                itemPayments.forEachIndexed { idx, pmt ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "• ${pmt.date} (${pmt.paymentMode}):",
                                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, color = OnSurfaceVariant)
                                                        )
                                                        Text(
                                                            text = "₹${String.format(Locale.US, "%,.2f", pmt.amount)}",
                                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Primary)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else if (record.paidAmount > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• Paid: ₹${String.format(Locale.US, "%,.2f", record.paidAmount)}${if (record.lastPaymentDate.isNotBlank()) " on ${record.lastPaymentDate}" else ""}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, color = Primary, fontWeight = FontWeight.Medium)
                                        )
                                    }

                                    // Row 3: Remaining Balance & Tap prompt
                                    if (itemRemaining > 0) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${strings.remainingBalance}: ₹${String.format(Locale.US, "%,.2f", itemRemaining)}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Secondary, fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Tap to Record Payment ➔",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Primary, fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        exportFarmerStatement(context, farmer, farmerRecords, farmerPayments, strings)
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

private fun exportFarmerStatement(
    context: Context,
    farmer: Farmer,
    records: List<ActivityRecord>,
    payments: List<PaymentRecord>,
    strings: AppStrings
) {
    val totalAcres = records.sumOf { it.acres }
    val totalBilled = records.sumOf { it.cost }
    val totalPaid = records.sumOf { it.paidAmount }
    val pendingAmount = (totalBilled - totalPaid).coerceAtLeast(0.0)

    val sb = StringBuilder()
    sb.append("🌾 *AGRI SERVICES - ACCOUNT STATEMENT* 🌾\n")
    sb.append("════════════════════════════════════\n")
    sb.append("👤 *Farmer:* ${farmer.name}\n")
    sb.append("📞 *Mobile:* ${farmer.mobile}\n")
    if (farmer.village.isNotBlank()) {
        sb.append("📍 *Village/Location:* ${farmer.village}\n")
    }
    sb.append("────────────────────────────────────\n")
    sb.append("🚜 *Total Serviced Land:* ${String.format(Locale.US, "%.1f", totalAcres)} ${strings.acres}\n")
    sb.append("💰 *Total Billed:* ₹${String.format(Locale.US, "%,.2f", totalBilled)}\n")
    sb.append("✅ *Total Paid:* ₹${String.format(Locale.US, "%,.2f", totalPaid)}\n")
    sb.append("⚠️ *Pending Balance Due:* ₹${String.format(Locale.US, "%,.2f", pendingAmount)}\n")
    sb.append("════════════════════════════════════\n")
    sb.append("📋 *ITEMIZED SERVICE & PAYMENT HISTORY:*\n")

    if (records.isEmpty()) {
        sb.append("No service records found.\n")
    } else {
        records.forEachIndexed { index, rec ->
            val due = (rec.cost - rec.paidAmount).coerceAtLeast(0.0)
            val recPayments = payments.filter { it.recordId == rec.id }

            sb.append("\n${index + 1}. *${rec.title}*\n")
            sb.append("   • Date: ${rec.date}\n")
            sb.append("   • Acres: ${rec.acres} @ ₹${String.format(Locale.US, "%.2f", rec.ratePerAcre)}/${strings.acres}\n")
            sb.append("   • Total Billed: ₹${String.format(Locale.US, "%,.2f", rec.cost)}\n")

            if (recPayments.isNotEmpty()) {
                sb.append("   • Payments Received (${recPayments.size} installments):\n")
                recPayments.forEach { pmt ->
                    sb.append("     - ₹${String.format(Locale.US, "%,.2f", pmt.amount)} on ${pmt.date} (${pmt.paymentMode}${if (pmt.notes.isNotBlank()) ", ${pmt.notes}" else ""})\n")
                }
            } else if (rec.paidAmount > 0) {
                sb.append("   • Paid: ₹${String.format(Locale.US, "%,.2f", rec.paidAmount)}${if (rec.lastPaymentDate.isNotBlank()) " on ${rec.lastPaymentDate}" else ""}\n")
            }

            sb.append("   • Status: ${if (rec.status == RecordStatus.COMPLETED) "[PAID]" else if (rec.status == RecordStatus.PARTIAL) "[PARTIAL - Due: ₹${String.format(Locale.US, "%,.2f", due)}]" else "[UNPAID - Due: ₹${String.format(Locale.US, "%,.2f", due)}]"}\n")
        }
    }
    sb.append("\n════════════════════════════════════\n")
    sb.append("Thank you for your business! 🙏\n")

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, "Agri Service Statement - ${farmer.name}")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Farmer Statement via")
    try {
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open sharing: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
