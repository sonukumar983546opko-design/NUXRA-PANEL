package com.example.ui.screens.admin

import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.NexusViewModel
import com.example.ui.components.CyberTextField
import com.example.ui.components.NeonButton
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
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
fun AdminProductsScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val allPricing by viewModel.allPricing.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showStockKeyDialog by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRODUCTS & PRICING",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${products.size} Active Products in Store",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    color = NeonCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier.testTag("admin_add_product_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ ADD PRODUCT", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(products, key = { it.id }) { product ->
            val productPricings = allPricing.filter { it.productId == product.id }.sortedBy { it.durationDays }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = NeonCyan.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = product.game.uppercase(),
                                    color = NeonCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = product.name,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                onClick = { showStockKeyDialog = product },
                                shape = RoundedCornerShape(6.dp),
                                color = CyberSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ KEY", color = NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(onClick = { productToDelete = product }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Configured Durations & Pricing:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        productPricings.forEach { p ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyberSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
                            ) {
                                Text(
                                    text = "${p.durationLabel} = ₹${p.priceInInr.toInt()}",
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Product Modal
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, game, cat, desc, video, icon, pricings ->
                viewModel.addProduct(name, game, cat, desc, video, icon, pricings)
                showAddDialog = false
            }
        )
    }

    // Add Stock Key Modal
    if (showStockKeyDialog != null) {
        val targetProduct = showStockKeyDialog!!
        var keyDuration by remember { mutableStateOf("1") }
        var rawKey by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showStockKeyDialog = null },
            containerColor = CyberSurface,
            title = {
                Text("ADD LICENSE KEY TO STOCK", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Product: ${targetProduct.name}", color = TextPrimary, fontSize = 12.sp)

                    CyberTextField(
                        value = keyDuration,
                        onValueChange = { keyDuration = it },
                        label = "Duration (Days, e.g. 1, 3, 7, 30)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    CyberTextField(
                        value = rawKey,
                        onValueChange = { rawKey = it },
                        label = "License Key Code (e.g. NEXUS-BGMI-7D-XXXX)"
                    )
                }
            },
            confirmButton = {
                NeonButton(
                    text = "SAVE KEY",
                    onClick = {
                        val days = keyDuration.toIntOrNull() ?: 1
                        if (rawKey.isNotBlank()) {
                            viewModel.addStockKey(targetProduct.id, days, rawKey)
                            showStockKeyDialog = null
                        }
                    },
                    modifier = Modifier.height(40.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { showStockKeyDialog = null }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }

    // Delete Confirmation
    if (productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor = CyberSurface,
            title = { Text("DELETE PRODUCT", color = NeonRed, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${prod.name}' from store? Existing keys will remain valid.", color = TextPrimary, fontSize = 13.sp) },
            confirmButton = {
                NeonButton(
                    text = "CONFIRM DELETE",
                    gradientColors = listOf(NeonRed, Color(0xFF8B0000)),
                    onClick = {
                        viewModel.deleteProduct(prod.id)
                        productToDelete = null
                    },
                    modifier = Modifier.height(40.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("CANCEL", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, List<Pair<Int, Double>>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var game by remember { mutableStateOf("BGMI / PUBG MOBILE") }
    var category by remember { mutableStateOf("ESP & AIMBOT") }
    var description by remember { mutableStateOf("") }
    var demoVideoUrl by remember { mutableStateOf("") }
    var iconUrl by remember { mutableStateOf("") }

    // Pricing rows
    val pricingRows = remember {
        mutableStateListOf(
            Pair(1, "60"),
            Pair(3, "150"),
            Pair(7, "320"),
            Pair(30, "899")
        )
    }

    var newDurationDays by remember { mutableStateOf("") }
    var newDurationPrice by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Text("ADD NEW PRODUCT", color = NeonCyan, fontWeight = FontWeight.Black, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CyberTextField(value = name, onValueChange = { name = it }, label = "Product Name (e.g. TITAN RADAR VIP)")
                CyberTextField(value = game, onValueChange = { game = it }, label = "Game Title (e.g. BGMI, FREE FIRE, COD)")
                CyberTextField(value = category, onValueChange = { category = it }, label = "Category (e.g. BYPASS, AIMBOT, INJECTOR)")
                CyberTextField(value = description, onValueChange = { description = it }, label = "Description & Anti-ban Features", singleLine = false)
                CyberTextField(value = demoVideoUrl, onValueChange = { demoVideoUrl = it }, label = "Demo Video URL (YouTube / Streamable)")
                CyberTextField(value = iconUrl, onValueChange = { iconUrl = it }, label = "Icon / Banner Image URL (Optional)")

                Spacer(modifier = Modifier.height(4.dp))
                Text("Custom Durations & Prices:", color = NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                pricingRows.forEachIndexed { index, (days, price) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$days Day(s) = ₹$price", color = TextPrimary, fontSize = 12.sp)
                        IconButton(onClick = { pricingRows.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = NeonRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Add custom duration row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newDurationDays,
                        onValueChange = { newDurationDays = it },
                        placeholder = { Text("Days", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberSurfaceBorder
                        )
                    )
                    OutlinedTextField(
                        value = newDurationPrice,
                        onValueChange = { newDurationPrice = it },
                        placeholder = { Text("₹ Price", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberSurfaceBorder
                        )
                    )
                    Surface(
                        onClick = {
                            val d = newDurationDays.toIntOrNull()
                            val p = newDurationPrice.toDoubleOrNull()
                            if (d != null && p != null) {
                                pricingRows.add(Pair(d, p.toInt().toString()))
                                newDurationDays = ""
                                newDurationPrice = ""
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = NeonCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                            Text("+ ADD", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "CREATE PRODUCT",
                onClick = {
                    if (name.isNotBlank()) {
                        val parsed = pricingRows.mapNotNull { (days, pStr) ->
                            pStr.toDoubleOrNull()?.let { Pair(days, it) }
                        }
                        onAdd(name, game, category, description, demoVideoUrl, iconUrl, parsed)
                    }
                },
                modifier = Modifier.height(42.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}
