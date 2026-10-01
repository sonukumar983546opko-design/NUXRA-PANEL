package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PurchaseResult
import com.example.data.model.ProductEntity
import com.example.data.model.ProductPricingEntity
import com.example.ui.MainNavigationTab
import com.example.ui.NexusViewModel
import com.example.ui.components.AnnouncementBanner
import com.example.ui.components.CopyKeyWidget
import com.example.ui.components.GlowingCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.CyberBackground
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoreScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val allPricing by viewModel.allPricing.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    // Purchase Dialog States
    var pendingPurchaseProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var pendingPricing by remember { mutableStateOf<ProductPricingEntity?>(null) }
    var isPurchasing by remember { mutableStateOf(false) }
    var purchaseErrorMessage by remember { mutableStateOf<String?>(null) }
    var purchaseShortage by remember { mutableStateOf<Double?>(null) }

    // Success Key Dialog
    var purchasedKey by remember { mutableStateOf<String?>(null) }
    var purchasedProductName by remember { mutableStateOf("") }

    val categories = listOf("ALL", "BGMI / PUBG", "FREE FIRE", "COD MOBILE", "OTHER")

    val filteredProducts = products.filter { p ->
        val matchesSearch = p.name.contains(searchQuery, ignoreCase = true) ||
                p.game.contains(searchQuery, ignoreCase = true) ||
                p.category.contains(searchQuery, ignoreCase = true)
        val matchesCat = when (selectedCategory) {
            "ALL" -> true
            "BGMI / PUBG" -> p.game.contains("BGMI", ignoreCase = true) || p.game.contains("PUBG", ignoreCase = true)
            "FREE FIRE" -> p.game.contains("FREE FIRE", ignoreCase = true)
            "COD MOBILE" -> p.game.contains("COD", ignoreCase = true) || p.game.contains("CALL OF DUTY", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesCat
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            settings?.announcement?.let {
                AnnouncementBanner(text = it)
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search hacks, games, VIP tools...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("store_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
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

        // Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonCyan else CyberSurfaceBorder
                        ),
                        modifier = Modifier.testTag("filter_$cat")
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) NeonCyan else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No products found", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        }

        items(filteredProducts, key = { it.id }) { product ->
            val productPricings = allPricing.filter { it.productId == product.id }.sortedBy { it.durationDays }
            var selectedPricingId by remember(product.id, productPricings) {
                mutableLongStateOf(productPricings.firstOrNull()?.id ?: 0L)
            }
            val activePricing = productPricings.firstOrNull { it.id == selectedPricingId }
                ?: productPricings.firstOrNull()

            ProductStoreCard(
                product = product,
                pricings = productPricings,
                selectedPricing = activePricing,
                onSelectPricing = { selectedPricingId = it.id },
                onBuyClick = {
                    if (activePricing != null) {
                        pendingPurchaseProduct = product
                        pendingPricing = activePricing
                        purchaseErrorMessage = null
                        purchaseShortage = null
                    }
                },
                onDemoVideoClick = {
                    if (product.demoVideoUrl.isNotBlank()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.demoVideoUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open demo video link", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "No demo video available for this item", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Purchase Confirmation Dialog
    if (pendingPurchaseProduct != null && pendingPricing != null) {
        val prod = pendingPurchaseProduct!!
        val pricing = pendingPricing!!
        val userBalance = currentUser?.walletBalance ?: 0.0
        val isInsufficient = userBalance < pricing.priceInInr

        AlertDialog(
            onDismissRequest = {
                if (!isPurchasing) {
                    pendingPurchaseProduct = null
                    pendingPricing = null
                }
            },
            containerColor = CyberSurface,
            title = {
                Text(
                    text = "CONFIRM PURCHASE",
                    color = NeonCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = prod.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Game: ${prod.game}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Surface(
                        color = CyberSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Duration", color = TextMuted, fontSize = 11.sp)
                                Text(pricing.durationLabel, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Amount", color = TextMuted, fontSize = 11.sp)
                                Text("₹${pricing.priceInInr.toInt()}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Balance:", color = TextSecondary, fontSize = 13.sp)
                        Text("₹${userBalance.toInt()}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    if (isInsufficient) {
                        val shortage = pricing.priceInInr - userBalance
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NeonRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, NeonRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "⚠️ Insufficient Wallet Balance",
                                    color = NeonRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "You need ₹${shortage.toInt()} more to complete this order.",
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    if (purchaseErrorMessage != null) {
                        Text(
                            text = purchaseErrorMessage!!,
                            color = NeonRed,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                if (isInsufficient) {
                    NeonButton(
                        text = "DEPOSIT ₹${(pricing.priceInInr - userBalance).toInt()} VIA UPI",
                        onClick = {
                            pendingPurchaseProduct = null
                            pendingPricing = null
                            viewModel.selectTab(MainNavigationTab.WALLET)
                        },
                        gradientColors = listOf(NeonAmber, NeonPink),
                        modifier = Modifier.height(44.dp),
                        testTag = "dialog_go_to_deposit"
                    )
                } else {
                    NeonButton(
                        text = "CONFIRM & GET KEY",
                        isLoading = isPurchasing,
                        onClick = {
                            isPurchasing = true
                            purchaseErrorMessage = null
                            viewModel.purchaseProduct(prod.id, pricing) { result ->
                                isPurchasing = false
                                when (result) {
                                    is PurchaseResult.Success -> {
                                        purchasedKey = result.licenseKey
                                        purchasedProductName = prod.name
                                        pendingPurchaseProduct = null
                                        pendingPricing = null
                                    }
                                    is PurchaseResult.Error -> {
                                        purchaseErrorMessage = result.message
                                        purchaseShortage = result.requiredAmount
                                    }
                                }
                            }
                        },
                        modifier = Modifier.height(44.dp),
                        testTag = "dialog_confirm_purchase"
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingPurchaseProduct = null
                        pendingPricing = null
                    },
                    enabled = !isPurchasing
                ) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }

    // Purchase Success Key Dialog
    if (purchasedKey != null) {
        AlertDialog(
            onDismissRequest = { purchasedKey = null },
            containerColor = CyberSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KEY DELIVERED!", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Order successful for $purchasedProductName. Your key is activated and ready to inject.",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    CopyKeyWidget(licenseKey = purchasedKey!!)
                    Text(
                        text = "Saved in 'My Keys' tab with live expiry countdown.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                NeonButton(
                    text = "VIEW IN MY KEYS",
                    onClick = {
                        purchasedKey = null
                        viewModel.selectTab(MainNavigationTab.ORDERS)
                    },
                    modifier = Modifier.height(42.dp),
                    testTag = "dialog_view_in_my_keys"
                )
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductStoreCard(
    product: ProductEntity,
    pricings: List<ProductPricingEntity>,
    selectedPricing: ProductPricingEntity?,
    onSelectPricing: (ProductPricingEntity) -> Unit,
    onBuyClick: () -> Unit,
    onDemoVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NeonPurple.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header: Category, Status, Demo Video
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = NeonCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = product.game.uppercase(),
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = NeonPurple.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = product.category,
                            color = NeonPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                if (product.demoVideoUrl.isNotBlank()) {
                    Surface(
                        onClick = onDemoVideoClick,
                        color = Color(0xFF26173D),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.6f)),
                        modifier = Modifier.testTag("demo_video_button_${product.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Demo Video",
                                tint = NeonPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DEMO", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product Title
            Text(
                text = product.name,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = product.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Duration & Pricing selector
            Text(
                text = "SELECT DURATION & PRICING",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pricings.forEach { p ->
                    val isSelected = selectedPricing?.id == p.id
                    Surface(
                        onClick = { onSelectPricing(p) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) NeonCyan.copy(alpha = 0.15f) else CyberSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) NeonCyan else CyberSurfaceBorder
                        ),
                        modifier = Modifier.testTag("duration_chip_${product.id}_${p.durationDays}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = p.durationLabel,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${p.priceInInr.toInt()}",
                                color = if (isSelected) NeonCyan else NeonGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Buy Now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Price",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "₹${selectedPricing?.priceInInr?.toInt() ?: 0}",
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                NeonButton(
                    text = "BUY KEY NOW",
                    icon = Icons.Default.ElectricBolt,
                    onClick = onBuyClick,
                    modifier = Modifier.width(180.dp).height(46.dp),
                    testTag = "buy_button_${product.id}"
                )
            }
        }
    }
}
