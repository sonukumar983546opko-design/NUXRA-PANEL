package com.example.ui.screens.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystemSettingsEntity
import com.example.ui.NexusViewModel
import com.example.ui.components.CyberTextField
import com.example.ui.components.NeonButton
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminSettingsScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val currentSettings by viewModel.settings.collectAsState()
    val initial = currentSettings ?: SystemSettingsEntity()

    var upiId by remember(initial) { mutableStateOf(initial.upiId) }
    var payeeName by remember(initial) { mutableStateOf(initial.payeeName) }
    var minDepositStr by remember(initial) { mutableStateOf(initial.minDeposit.toInt().toString()) }

    var telegramChannel by remember(initial) { mutableStateOf(initial.telegramChannel) }
    var telegramGroup by remember(initial) { mutableStateOf(initial.telegramGroup) }
    var telegramSupport by remember(initial) { mutableStateOf(initial.telegramSupport) }

    var announcement by remember(initial) { mutableStateOf(initial.announcement) }
    var externalApiBaseUrl by remember(initial) { mutableStateOf(initial.externalApiBaseUrl) }
    var externalApiKeySecret by remember(initial) { mutableStateOf(initial.externalApiKeySecret) }
    var autoApproveApiKeys by remember(initial) { mutableStateOf(initial.autoApproveApiKeys) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "GLOBAL PANEL CONFIGURATION",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Changes take effect in real-time across customer app & wallet",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Section 1: UPI & Deposit Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("UPI PAYMENT & QR SETTINGS", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    CyberTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = "UPI ID / VPA (e.g. yourbusiness@upi)"
                    )

                    CyberTextField(
                        value = payeeName,
                        onValueChange = { payeeName = it },
                        label = "Merchant / Payee Name"
                    )

                    CyberTextField(
                        value = minDepositStr,
                        onValueChange = { minDepositStr = it },
                        label = "Minimum Deposit Amount in INR (₹)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        // Section 2: Telegram Links
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = NeonPurple)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("TELEGRAM COMMUNITY & SUPPORT LINKS", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    CyberTextField(
                        value = telegramChannel,
                        onValueChange = { telegramChannel = it },
                        label = "Official Telegram Channel Link"
                    )

                    CyberTextField(
                        value = telegramGroup,
                        onValueChange = { telegramGroup = it },
                        label = "Community Discussion Group Link"
                    )

                    CyberTextField(
                        value = telegramSupport,
                        onValueChange = { telegramSupport = it },
                        label = "24/7 Live Customer Support Bot Link"
                    )
                }
            }
        }

        // Section 3: Announcement Marquee
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = NeonPink)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("HOMEPAGE ANNOUNCEMENT TICKER", color = NeonPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    CyberTextField(
                        value = announcement,
                        onValueChange = { announcement = it },
                        label = "Broadcast Banner Text (Visible to all users)",
                        singleLine = false
                    )
                }
            }
        }

        // Section 4: External API Key Dispatch
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Api, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("SECURE EXTERNAL KEY DISPATCH API", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    CyberTextField(
                        value = externalApiBaseUrl,
                        onValueChange = { externalApiBaseUrl = it },
                        label = "External Key API Endpoint (e.g. Render / Vercel URL)"
                    )

                    CyberTextField(
                        value = externalApiKeySecret,
                        onValueChange = { externalApiKeySecret = it },
                        label = "Server API Bearer Secret Token"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automatic Key Delivery", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Deliver key immediately upon wallet deduction", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = autoApproveApiKeys,
                            onCheckedChange = { autoApproveApiKeys = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = CyberSurfaceElevated
                            )
                        )
                    }
                }
            }
        }

        // Save Button
        item {
            NeonButton(
                text = "SAVE & APPLY SETTINGS",
                icon = Icons.Default.Save,
                onClick = {
                    val minDep = minDepositStr.toDoubleOrNull() ?: 10.0
                    val updated = initial.copy(
                        upiId = upiId.trim(),
                        payeeName = payeeName.trim(),
                        minDeposit = if (minDep < 1.0) 10.0 else minDep,
                        telegramChannel = telegramChannel.trim(),
                        telegramGroup = telegramGroup.trim(),
                        telegramSupport = telegramSupport.trim(),
                        announcement = announcement.trim(),
                        externalApiBaseUrl = externalApiBaseUrl.trim(),
                        externalApiKeySecret = externalApiKeySecret.trim(),
                        autoApproveApiKeys = autoApproveApiKeys
                    )
                    viewModel.updateSettings(updated)
                },
                modifier = Modifier.height(48.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
