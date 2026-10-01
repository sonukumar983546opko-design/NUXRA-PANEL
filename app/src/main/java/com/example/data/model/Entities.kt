package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String = "CUSTOMER", // "CUSTOMER", "ADMIN"
    val walletBalance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val isBanned: Boolean = false
)

@Entity(tableName = "deposits")
data class DepositEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val username: String,
    val amount: Double,
    val utrNumber: String,
    val paymentMethod: String = "UPI",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val game: String,
    val category: String, // e.g., "ESP & AIMBOT", "BYPASS", "INJECTOR", "CONFIG"
    val description: String,
    val iconUrl: String = "",
    val demoVideoUrl: String = "",
    val status: String = "ACTIVE", // "ACTIVE", "MAINTENANCE"
    val keyDeliveryType: String = "INSTANT_GEN", // "INVENTORY_STOCK", "EXTERNAL_API", "INSTANT_GEN"
    val externalApiUrl: String? = null
)

@Entity(
    tableName = "product_pricing",
    indices = [Index(value = ["productId", "durationDays"])]
)
data class ProductPricingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val durationDays: Int, // 1, 2, 3, 7, 30
    val durationLabel: String, // "1 Day", "3 Days", "7 Days", "30 Days"
    val priceInInr: Double,
    val isPopular: Boolean = false
)

@Entity(tableName = "license_key_stock")
data class LicenseKeyStockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val durationDays: Int,
    val licenseKey: String,
    val isUsed: Boolean = false,
    val orderId: Long? = null
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val username: String,
    val productId: Long,
    val productName: String,
    val game: String,
    val durationDays: Int,
    val durationLabel: String,
    val pricePaid: Double,
    val licenseKey: String,
    val status: String = "ACTIVE", // "ACTIVE", "EXPIRED", "REVOKED"
    val purchasedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val username: String,
    val type: String, // "DEPOSIT", "PURCHASE", "ADMIN_ADJUSTMENT", "REFUND"
    val amount: Double,
    val balanceAfter: Double,
    val description: String,
    val referenceId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_settings")
data class SystemSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val upiId: String = "nexuspanel@upi",
    val payeeName: String = "NEXUS PANEL OFFICIAL",
    val minDeposit: Double = 10.0,
    val telegramChannel: String = "https://t.me/nexuspanel_official",
    val telegramGroup: String = "https://t.me/nexuspanel_community",
    val telegramSupport: String = "https://t.me/nexus_support_bot",
    val announcement: String = "⚡ FLASH SALE: VIP Keys active with instant delivery. Safe & Anti-Ban v3!",
    val externalApiBaseUrl: String = "https://api.nexuspanel.cloud/v1/dispatch",
    val externalApiKeySecret: String = "nexus_sec_live_9482759384",
    val autoApproveApiKeys: Boolean = true
)
