package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AuthResult
import com.example.data.DatabaseSeeder
import com.example.data.NexusRepository
import com.example.data.PurchaseResult
import com.example.data.model.DepositEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ProductPricingEntity
import com.example.data.model.SystemSettingsEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavigationTab {
    STORE, WALLET, ORDERS, PROFILE
}

enum class AdminNavigationTab {
    DASHBOARD, DEPOSITS, PRODUCTS, USERS, SETTINGS
}

data class PurchaseSuccessEvent(
    val order: OrderEntity,
    val licenseKey: String
)

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = NexusRepository(db)

    // Current User
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Navigation State
    private val _currentTab = MutableStateFlow(MainNavigationTab.STORE)
    val currentTab: StateFlow<MainNavigationTab> = _currentTab.asStateFlow()

    private val _adminTab = MutableStateFlow(AdminNavigationTab.DASHBOARD)
    val adminTab: StateFlow<AdminNavigationTab> = _adminTab.asStateFlow()

    private val _isAdminScreenActive = MutableStateFlow(false)
    val isAdminScreenActive: StateFlow<Boolean> = _isAdminScreenActive.asStateFlow()

    // UI Feedback
    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    private val _purchaseSuccess = MutableSharedFlow<PurchaseSuccessEvent>()
    val purchaseSuccess: SharedFlow<PurchaseSuccessEvent> = _purchaseSuccess.asSharedFlow()

    val settings: StateFlow<SystemSettingsEntity?> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SystemSettingsEntity()
    )

    val products: StateFlow<List<ProductEntity>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allPricing: StateFlow<List<ProductPricingEntity>> = repository.allPricing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pendingDepositsCount: StateFlow<Int> = repository.pendingDepositsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // User specific streams
    val userOrders: StateFlow<List<OrderEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getOrdersForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userDeposits: StateFlow<List<DepositEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getDepositsForUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin streams
    val allDeposits: StateFlow<List<DepositEntity>> = repository.getAllDeposits().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val pendingDeposits: StateFlow<List<DepositEntity>> = repository.getPendingDeposits().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrders().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val totalUsersCount: StateFlow<Int> = repository.getUserCount().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val totalOrdersCount: StateFlow<Int> = repository.getOrderCount().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val totalRevenue: StateFlow<Double?> = repository.getTotalRevenue().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0
    )

    init {
        viewModelScope.launch {
            DatabaseSeeder.seedDatabaseIfEmpty(db)
            // Pre-select demo customer by default so app can be tested immediately
            val demoUser = repository.login("gamer_pro", "gamer123")
            if (demoUser is AuthResult.Success) {
                _currentUser.value = demoUser.user
            }
        }
    }

    fun selectTab(tab: MainNavigationTab) {
        _currentTab.value = tab
    }

    fun selectAdminTab(tab: AdminNavigationTab) {
        _adminTab.value = tab
    }

    fun setAdminScreenActive(active: Boolean) {
        _isAdminScreenActive.value = active
    }

    fun refreshCurrentUser() {
        val uid = _currentUser.value?.id ?: return
        viewModelScope.launch {
            val updated = repository.getUserById(uid)
            if (updated != null) {
                _currentUser.value = updated
            }
        }
    }

    // ----------------- AUTH -----------------
    fun login(usernameOrEmail: String, rawPass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            when (val res = repository.login(usernameOrEmail, rawPass)) {
                is AuthResult.Success -> {
                    _currentUser.value = res.user
                    _isAdminScreenActive.value = (res.user.role == "ADMIN")
                    onResult(true, null)
                }
                is AuthResult.Error -> {
                    onResult(false, res.message)
                }
            }
        }
    }

    fun register(username: String, email: String, rawPass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            when (val res = repository.register(username, email, rawPass)) {
                is AuthResult.Success -> {
                    _currentUser.value = res.user
                    _isAdminScreenActive.value = false
                    onResult(true, null)
                }
                is AuthResult.Error -> {
                    onResult(false, res.message)
                }
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _isAdminScreenActive.value = false
        _currentTab.value = MainNavigationTab.STORE
    }

    // ----------------- DEPOSITS -----------------
    fun submitDeposit(amount: Double, utr: String, onComplete: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.submitDeposit(user.id, user.username, amount, utr)
            if (result.isSuccess) {
                refreshCurrentUser()
                onComplete(true, "Deposit submitted! Admin will verify UTR and credit your wallet.")
            } else {
                onComplete(false, result.exceptionOrNull()?.message ?: "Failed to submit deposit")
            }
        }
    }

    // ----------------- PURCHASES -----------------
    fun purchaseProduct(
        productId: Long,
        pricing: ProductPricingEntity,
        onComplete: (PurchaseResult) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.purchaseProduct(user.id, productId, pricing)
            if (result is PurchaseResult.Success) {
                refreshCurrentUser()
                _purchaseSuccess.emit(PurchaseSuccessEvent(result.order, result.licenseKey))
            }
            onComplete(result)
        }
    }

    // ----------------- ADMIN ACTIONS -----------------
    fun approveDeposit(depositId: Long) {
        viewModelScope.launch {
            val res = repository.approveDeposit(depositId)
            if (res.isSuccess) {
                _uiMessage.emit("Deposit approved and credited to user wallet.")
                refreshCurrentUser()
            } else {
                _uiMessage.emit(res.exceptionOrNull()?.message ?: "Approval failed")
            }
        }
    }

    fun rejectDeposit(depositId: Long, reason: String) {
        viewModelScope.launch {
            val res = repository.rejectDeposit(depositId, reason)
            if (res.isSuccess) {
                _uiMessage.emit("Deposit marked as rejected.")
            } else {
                _uiMessage.emit(res.exceptionOrNull()?.message ?: "Rejection failed")
            }
        }
    }

    fun addProduct(
        name: String,
        game: String,
        category: String,
        description: String,
        demoVideoUrl: String,
        iconUrl: String,
        pricingList: List<Pair<Int, Double>> // List of (days to price)
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                name = name,
                game = game,
                category = category,
                description = description,
                demoVideoUrl = demoVideoUrl,
                iconUrl = iconUrl,
                status = "ACTIVE",
                keyDeliveryType = "INSTANT_GEN"
            )
            val pricings = pricingList.map { (days, price) ->
                ProductPricingEntity(
                    productId = 0,
                    durationDays = days,
                    durationLabel = "$days Day${if (days > 1) "s" else ""}",
                    priceInInr = price
                )
            }
            repository.addProductWithPricing(product, pricings)
            _uiMessage.emit("Product '$name' added successfully!")
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            _uiMessage.emit("Product deleted.")
        }
    }

    fun addStockKey(productId: Long, durationDays: Int, key: String) {
        viewModelScope.launch {
            repository.addStockKey(productId, durationDays, key)
            _uiMessage.emit("Key added to stock.")
        }
    }

    fun adjustUserBalance(userId: Long, newBalance: Double, reason: String) {
        viewModelScope.launch {
            repository.adjustUserBalance(userId, newBalance, reason)
            refreshCurrentUser()
            _uiMessage.emit("User balance updated to ₹${newBalance.toInt()}.")
        }
    }

    fun toggleUserBan(userId: Long, isBanned: Boolean) {
        viewModelScope.launch {
            repository.toggleUserBan(userId, isBanned)
            _uiMessage.emit(if (isBanned) "User has been banned." else "User unbanned.")
        }
    }

    fun updateSettings(settings: SystemSettingsEntity) {
        viewModelScope.launch {
            repository.updateSystemSettings(settings)
            _uiMessage.emit("System settings updated successfully.")
        }
    }
}
