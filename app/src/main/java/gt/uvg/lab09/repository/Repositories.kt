package gt.uvg.lab09.repository

import gt.uvg.lab09.model.CartItem
import gt.uvg.lab09.model.CatalogSnapshot
import gt.uvg.lab09.model.RemoteOrder
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun ensureSession(): Result<Unit>
}

interface ProductRepository {
    fun observeProducts(): Flow<CatalogSnapshot>
    suspend fun reserveUnit(productId: Int): Result<Unit>
    suspend fun reserveUnits(productId: Int, quantity: Int): Result<Unit>
    suspend fun releaseUnits(productId: Int, quantity: Int): Result<Unit>
}

interface FavoritesRepository {
    fun observeFavoriteIds(): Flow<Set<Int>>
    suspend fun toggleFavorite(productId: Int): Result<Unit>
}

interface PreferencesRepository {
    fun observeDarkTheme(): Flow<Boolean>
    suspend fun setDarkTheme(enabled: Boolean): Result<Unit>
}

interface OrdersRepository {
    suspend fun createOrder(order: RemoteOrder): Result<RemoteOrder>
    suspend fun getOrders(): Result<List<RemoteOrder>>
    suspend fun getOrderById(orderId: String): Result<RemoteOrder>
}

interface CartRepository {
    fun observeItems(): Flow<List<CartItem>>
    suspend fun addUnits(productId: Int, quantity: Int): Result<Unit>
    suspend fun decreaseUnit(productId: Int): Result<Unit>
    suspend fun removeLine(productId: Int): Result<Unit>
    suspend fun confirmOrder(order: RemoteOrder): Result<RemoteOrder>
}