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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.AdminNavigationTab
import com.example.ui.NexusViewModel
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
fun AdminDashboardScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val pendingCount by viewModel.pendingDepositsCount.collectAsState()
    val userCount by viewModel.totalUsersCount.collectAsState()
    val orderCount by viewModel.totalOrdersCount.collectAsState()
    val transactions by viewModel.allOrders.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pending Alert Banner (if pending deposits exist)
        if (pendingCount > 0) {
            item {
                Surface(
                    onClick = { viewModel.selectAdminTab(AdminNavigationTab.DEPOSITS) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2B1D0E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                    modifier = Modifier.fillMaxWidth().testTag("pending_deposit_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$pendingCount Deposit(s) Awaiting Approval!",
                                    color = NeonAmber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap to verify submitted UTRs and credit wallets",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Text(
                            text = "REVIEW >",
                            color = NeonAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Stats Grid
        item {
            Text(
                text = "PANEL REAL-TIME METRICS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "TOTAL REVENUE",
                    value = "₹${totalRevenue?.toInt() ?: 0}",
                    icon = Icons.Default.AttachMoney,
                    accentColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "PENDING UTRs",
                    value = pendingCount.toString(),
                    icon = Icons.Default.NotificationsActive,
                    accentColor = NeonAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "TOTAL USERS",
                    value = userCount.toString(),
                    icon = Icons.Default.People,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "KEYS SOLD",
                    value = orderCount.toString(),
                    icon = Icons.Default.Key,
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Navigation Shortcuts
        item {
            Text(
                text = "ADMIN QUICK ACTIONS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminActionRow(
                        title = "Review UTR Deposits",
                        subtitle = "Verify incoming UPI payments and credit wallets",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = NeonAmber,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.DEPOSITS) }
                    )

                    AdminActionRow(
                        title = "Manage Products & Prices",
                        subtitle = "Add new cheats/mods, custom duration pricing & stock keys",
                        icon = Icons.Default.ShoppingBag,
                        color = NeonCyan,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.PRODUCTS) }
                    )

                    AdminActionRow(
                        title = "Manage Users & Wallets",
                        subtitle = "Inspect users, manually adjust balance, ban or unban",
                        icon = Icons.Default.People,
                        color = NeonPurple,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.USERS) }
                    )

                    AdminActionRow(
                        title = "Configure UPI & Telegram Links",
                        subtitle = "Change UPI ID, QR code, Min Deposit & Support links",
                        icon = Icons.Default.Settings,
                        color = NeonPink,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.SETTINGS) }
                    )
                }
            }
        }

        // Recent Orders stream
        item {
            Text(
                text = "RECENT KEY DELIVERIES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    text = "No orders yet in the system.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }

        items(transactions.take(8), key = { it.id }) { order ->
            val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${order.username} • ${order.productName}",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${order.durationLabel} • Key: ${order.licenseKey.take(15)}...",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${order.pricePaid.toInt()}",
                            color = NeonGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateFormat.format(Date(order.purchasedAt)),
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun AdminActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = CyberSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
                }
            }
            Text(text = ">", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
