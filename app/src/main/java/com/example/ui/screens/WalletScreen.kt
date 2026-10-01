package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositEntity
import com.example.ui.NexusViewModel
import com.example.ui.components.CyberTextField
import com.example.ui.components.NeonButton
import com.example.ui.components.StatusBadge
import com.example.ui.components.UpiQrCodeCard
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val deposits by viewModel.userDeposits.collectAsState()

    var amountText by remember { mutableStateOf("") }
    var utrText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val minDeposit = settings?.minDeposit ?: 10.0
    val upiId = settings?.upiId ?: "nexuspanel@upi"
    val payeeName = settings?.payeeName ?: "NEXUS PANEL OFFICIAL"

    val quickAmounts = listOf(50.0, 100.0, 200.0, 500.0, 1000.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
                        RoundedCornerShape(18.dp)
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF131A38),
                                    Color(0xFF1B1138),
                                    Color(0xFF0F142A)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEXUS CYBER WALLET",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                color = NeonGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = NeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "₹${currentUser?.walletBalance?.toInt() ?: 0}",
                            color = TextPrimary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "Available funds for instant key purchases",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // UPI QR Code Component
        item {
            val enteredAmount = amountText.toDoubleOrNull() ?: 0.0
            UpiQrCodeCard(
                upiId = upiId,
                payeeName = payeeName,
                amount = enteredAmount,
                minDeposit = minDeposit
            )
        }

        // Deposit Submission Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUBMIT UPI PAYMENT DETAILS",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Amount Chips
                    Text(
                        text = "QUICK AMOUNT SELECT",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickAmounts.forEach { amt ->
                            Surface(
                                onClick = { amountText = amt.toInt().toString() },
                                shape = RoundedCornerShape(8.dp),
                                color = if (amountText == amt.toInt().toString()) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (amountText == amt.toInt().toString()) NeonCyan else CyberSurfaceBorder
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "₹${amt.toInt()}",
                                    color = if (amountText == amt.toInt().toString()) NeonCyan else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Amount Field
                    CyberTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            statusMessage = null
                        },
                        label = "Amount in INR (Minimum ₹${minDeposit.toInt()})",
                        leadingIcon = Icons.Default.AccountBalanceWallet,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        testTag = "deposit_amount_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // UTR Number Field
                    CyberTextField(
                        value = utrText,
                        onValueChange = {
                            utrText = it
                            statusMessage = null
                        },
                        label = "12-Digit UPI UTR / Reference No.",
                        leadingIcon = Icons.Default.Payment,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        testTag = "deposit_utr_input"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Find UTR/Ref in GPay/PhonePe payment details screen",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    AnimatedVisibility(visible = statusMessage != null) {
                        statusMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .background(
                                        if (isSuccessStatus) NeonGreen.copy(alpha = 0.15f) else NeonRed.copy(alpha = 0.15f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSuccessStatus) NeonGreen.copy(alpha = 0.5f) else NeonRed.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg,
                                    color = if (isSuccessStatus) NeonGreen else NeonRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    NeonButton(
                        text = "SUBMIT UTR FOR APPROVAL",
                        isLoading = isSubmitting,
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt < minDeposit) {
                                statusMessage = "Minimum deposit amount is ₹${minDeposit.toInt()}"
                                isSuccessStatus = false
                                return@NeonButton
                            }
                            if (utrText.trim().length < 8) {
                                statusMessage = "Please enter valid 12-digit UPI UTR"
                                isSuccessStatus = false
                                return@NeonButton
                            }
                            isSubmitting = true
                            viewModel.submitDeposit(amt, utrText) { success, msg ->
                                isSubmitting = false
                                statusMessage = msg
                                isSuccessStatus = success
                                if (success) {
                                    amountText = ""
                                    utrText = ""
                                }
                            }
                        },
                        testTag = "submit_deposit_button"
                    )
                }
            }
        }

        // Deposit History Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MY DEPOSIT HISTORY",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "${deposits.size} Total",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        if (deposits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No deposits yet. Add funds using UPI above!",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        items(deposits, key = { it.id }) { deposit ->
            DepositHistoryCard(deposit = deposit)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DepositHistoryCard(deposit: DepositEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(deposit.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${deposit.amount.toInt()}",
                    color = NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                StatusBadge(status = deposit.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "UTR: ${deposit.utrNumber}",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = formattedDate,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            if (!deposit.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reason: ${deposit.rejectionReason}",
                    color = NeonRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
