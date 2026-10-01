package com.example.data

import com.example.data.model.DepositEntity
import com.example.data.model.LicenseKeyStockEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ProductPricingEntity
import com.example.data.model.SystemSettingsEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import java.security.MessageDigest

object DatabaseSeeder {

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) {
        val userCount = db.userDao().getUserByUsername("admin")
        if (userCount != null) return // Already seeded

        // 1. Seed System Settings
        val defaultSettings = SystemSettingsEntity(
            id = 1,
            upiId = "nexuspanel@upi",
            payeeName = "NEXUS PANEL OFFICIAL",
            minDeposit = 10.0,
            telegramChannel = "https://t.me/nexuspanel_official",
            telegramGroup = "https://t.me/nexuspanel_community",
            telegramSupport = "https://t.me/nexus_support_bot",
            announcement = "⚡ WELCOME TO NEXUS PANEL: Instant auto-delivery 24/7. Safe anti-ban v3 active!",
            externalApiBaseUrl = "https://api.nexuspanel.cloud/v1/dispatch",
            externalApiKeySecret = "nexus_live_sec_9948291048",
            autoApproveApiKeys = true
        )
        db.settingsDao().insertOrUpdate(defaultSettings)

        // 2. Seed Users: Admin & Demo Gamer
        val adminId = db.userDao().insertUser(
            UserEntity(
                username = "admin",
                email = "admin@nexuspanel.com",
                passwordHash = hashPassword("admin123"),
                role = "ADMIN",
                walletBalance = 25000.0,
                createdAt = System.currentTimeMillis() - 86400000L * 7
            )
        )

        val customerId = db.userDao().insertUser(
            UserEntity(
                username = "gamer_pro",
                email = "gamer@nexuspanel.com",
                passwordHash = hashPassword("gamer123"),
                role = "CUSTOMER",
                walletBalance = 150.0,
                createdAt = System.currentTimeMillis() - 86400000L * 3
            )
        )

        // 3. Seed Products
        val p1 = ProductEntity(
            name = "NEXUS VIP AIMBOT & RADAR",
            game = "BGMI / PUBG MOBILE",
            category = "ESP & AIMBOT",
            description = "Elite memory-safe bypass. 360° Radar, Bone Aim Lock, Magic Bullet, iPad View, 90 FPS unlocker. 100% Main ID safe with real-time server-side bypass updates.",
            iconUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=200",
            demoVideoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            status = "ACTIVE",
            keyDeliveryType = "INSTANT_GEN"
        )
        val p1Id = db.productDao().insertProduct(p1)

        val p2 = ProductEntity(
            name = "SHADOW BYPASS PRO",
            game = "FREE FIRE MAX",
            category = "BYPASS & AIM",
            description = "Auto Headshot 98%, Antenna Location, White Body Chams, Fast Medkit, Anti-Report Shield. Non-root and root devices supported.",
            iconUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=200",
            demoVideoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            status = "ACTIVE",
            keyDeliveryType = "INSTANT_GEN"
        )
        val p2Id = db.productDao().insertProduct(p2)

        val p3 = ProductEntity(
            name = "PHANTOM INJECTOR V5",
            game = "CALL OF DUTY: MOBILE",
            category = "INJECTOR",
            description = "Zero recoil, silent aimbot, wallhack ESP, fast slide & bunnyhop assist. Universal Android 10-15 compatible without crash.",
            iconUrl = "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=200",
            demoVideoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            status = "ACTIVE",
            keyDeliveryType = "INSTANT_GEN"
        )
        val p3Id = db.productDao().insertProduct(p3)

        val p4 = ProductEntity(
            name = "APEX SPEED GLIDE",
            game = "APEX LEGENDS / WARZONE",
            category = "RADAR & ESP",
            description = "Precision enemy distance, glow ESP, item filter, no bullet drop calculation, stealth memory cloaking.",
            iconUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=200",
            demoVideoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            status = "ACTIVE",
            keyDeliveryType = "INSTANT_GEN"
        )
        val p4Id = db.productDao().insertProduct(p4)

        // 4. Seed Pricing for each product
        val pricings = listOf(
            ProductPricingEntity(productId = p1Id, durationDays = 1, durationLabel = "1 Day", priceInInr = 60.0),
            ProductPricingEntity(productId = p1Id, durationDays = 3, durationLabel = "3 Days", priceInInr = 150.0),
            ProductPricingEntity(productId = p1Id, durationDays = 7, durationLabel = "7 Days", priceInInr = 320.0, isPopular = true),
            ProductPricingEntity(productId = p1Id, durationDays = 30, durationLabel = "30 Days", priceInInr = 899.0),

            ProductPricingEntity(productId = p2Id, durationDays = 1, durationLabel = "1 Day", priceInInr = 40.0),
            ProductPricingEntity(productId = p2Id, durationDays = 3, durationLabel = "3 Days", priceInInr = 99.0),
            ProductPricingEntity(productId = p2Id, durationDays = 7, durationLabel = "7 Days", priceInInr = 199.0, isPopular = true),
            ProductPricingEntity(productId = p2Id, durationDays = 30, durationLabel = "30 Days", priceInInr = 549.0),

            ProductPricingEntity(productId = p3Id, durationDays = 1, durationLabel = "1 Day", priceInInr = 80.0),
            ProductPricingEntity(productId = p3Id, durationDays = 7, durationLabel = "7 Days", priceInInr = 450.0, isPopular = true),
            ProductPricingEntity(productId = p3Id, durationDays = 30, durationLabel = "30 Days", priceInInr = 1199.0),

            ProductPricingEntity(productId = p4Id, durationDays = 1, durationLabel = "1 Day", priceInInr = 90.0),
            ProductPricingEntity(productId = p4Id, durationDays = 7, durationLabel = "7 Days", priceInInr = 490.0, isPopular = true),
            ProductPricingEntity(productId = p4Id, durationDays = 30, durationLabel = "30 Days", priceInInr = 1350.0)
        )
        for (pricing in pricings) {
            db.productDao().insertPricing(pricing)
        }

        // 5. Seed License Keys in Stock
        db.productDao().insertKeyStock(LicenseKeyStockEntity(productId = p1Id, durationDays = 1, licenseKey = "NEXUS-BGMI-1D-9A28-4K12"))
        db.productDao().insertKeyStock(LicenseKeyStockEntity(productId = p1Id, durationDays = 7, licenseKey = "NEXUS-BGMI-7D-89FF-4401"))
        db.productDao().insertKeyStock(LicenseKeyStockEntity(productId = p2Id, durationDays = 1, licenseKey = "NEXUS-FFMAX-1D-33C1-998A"))
        db.productDao().insertKeyStock(LicenseKeyStockEntity(productId = p2Id, durationDays = 7, licenseKey = "NEXUS-FFMAX-7D-7721-BC41"))

        // 6. Seed Sample Deposit for Customer
        db.depositDao().insertDeposit(
            DepositEntity(
                userId = customerId,
                username = "gamer_pro",
                amount = 150.0,
                utrNumber = "428901849102",
                status = "APPROVED",
                createdAt = System.currentTimeMillis() - 86400000L * 2,
                reviewedAt = System.currentTimeMillis() - 86400000L * 2
            )
        )
        db.depositDao().insertDeposit(
            DepositEntity(
                userId = customerId,
                username = "gamer_pro",
                amount = 100.0,
                utrNumber = "519382049182",
                status = "PENDING",
                createdAt = System.currentTimeMillis() - 3600000L * 2
            )
        )

        // Seed Transaction
        db.transactionDao().insertTransaction(
            TransactionEntity(
                userId = customerId,
                username = "gamer_pro",
                type = "DEPOSIT",
                amount = 150.0,
                balanceAfter = 150.0,
                description = "Approved Deposit via UPI UTR #428901849102",
                referenceId = "DEP-1"
            )
        )
    }
}
