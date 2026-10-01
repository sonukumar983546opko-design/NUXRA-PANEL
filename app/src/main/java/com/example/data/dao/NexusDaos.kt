package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.DepositEntity
import com.example.data.model.LicenseKeyStockEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ProductPricingEntity
import com.example.data.model.SystemSettingsEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("UPDATE users SET walletBalance = :newBalance WHERE id = :userId")
    suspend fun updateBalance(userId: Long, newBalance: Double)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun setBanned(userId: Long, isBanned: Boolean)

    @Query("UPDATE users SET role = :newRole WHERE id = :userId")
    suspend fun updateRole(userId: Long, newRole: String)

    @Query("SELECT COUNT(*) FROM users")
    fun countUsers(): Flow<Int>
}

@Dao
interface DepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositEntity): Long

    @Query("SELECT * FROM deposits ORDER BY createdAt DESC")
    fun getAllDeposits(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getDepositsByUser(userId: Long): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingDeposits(): Flow<List<DepositEntity>>

    @Query("SELECT COUNT(*) FROM deposits WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT * FROM deposits WHERE id = :id")
    suspend fun getDepositById(id: Long): DepositEntity?

    @Query("UPDATE deposits SET status = :status, rejectionReason = :reason, reviewedAt = :reviewedAt WHERE id = :depositId")
    suspend fun updateStatus(depositId: Long, status: String, reason: String?, reviewedAt: Long)
}

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: Long)

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    // Pricing
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPricing(pricing: ProductPricingEntity): Long

    @Query("SELECT * FROM product_pricing WHERE productId = :productId ORDER BY durationDays ASC")
    fun getPricingForProduct(productId: Long): Flow<List<ProductPricingEntity>>

    @Query("SELECT * FROM product_pricing ORDER BY productId ASC, durationDays ASC")
    fun getAllPricing(): Flow<List<ProductPricingEntity>>

    @Query("DELETE FROM product_pricing WHERE productId = :productId")
    suspend fun deletePricingForProduct(productId: Long)

    // Key Stock
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyStock(key: LicenseKeyStockEntity): Long

    @Query("SELECT * FROM license_key_stock WHERE productId = :productId AND durationDays = :durationDays AND isUsed = 0 LIMIT 1")
    suspend fun getUnusedKey(productId: Long, durationDays: Int): LicenseKeyStockEntity?

    @Query("UPDATE license_key_stock SET isUsed = 1, orderId = :orderId WHERE id = :keyId")
    suspend fun markKeyUsed(keyId: Long, orderId: Long)

    @Query("SELECT COUNT(*) FROM license_key_stock WHERE productId = :productId AND isUsed = 0")
    fun countKeysInStock(productId: Long): Flow<Int>

    @Query("SELECT * FROM license_key_stock WHERE productId = :productId ORDER BY id DESC")
    fun getStockForProduct(productId: Long): Flow<List<LicenseKeyStockEntity>>
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY purchasedAt DESC")
    fun getOrdersByUser(userId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY purchasedAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("SELECT COUNT(*) FROM orders")
    fun countOrders(): Flow<Int>

    @Query("SELECT SUM(pricePaid) FROM orders")
    fun getTotalRevenue(): Flow<Double?>
}

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsByUser(userId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
}

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: SystemSettingsEntity)

    @Query("SELECT * FROM system_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SystemSettingsEntity?>

    @Query("SELECT * FROM system_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SystemSettingsEntity?
}
