package com.agri.costtracker.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.agri.costtracker.data.sync.CloudSyncState
import com.agri.costtracker.ui.localization.AppStrings
import com.agri.costtracker.ui.theme.*
import com.agri.costtracker.ui.viewmodel.AgriViewModel

@Composable
fun CloudBackupDialog(
    viewModel: AgriViewModel,
    strings: AppStrings,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val syncState by viewModel.syncState.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val signedInUser by viewModel.signedInUser.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val authenticationError by viewModel.authenticationError.collectAsState()
    val farmers by viewModel.allFarmers.collectAsState()
    val allRecords by viewModel.allRecords.collectAsState()
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryContainer.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("☁", color = Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }

                        Column {
                            Text(
                                text = "GOOGLE FIREBASE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.5.sp,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Cloud Backup & Sync",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = OnSurface
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sync Status Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (syncState) {
                                CloudSyncState.SUCCESS -> GreenCardBadgeBg.copy(alpha = 0.5f)
                                CloudSyncState.ERROR -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                CloudSyncState.SYNCING -> PrimaryContainer.copy(alpha = 0.2f)
                                CloudSyncState.IDLE -> SurfaceContainerLow
                            }
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (syncState) {
                            CloudSyncState.SYNCING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.5.dp,
                                    color = Primary
                                )
                            }
                            CloudSyncState.SUCCESS -> {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Primary, modifier = Modifier.size(22.dp))
                            }
                            CloudSyncState.ERROR -> {
                                Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp))
                            }
                            CloudSyncState.IDLE -> {
                                Icon(Icons.Default.CloudDone, contentDescription = "Idle", tint = Primary, modifier = Modifier.size(22.dp))
                            }
                        }

                        Column {
                            Text(
                                text = when (syncState) {
                                    CloudSyncState.SYNCING -> "Synchronizing..."
                                    CloudSyncState.SUCCESS -> "Cloud Synchronized"
                                    CloudSyncState.ERROR -> "Sync Notice"
                                    CloudSyncState.IDLE -> "Cloud Ready (Offline Cache Active)"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = OnSurface)
                            )
                            Text(
                                text = syncMessage,
                                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant, fontSize = 10.5.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (signedInUser == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryContainer.copy(alpha = 0.16f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Sign in to enable secure cloud sync", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = OnSurface))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("You can continue using the app offline without signing in. Your data stays on this device until you choose to sync.", style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant))
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { (context as? Activity)?.let(viewModel::signInWithGoogle) },
                                enabled = !isAuthenticating && context is Activity,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text(if (isAuthenticating) "Signing in…" else "Continue with Google", fontWeight = FontWeight.Bold)
                            }
                            if (authenticationError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(authenticationError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenCardBadgeBg.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Signed in as", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Primary))
                                Text(signedInUser!!.displayName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = OnSurface))
                                Text(signedInUser!!.email, style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant))
                            }
                            TextButton(onClick = viewModel::signOut) { Text("Sign out") }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Farmers", style = MaterialTheme.typography.labelSmall.copy(color = Outline, fontSize = 10.sp))
                            Text("${farmers.size}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Primary))
                        }
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(OutlineVariant))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Bookings", style = MaterialTheme.typography.labelSmall.copy(color = Outline, fontSize = 10.sp))
                            Text("${allRecords.size}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = OnSurface))
                        }
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(OutlineVariant))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Mode", style = MaterialTheme.typography.labelSmall.copy(color = Outline, fontSize = 10.sp))
                            Text("Offline-1st", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Secondary))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action 1: Backup To Cloud Now Button
                Button(
                    onClick = { viewModel.triggerCloudBackup() },
                    enabled = syncState != CloudSyncState.SYNCING && signedInUser != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
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
                            Text("↑", color = OnPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Backup to Cloud Now",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = OnPrimary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Restore from Cloud Button
                OutlinedButton(
                    onClick = { viewModel.triggerCloudRestore() },
                    enabled = syncState != CloudSyncState.SYNCING && signedInUser != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("↓", color = Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Restore from Cloud",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Informational Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TertiaryContainer.copy(alpha = 0.12f))
                        .border(1.dp, Tertiary.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Info", tint = Tertiary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Zero-Cost Offline Architecture",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnTertiaryContainer)
                            )
                        }
                        Text(
                            text = "Data is always cached locally on your device for immediate access in remote farm fields with no network. When internet is detected, updates sync automatically to Google Cloud Firestore under the free Spark quota.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, color = OnTertiaryContainer, lineHeight = 15.sp)
                        )
                    }
                }
            }
        }
    }
}
