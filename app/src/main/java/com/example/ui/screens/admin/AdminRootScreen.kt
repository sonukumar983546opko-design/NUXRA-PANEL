package com.example.ui.screens.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AdminNavigationTab
import com.example.ui.NexusViewModel
import com.example.ui.components.CyberBackgroundBox
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminRootScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val currentAdminTab by viewModel.adminTab.collectAsState()
    val pendingCount by viewModel.pendingDepositsCount.collectAsState()

    // Handle system back button to exit admin panel to customer view
    BackHandler {
        viewModel.setAdminScreenActive(false)
    }

    CyberBackgroundBox(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Admin Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF130E26),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.setAdminScreenActive(false) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Store",
                                tint = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "NEXUS",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ADMIN",
                                    color = NeonPurple,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = "SYSTEM CONTROL PANEL",
                                color = NeonPink,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Surface(
                        onClick = { viewModel.setAdminScreenActive(false) },
                        shape = RoundedCornerShape(8.dp),
                        color = CyberSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder),
                        modifier = Modifier.testTag("exit_admin_button")
                    ) {
                        Text(
                            text = "EXIT TO STORE",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Sub Navigation Tab Pills
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CyberSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminTabPill(
                        title = "DASHBOARD",
                        icon = Icons.Default.Dashboard,
                        isSelected = currentAdminTab == AdminNavigationTab.DASHBOARD,
                        badge = null,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.DASHBOARD) }
                    )

                    AdminTabPill(
                        title = "DEPOSITS",
                        icon = Icons.Default.AccountBalanceWallet,
                        isSelected = currentAdminTab == AdminNavigationTab.DEPOSITS,
                        badge = if (pendingCount > 0) pendingCount.toString() else null,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.DEPOSITS) }
                    )

                    AdminTabPill(
                        title = "PRODUCTS",
                        icon = Icons.Default.ShoppingBag,
                        isSelected = currentAdminTab == AdminNavigationTab.PRODUCTS,
                        badge = null,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.PRODUCTS) }
                    )

                    AdminTabPill(
                        title = "USERS",
                        icon = Icons.Default.People,
                        isSelected = currentAdminTab == AdminNavigationTab.USERS,
                        badge = null,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.USERS) }
                    )

                    AdminTabPill(
                        title = "SETTINGS",
                        icon = Icons.Default.Settings,
                        isSelected = currentAdminTab == AdminNavigationTab.SETTINGS,
                        badge = null,
                        onClick = { viewModel.selectAdminTab(AdminNavigationTab.SETTINGS) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentAdminTab) {
                    AdminNavigationTab.DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                    AdminNavigationTab.DEPOSITS -> AdminDepositsScreen(viewModel = viewModel)
                    AdminNavigationTab.PRODUCTS -> AdminProductsScreen(viewModel = viewModel)
                    AdminNavigationTab.USERS -> AdminUsersScreen(viewModel = viewModel)
                    AdminNavigationTab.SETTINGS -> AdminSettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AdminTabPill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    badge: String?,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) NeonPurple.copy(alpha = 0.25f) else CyberSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NeonPurple else CyberSurfaceBorder
        ),
        modifier = Modifier.testTag("admin_tab_$title")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) NeonPurple else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )

            if (badge != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(NeonAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        color = CyberVoid,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
