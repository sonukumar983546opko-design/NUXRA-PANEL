package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainNavigationTab
import com.example.ui.NexusViewModel
import com.example.ui.components.CyberBackgroundBox
import com.example.ui.components.CyberTopHeader
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.admin.AdminRootScreen
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NexusApp()
            }
        }
    }
}

@Composable
fun NexusApp(viewModel: NexusViewModel = viewModel()) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdminActive by viewModel.isAdminScreenActive.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()

    // Listen to UI messages
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else if (isAdminActive && currentUser?.role == "ADMIN") {
        AdminRootScreen(viewModel = viewModel)
    } else {
        CyberBackgroundBox {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CyberTopHeader(
                        walletBalance = currentUser?.walletBalance ?: 0.0,
                        username = currentUser?.username ?: "",
                        role = currentUser?.role ?: "CUSTOMER",
                        onWalletClick = { viewModel.selectTab(MainNavigationTab.WALLET) },
                        modifier = Modifier.statusBarsPadding()
                    )
                },
                bottomBar = {
                    NexusBottomNavigation(
                        currentTab = currentTab,
                        onTabSelect = { viewModel.selectTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "MainScreenNavigation"
                    ) { tab ->
                        when (tab) {
                            MainNavigationTab.STORE -> StoreScreen(viewModel = viewModel)
                            MainNavigationTab.WALLET -> WalletScreen(viewModel = viewModel)
                            MainNavigationTab.ORDERS -> OrdersScreen(viewModel = viewModel)
                            MainNavigationTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NexusBottomNavigation(
    currentTab: MainNavigationTab,
    onTabSelect: (MainNavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = CyberSurface.copy(alpha = 0.96f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                title = "Store",
                icon = Icons.Default.ShoppingBag,
                isSelected = currentTab == MainNavigationTab.STORE,
                onClick = { onTabSelect(MainNavigationTab.STORE) },
                testTag = "nav_store"
            )

            BottomNavItem(
                title = "Wallet",
                icon = Icons.Default.AccountBalanceWallet,
                isSelected = currentTab == MainNavigationTab.WALLET,
                onClick = { onTabSelect(MainNavigationTab.WALLET) },
                testTag = "nav_wallet"
            )

            BottomNavItem(
                title = "My Keys",
                icon = Icons.Default.Key,
                isSelected = currentTab == MainNavigationTab.ORDERS,
                onClick = { onTabSelect(MainNavigationTab.ORDERS) },
                testTag = "nav_orders"
            )

            BottomNavItem(
                title = "Profile",
                icon = Icons.Default.Person,
                isSelected = currentTab == MainNavigationTab.PROFILE,
                onClick = { onTabSelect(MainNavigationTab.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent,
        modifier = Modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) NeonCyan else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                color = if (isSelected) NeonCyan else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
