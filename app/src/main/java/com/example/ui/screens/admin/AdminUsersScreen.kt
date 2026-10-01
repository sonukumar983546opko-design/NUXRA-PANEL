package com.example.ui.screens.admin

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.NexusViewModel
import com.example.ui.components.CyberTextField
import com.example.ui.components.NeonButton
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminUsersScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val users by viewModel.allUsers.collectAsState()
    var searchUser by remember { mutableStateOf("") }
    var adjustingUser by remember { mutableStateOf<UserEntity?>(null) }

    val filteredUsers = users.filter {
        it.username.contains(searchUser, ignoreCase = true) || it.email.contains(searchUser, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "USER & WALLET MANAGEMENT",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Manage registered players, adjust balance, ban or unban accounts",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchUser,
                onValueChange = { searchUser = it },
                placeholder = { Text("Search by username or email...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                modifier = Modifier.fillMaxWidth().testTag("admin_user_search"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CyberSurface,
                    unfocusedContainerColor = CyberSurface,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        items(filteredUsers, key = { it.id }) { user ->
            AdminUserCard(
                user = user,
                onAdjustBalance = { adjustingUser = user },
                onToggleBan = { viewModel.toggleUserBan(user.id, !user.isBanned) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Adjust Balance Modal
    if (adjustingUser != null) {
        val target = adjustingUser!!
        var newBalanceStr by remember { mutableStateOf(target.walletBalance.toInt().toString()) }
        var reason by remember { mutableStateOf("Manual Admin Credit / Correction") }

        AlertDialog(
            onDismissRequest = { adjustingUser = null },
            containerColor = CyberSurface,
            title = {
                Text("ADJUST WALLET BALANCE", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("User: ${target.username} (${target.email})", color = TextPrimary, fontSize = 13.sp)
                    Text("Current Balance: ₹${target.walletBalance.toInt()}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    CyberTextField(
                        value = newBalanceStr,
                        onValueChange = { newBalanceStr = it },
                        label = "New Balance in INR (₹)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    CyberTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = "Adjustment Reason / Audit Note"
                    )
                }
            },
            confirmButton = {
                NeonButton(
                    text = "APPLY NEW BALANCE",
                    onClick = {
                        val nb = newBalanceStr.toDoubleOrNull()
                        if (nb != null && nb >= 0) {
                            viewModel.adjustUserBalance(target.id, nb, reason)
                            adjustingUser = null
                        }
                    },
                    modifier = Modifier.height(42.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { adjustingUser = null }) { Text("CANCEL", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun AdminUserCard(
    user: UserEntity,
    onAdjustBalance: () -> Unit,
    onToggleBan: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (user.isBanned) NeonRed.copy(alpha = 0.6f) else CyberSurfaceBorder,
                RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (user.role == "ADMIN") NeonPurple.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.username.take(1).uppercase(),
                            color = if (user.role == "ADMIN") NeonPurple else NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = user.username, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (user.role == "ADMIN") NeonPurple.copy(alpha = 0.2f) else CyberSurfaceElevated,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = user.role,
                                    color = if (user.role == "ADMIN") NeonPurple else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(text = user.email, color = TextSecondary, fontSize = 11.sp)
                    }
                }

                // Balance
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "₹${user.walletBalance.toInt()}", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text(text = if (user.isBanned) "BANNED" else "ACTIVE", color = if (user.isBanned) NeonRed else NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onAdjustBalance,
                    shape = RoundedCornerShape(8.dp),
                    color = CyberSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ADJUST WALLET", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    onClick = onToggleBan,
                    shape = RoundedCornerShape(8.dp),
                    color = CyberSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (user.isBanned) NeonGreen.copy(alpha = 0.5f) else NeonRed.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (user.isBanned) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = null,
                            tint = if (user.isBanned) NeonGreen else NeonRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isBanned) "UNBAN" else "BAN USER",
                            color = if (user.isBanned) NeonGreen else NeonRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
