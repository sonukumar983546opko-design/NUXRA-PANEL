package com.example.data

import com.example.data.model.DepositEntity
import com.example.data.model.LicenseKeyStockEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ProductPricingEntity
import com.example.data.model.SystemSettingsEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class PurchaseResult {
    data class Success(val order: OrderEntity, val licenseKey: String) : PurchaseResult()
    data class Error(val message: String, val requiredAmount: Double? = null) : PurchaseResult()
}

class NexusRepository(private val db: AppDatabase) {

    val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val allPricing: Flow<List<ProductPricingEntity>> = db.productDao().getAllPricing()
    val pendingDepositsCount: Flow<Int> = db.depositDao().getPendingCount()
    val settingsFlow: Flow<SystemSettingsEntity?> = db.settingsDao().getSettingsFlow()

    fun getPricingForProduct(productId: Long): Flow<List<ProductPricingEntity>> =
        db.productDao().getPricingForProduct(productId)

    fun getOrdersForUser(userId: Long): Flow<List<OrderEntity>> =
        db.orderDao().getOrdersByUser(userId)

    fun getAllOrders(): Flow<List<OrderEntity>> = db.orderDao().getAllOrders()

    fun getDepositsForUser(userId: Long): Flow<List<DepositEntity>> =
        db.depositDao().getDepositsByUser(userId)

    fun getAllDeposits(): Flow<List<DepositEntity>> = db.depositDao().getAllDeposits()

    fun getPendingDeposits(): Flow<List<DepositEntity>> = db.depositDao().getPendingDeposits()

    fun getTransactionsForUser(userId: Long): Flow<List<TransactionEntity>> =
        db.transactionDao().getTransactionsByUser(userId)

    fun getAllTransactions(): Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()

    fun getAllUsers(): Flow<List<UserEntity>> = db.userDao().getAllUsers()

    fun getUserCount(): Flow<Int> = db.userDao().countUsers()

    fun getOrderCount(): Flow<Int> = db.orderDao().countOrders()

    fun getTotalRevenue(): Flow<Double?> = db.orderDao().getTotalRevenue()

    // ---------------- AUTHENTICATION ----------------
    suspend fun login(usernameOrEmail: String, rawPassword: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmed = usernameOrEmail.trim()
        val user = if (trimmed.contains("@")) {
            db.userDao().getUserByEmail(trimmed.lowercase())
        } else {
            db.userDao().getUserByUsername(trimmed)
        } ?: return@withContext AuthResult.Error("No user found with '$trimmed'")

        if (user.isBanned) {
            return@withContext AuthResult.Error("Your account has been suspended by Admin.")
        }

        val inputHash = DatabaseSeeder.hashPassword(rawPassword)
        if (user.passwordHash != inputHash) {
            return@withContext AuthResult.Error("Incorrect password. Please try again.")
        }

        AuthResult.Success(user)
    }

    suspend fun register(username: String, email: String, rawPassword: String): AuthResult = withContext(Dispatchers.IO) {
        val cleanUser = username.trim()
        val cleanEmail = email.trim().lowercase()

        if (cleanUser.length < 3) {
            return@withContext AuthResult.Error("Username must be at least 3 characters")
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return@withContext AuthResult.Error("Invalid email address format")
        }
        if (rawPassword.length < 5) {
            return@withContext AuthResult.Error("Password must be at least 5 characters")
        }

        if (db.userDao().getUserByUsername(cleanUser) != null) {
            return@withContext AuthResult.Error("Username '$cleanUser' is already taken")
        }
        if (db.userDao().getUserByEmail(cleanEmail) != null) {
            return@withContext AuthResult.Error("Email '$cleanEmail' is already registered")
        }

        val newUser = UserEntity(
            username = cleanUser,
            email = cleanEmail,
            passwordHash = DatabaseSeeder.hashPassword(rawPassword),
            role = "CUSTOMER",
            walletBalance = 0.0,
            createdAt = System.currentTimeMillis()
        )
        val newId = db.userDao().insertUser(newUser)
        val created = db.userDao().getUserById(newId)!!
        AuthResult.Success(created)
    }

    suspend fun getUserById(userId: Long): UserEntity? = withContext(Dispatchers.IO) {
        db.userDao().getUserById(userId)
    }

    // ---------------- DEPOSITS ----------------
    suspend fun submitDeposit(userId: Long, username: String, amount: Double, utrNumber: String): Result<DepositEntity> = withContext(Dispatchers.IO) {
        val settings = db.settingsDao().getSettings() ?: SystemSettingsEntity()
        if (amount < settings.minDeposit) {
            return@withContext Result.failure(IllegalArgumentException("Minimum deposit amount is ₹${settings.minDeposit.toInt()}"))
        }
        val cleanUtr = utrNumber.trim()
        if (cleanUtr.length < 8) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid 12-digit UPI UTR / Ref number"))
        }

        val deposit = DepositEntity(
            userId = userId,
            username = username,
            amount = amount,
            utrNumber = cleanUtr,
            status = "PENDING",
            createdAt = System.currentTimeMillis()
        )
        val id = db.depositDao().insertDeposit(deposit)
        Result.success(deposit.copy(id = id))
    }

    suspend fun approveDeposit(depositId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val deposit = db.depositDao().getDepositById(depositId)
            ?: return@withContext Result.failure(IllegalStateException("Deposit not found"))

        if (deposit.status != "PENDING") {
            return@withContext Result.failure(IllegalStateException("Deposit is already ${deposit.status}"))
        }

        val user = db.userDao().getUserById(deposit.userId)
            ?: return@withContext Result.failure(IllegalStateException("User not found"))

        val newBalance = user.walletBalance + deposit.amount
        db.userDao().updateBalance(user.id, newBalance)
        db.depositDao().updateStatus(depositId, "APPROVED", null, System.currentTimeMillis())

        db.transactionDao().insertTransaction(
            TransactionEntity(
                userId = user.id,
                username = user.username,
                type = "DEPOSIT",
                amount = deposit.amount,
                balanceAfter = newBalance,
                description = "Approved Deposit (UTR: ${deposit.utrNumber})",
                referenceId = "DEP-$depositId"
            )
        )
        Result.success(Unit)
    }

    suspend fun rejectDeposit(depositId: Long, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        val deposit = db.depositDao().getDepositById(depositId)
            ?: return@withContext Result.failure(IllegalStateException("Deposit not found"))

        db.depositDao().updateStatus(depositId, "REJECTED", reason.ifBlank { "Invalid UTR / Payment Not Received" }, System.currentTimeMillis())
        Result.success(Unit)
    }

    // ---------------- STORE & PURCHASES ----------------
    suspend fun purchaseProduct(
        userId: Long,
        productId: Long,
        pricing: ProductPricingEntity
    ): PurchaseResult = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(userId)
            ?: return@withContext PurchaseResult.Error("User session expired. Please re-login.")

        if (user.isBanned) {
            return@withContext PurchaseResult.Error("Account is suspended.")
        }

        if (user.walletBalance < pricing.priceInInr) {
            val shortage = pricing.priceInInr - user.walletBalance
            return@withContext PurchaseResult.Error(
                message = "Insufficient wallet balance! Needed: ₹${pricing.priceInInr.toInt()}, Current: ₹${user.walletBalance.toInt()}. Add ₹${shortage.toInt()} to continue.",
                requiredAmount = shortage
            )
        }

        val product = db.productDao().getProductById(productId)
            ?: return@withContext PurchaseResult.Error("Product is currently unavailable.")

        if (product.status != "ACTIVE") {
            return@withContext PurchaseResult.Error("Product is currently under maintenance.")
        }

        // 1. Deliver Key (from stock or instant generator/API)
        val unusedKey = db.productDao().getUnusedKey(productId, pricing.durationDays)
        val licenseKeyString: String
        val keyStockId: Long?

        if (unusedKey != null) {
            licenseKeyString = unusedKey.licenseKey
            keyStockId = unusedKey.id
        } else {
            // Generate clean secure product license key
            val gamePrefix = product.game.take(4).uppercase().replace(" ", "").filter { it.isLetter() }.padEnd(4, 'X')
            val randomToken = UUID.randomUUID().toString().take(8).uppercase()
            val token2 = UUID.randomUUID().toString().take(4).uppercase()
            licenseKeyString = "NEXUS-$gamePrefix-${pricing.durationDays}D-$randomToken-$token2"
            keyStockId = null
        }

        val durationMillis = pricing.durationDays.toLong() * 24L * 60L * 60L * 1000L
        val now = System.currentTimeMillis()
        val expiresAt = now + durationMillis

        // Deduct balance
        val newBalance = user.walletBalance - pricing.priceInInr
        db.userDao().updateBalance(userId, newBalance)

        // Create Order
        val order = OrderEntity(
            userId = user.id,
            username = user.username,
            productId = product.id,
            productName = product.name,
            game = product.game,
            durationDays = pricing.durationDays,
            durationLabel = pricing.durationLabel,
            pricePaid = pricing.priceInInr,
            licenseKey = licenseKeyString,
            status = "ACTIVE",
            purchasedAt = now,
            expiresAt = expiresAt
        )
        val orderId = db.orderDao().insertOrder(order)

        if (keyStockId != null) {
            db.productDao().markKeyUsed(keyStockId, orderId)
        }

        // Record Transaction
        db.transactionDao().insertTransaction(
            TransactionEntity(
                userId = user.id,
                username = user.username,
                type = "PURCHASE",
                amount = -pricing.priceInInr,
                balanceAfter = newBalance,
                description = "Purchased ${product.name} (${pricing.durationLabel})",
                referenceId = "ORD-$orderId"
            )
        )

        PurchaseResult.Success(order.copy(id = orderId), licenseKeyString)
    }

    // ---------------- ADMIN MANAGEMENT ----------------
    suspend fun addProductWithPricing(
        product: ProductEntity,
        pricings: List<ProductPricingEntity>
    ): Long = withContext(Dispatchers.IO) {
        val prodId = db.productDao().insertProduct(product)
        for (pricing in pricings) {
            db.productDao().insertPricing(pricing.copy(productId = prodId))
        }
        prodId
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        db.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(productId: Long) = withContext(Dispatchers.IO) {
        db.productDao().deleteProduct(productId)
        db.productDao().deletePricingForProduct(productId)
    }

    suspend fun setPricingForProduct(productId: Long, pricings: List<ProductPricingEntity>) = withContext(Dispatchers.IO) {
        db.productDao().deletePricingForProduct(productId)
        for (p in pricings) {
            db.productDao().insertPricing(p.copy(productId = productId))
        }
    }

    suspend fun addStockKey(productId: Long, durationDays: Int, key: String) = withContext(Dispatchers.IO) {
        db.productDao().insertKeyStock(
            LicenseKeyStockEntity(
                productId = productId,
                durationDays = durationDays,
                licenseKey = key.trim(),
                isUsed = false
            )
        )
    }

    suspend fun adjustUserBalance(userId: Long, newBalance: Double, reason: String) = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(userId) ?: return@withContext
        val diff = newBalance - user.walletBalance
        db.userDao().updateBalance(userId, newBalance)
        db.transactionDao().insertTransaction(
            TransactionEntity(
                userId = userId,
                username = user.username,
                type = "ADMIN_ADJUSTMENT",
                amount = diff,
                balanceAfter = newBalance,
                description = "Admin Balance Adjustment: $reason",
                referenceId = "ADJ-${System.currentTimeMillis()}"
            )
        )
    }

    suspend fun toggleUserBan(userId: Long, isBanned: Boolean) = withContext(Dispatchers.IO) {
        db.userDao().setBanned(userId, isBanned)
    }

    suspend fun updateSystemSettings(settings: SystemSettingsEntity) = withContext(Dispatchers.IO) {
        db.settingsDao().insertOrUpdate(settings)
    }

    suspend fun getSettings(): SystemSettingsEntity = withContext(Dispatchers.IO) {
        db.settingsDao().getSettings() ?: SystemSettingsEntity()
    }
}
