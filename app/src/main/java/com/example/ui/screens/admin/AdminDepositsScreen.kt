package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositEntity
import com.example.ui.NexusViewModel
import com.example.ui.components.NeonButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDepositsScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDeposits by viewModel.allDeposits.collectAsState()
    val pendingDeposits by viewModel.pendingDeposits.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Pending, 1: All
    var rejectingDeposit by remember { mutableStateOf<DepositEntity?>(null) }
    var rejectionReason by remember { mutableStateOf("Invalid UTR / Payment Not Received") }

    val currentList = if (selectedTabIndex == 0) pendingDeposits else allDeposits

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "UPI DEPOSIT VERIFICATION",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Verify UTR with your UPI app / bank SMS and approve wallet credits",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Tab Selection
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberSurface,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonCyan,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "PENDING (${pendingDeposits.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTabIndex == 0) NeonAmber else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_pending_deposits")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "ALL DEPOSITS (${allDeposits.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTabIndex == 1) NeonCyan else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_all_deposits")
                )
            }
        }

        if (currentList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTabIndex == 0) "No pending deposits to verify!" else "No deposits recorded yet.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        items(currentList, key = { it.id }) { deposit ->
            AdminDepositCard(
                deposit = deposit,
                onApprove = { viewModel.approveDeposit(deposit.id) },
                onReject = { rejectingDeposit = deposit },
                onCopyUtr = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("UTR", deposit.utrNumber))
                    Toast.makeText(context, "UTR copied: ${deposit.utrNumber}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Rejection Dialog
    if (rejectingDeposit != null) {
        val target = rejectingDeposit!!
        val predefinedReasons = listOf(
            "Invalid UTR / Payment Not Received",
            "UTR Already Claimed by Another User",
            "Amount Mismatch with Bank Statement",
            "Bank Server Delayed / Not Credited"
        )

        AlertDialog(
            onDismissRequest = { rejectingDeposit = null },
            containerColor = CyberSurface,
            title = {
                Text(
                    text = "REJECT DEPOSIT",
                    color = NeonRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Rejecting ₹${target.amount.toInt()} for user '${target.username}' (UTR: ${target.utrNumber})",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "Select or enter rejection reason:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    predefinedReasons.forEach { reason ->
                        Surface(
                            onClick = { rejectionReason = reason },
                            shape = RoundedCornerShape(8.dp),
                            color = if (rejectionReason == reason) NeonRed.copy(alpha = 0.2f) else CyberSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (rejectionReason == reason) NeonRed else CyberSurfaceBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = reason,
                                color = if (rejectionReason == reason) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = { Text("Custom Reason", color = TextSecondary, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonRed,
                            unfocusedBorderColor = CyberSurfaceBorder
                        )
                    )
                }
            },
            confirmButton = {
                NeonButton(
                    text = "CONFIRM REJECTION",
                    gradientColors = listOf(NeonRed, Color(0xFF8B0000)),
                    onClick = {
                        viewModel.rejectDeposit(target.id, rejectionReason)
                        rejectingDeposit = null
                    },
                    modifier = Modifier.height(42.dp),
                    testTag = "confirm_rejection_button"
                )
            },
            dismissButton = {
                TextButton(onClick = { rejectingDeposit = null }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun AdminDepositCard(
    deposit: DepositEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onCopyUtr: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (deposit.status == "PENDING") NeonAmber.copy(alpha = 0.6f) else CyberSurfaceBorder,
                RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: User, Amount, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = deposit.username,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = dateFormat.format(Date(deposit.createdAt)),
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${deposit.amount.toInt()}",
                        color = NeonGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    StatusBadge(status = deposit.status)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // UTR Box with copy button
            Surface(
                onClick = onCopyUtr,
                shape = RoundedCornerShape(8.dp),
                color = CyberSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder),
                modifier = Modifier.fillMaxWidth().testTag("copy_utr_${deposit.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "UTR: ", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = deposit.utrNumber,
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "COPY", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                    }
                }
            }

            if (!deposit.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Rejection Reason: ${deposit.rejectionReason}",
                    color = NeonRed,
                    fontSize = 11.sp
                )
            }

            // Approve / Reject Actions (Only if PENDING)
            if (deposit.status == "PENDING") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reject Button
                    Surface(
                        onClick = onReject,
                        shape = RoundedCornerShape(10.dp),
                        color = CyberSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonRed.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("admin_reject_btn_${deposit.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = NeonRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("REJECT", color = NeonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Approve Button
                    Surface(
                        onClick = onApprove,
                        shape = RoundedCornerShape(10.dp),
                        color = NeonGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(42.dp)
                            .testTag("admin_approve_btn_${deposit.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("APPROVE & CREDIT", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
