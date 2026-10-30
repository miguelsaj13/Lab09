package gt.uvg.lab09.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import gt.uvg.lab09.VersusApplication
import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.CartItem
import gt.uvg.lab09.model.OrderLine
import gt.uvg.lab09.model.OrderReceipt
import gt.uvg.lab09.model.PaymentMethod
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.ProductSort
import gt.uvg.lab09.model.Profile
import gt.uvg.lab09.model.RemoteOrder
import gt.uvg.lab09.model.RemoteOrderLine
import gt.uvg.lab09.model.calculateLineSubtotalMinorUnits
import gt.uvg.lab09.model.calculateOrderTotalMinorUnits
import gt.uvg.lab09.model.calculateOrderUnitCount
import gt.uvg.lab09.model.priceToMinorUnits
import gt.uvg.lab09.repository.CartRepository
import gt.uvg.lab09.repository.FavoritesRepository
import gt.uvg.lab09.repository.OrdersRepository
import gt.uvg.lab09.repository.PreferencesRepository
import gt.uvg.lab09.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VersusViewModel(
    private val productRepository: ProductRepository,
    private val favoritesRepository: FavoritesRepository,
    private val cartRepository: CartRepository,
    private val preferencesRepository: PreferencesRepository,
    private val ordersRepository: OrdersRepository
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VersusApplication
                val container = application.container
                VersusViewModel(
                    container.productRepository, container.favoritesRepository,
                    container.cartRepository, container.preferencesRepository, container.ordersRepository
                )
            }
        }
    }
    private var catalogJob: Job? = null
    private var catalogTimeoutJob: Job? = null
    private var cartItems: List<CartItem> = emptyList()

    private val _confirmationState =
        MutableStateFlow<RemoteUiState<RemoteOrder>>(
            RemoteUiState.Idle
        )

    val confirmationState: StateFlow<RemoteUiState<RemoteOrder>> =
        _confirmationState.asStateFlow()

    private val _ordersState =
        MutableStateFlow<RemoteUiState<List<RemoteOrder>>>(
            RemoteUiState.Idle
        )

    val ordersState: StateFlow<RemoteUiState<List<RemoteOrder>>> =
        _ordersState.asStateFlow()

    private val _orderDetailState =
        MutableStateFlow<RemoteUiState<RemoteOrder>>(
            RemoteUiState.Idle
        )

    val orderDetailState: StateFlow<RemoteUiState<RemoteOrder>> =
        _orderDetailState.asStateFlow()

    private val initialProfiles = listOf(
        Profile(
            id = 1,
            name = "Nike",
            role = "Fabricante de zapatos de baloncesto",
            location = "Portland, Oregon",
            description = "Marca especializada en calzado y ropa deportiva de alto rendimiento."
        ),
        Profile(
            id = 2,
            name = "Adidas",
            role = "Fabricante de ropa deportiva",
            location = "Los Angeles, California",
            description = "Marca enfocada en productos deportivos modernos para entrenamiento y uso diario."
        )
    )

    private val _uiState = MutableStateFlow(
        VersusUiState(
            profiles = initialProfiles,
            favoriteIds = emptySet()
        )
    )

    val uiState: StateFlow<VersusUiState> = _uiState.asStateFlow()

    private val _checkoutUiState = MutableStateFlow(
        CheckoutUiState()
    )

    val checkoutUiState: StateFlow<CheckoutUiState> =
        _checkoutUiState.asStateFlow()

    fun updateCheckoutName(name: String) {
        _checkoutUiState.update { currentState ->
            currentState.copy(
                name = name
            )
        }
    }

    fun updateCheckoutNumber(number: String) {
        _checkoutUiState.update { currentState ->
            currentState.copy(
                number = number
            )
        }
    }

    fun updateCheckoutNit(nit: String) {
        _checkoutUiState.update { currentState ->
            currentState.copy(
                nit = nit
            )
        }
    }

    fun updateCheckoutBusinessName(businessName: String) {
        _checkoutUiState.update { currentState ->
            currentState.copy(
                businessName = businessName
            )
        }
    }

    fun updateBillingType(billingType: BillingType) {
        _checkoutUiState.update { currentState ->
            if (billingType == BillingType.FINAL_CONSUMER) {
                currentState.copy(
                    billingType = billingType,
                    nit = "",
                    businessName = "",
                    nitIsTouched = false,
                    businessNameIsTouched = false
                )
            } else {
                currentState.copy(
                    billingType = billingType
                )
            }
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        _checkoutUiState.update { currentState ->
            currentState.copy(
                paymentMethod = paymentMethod
            )
        }
    }

    fun touchCheckoutName() {
        _checkoutUiState.update { currentState ->
            currentState.copy(
                nameIsTouched = true
            )
        }
    }

    fun touchCheckoutNumber() {
        _checkoutUiState.update { currentState ->
            currentState.copy(
                numberIsTouched = true
            )
        }
    }

    fun touchCheckoutNit() {
        _checkoutUiState.update { currentState ->
            if (currentState.billingType == BillingType.INVOICE_WITH_NIT) {
                currentState.copy(
                    nitIsTouched = true
                )
            } else {
                currentState
            }
        }
    }

    fun touchCheckoutBusinessName() {
        _checkoutUiState.update { currentState ->
            if (currentState.billingType == BillingType.INVOICE_WITH_NIT) {
                currentState.copy(
                    businessNameIsTouched = true
                )
            } else {
                currentState
            }
        }
    }

    fun confirmOrder() {
        if (_uiState.value.isOrderBusy || _uiState.value.isTesting) return
        val checkoutState = _checkoutUiState.value
        val orderState = _uiState.value
        if (!checkoutState.isFormValid || orderState.orderUnitCount < 1 || !orderState.isLocalReady) {
            _confirmationState.value = RemoteUiState.Error("Completa el formulario y agrega productos al pedido.")
            return
        }
        val order = RemoteOrder(
            customerName = checkoutState.name.trim(),
            billingType = checkoutState.billingType.name,
            paymentMethod = checkoutState.paymentMethod.name,
            lines = orderState.orderLines.map { line ->
                RemoteOrderLine(line.product.id, line.product.name, line.quantity, priceToMinorUnits(line.product.price))
            },
            totalMinorUnits = orderState.orderTotalMinorUnits
        )
        _confirmationState.value = RemoteUiState.Loading
        _uiState.update { it.copy(isOrderBusy = true) }
        viewModelScope.launch {
            try {
                cartRepository.confirmOrder(order).fold(
                    onSuccess = { savedOrder ->
                        cartItems = emptyList()
                        val receipt = OrderReceipt(
                            folio = savedOrder.id,
                            customerName = savedOrder.customerName,
                            billingType = checkoutState.billingType,
                            nit = checkoutState.nit.trim().takeIf { checkoutState.billingType == BillingType.INVOICE_WITH_NIT },
                            businessName = checkoutState.businessName.trim().takeIf { checkoutState.billingType == BillingType.INVOICE_WITH_NIT },
                            paymentMethod = checkoutState.paymentMethod,
                            totalMinorUnits = savedOrder.totalMinorUnits
                        )
                        _uiState.update { it.withOrderLines(emptyList(), null).copy(latestReceipt = receipt) }
                        _checkoutUiState.value = CheckoutUiState()
                        _confirmationState.value = RemoteUiState.Success(savedOrder)
                    },
                    onFailure = { _confirmationState.value = RemoteUiState.Error(it.message ?: "No se pudo confirmar el pedido.") }
                )
            } finally {
                _uiState.update { it.copy(isOrderBusy = false) }
            }
        }
    }

    fun resetConfirmationState() {
        if (_confirmationState.value != RemoteUiState.Loading) {
            _confirmationState.value = RemoteUiState.Idle
        }
    }

    init {
        retryCatalog()
        viewModelScope.launch {
            favoritesRepository.observeFavoriteIds().catch { showError("No se pudieron recuperar los favoritos.") }
                .collect { ids -> _uiState.update { it.copy(favoriteIds = ids) } }
        }
        viewModelScope.launch {
            cartRepository.observeItems().catch { showError("No se pudo recuperar el pedido. Reinicia la aplicación.") }
                .collect { items ->
                    cartItems = items
                    _uiState.update { it.copy(isLocalReady = true) }
                    refreshOrderLines()
                }
        }
        viewModelScope.launch {
            preferencesRepository.observeDarkTheme().catch { showError("No se pudo recuperar el tema.") }
                .collect { enabled -> _uiState.update { it.copy(darkTheme = enabled) } }
        }
    }

    fun retryCatalog() {
        catalogJob?.cancel()
        catalogTimeoutJob?.cancel()
        _uiState.update { it.copy(isCatalogLoading = true, catalogError = null) }
        catalogTimeoutJob = viewModelScope.launch {
            delay(15_000L)
            if (_uiState.value.isCatalogLoading) {
                _uiState.update { it.copy(isCatalogLoading = false, catalogError = "No se pudo cargar el catálogo. Revisa tu conexión y reintenta.") }
            }
        }
        catalogJob = viewModelScope.launch {
            productRepository.observeProducts()
                .catch { exception ->
                    catalogTimeoutJob?.cancel()
                    _uiState.update { it.copy(isCatalogLoading = false, catalogError = "No se pudo cargar el catálogo. Revisa la conexión, la sesión y las reglas de Firestore.") }
                }
                .collect { snapshot ->
                    catalogTimeoutJob?.cancel()
                    _uiState.update { current ->
                        current.copy(
                            products = snapshot.products,
                            visibleProducts = visibleProducts(snapshot.products, current.query, current.sort),
                            isCatalogLoading = false,
                            isFromCache = snapshot.isFromCache,
                            catalogError = if (snapshot.isFromCache && snapshot.products.isEmpty()) "No hay catálogo guardado. Conéctate y reintenta." else null
                        )
                    }
                    refreshOrderLines()
                }
        }
    }

    private fun refreshOrderLines() {
        _uiState.update { current ->
            val catalog = current.products.associateBy { it.id }
            val lines = cartItems.mapNotNull { item -> catalog[item.productId]?.let { OrderLine(it, item.quantity) } }
            current.withOrderLines(lines, current.orderFeedback)
        }
    }

    fun loadOrders() {
        if (_ordersState.value == RemoteUiState.Loading) return
        _ordersState.value = RemoteUiState.Loading
        viewModelScope.launch {
            ordersRepository.getOrders().fold(
                onSuccess = { orders -> _ordersState.value = if (orders.isEmpty()) RemoteUiState.Empty else RemoteUiState.Success(orders) },
                onFailure = { _ordersState.value = RemoteUiState.Error("No se pudieron consultar los pedidos. Revisa tu conexión y reintenta.") }
            )
        }
    }

    fun loadOrderDetail(orderId: String) {
        if (_orderDetailState.value == RemoteUiState.Loading) return
        _orderDetailState.value = RemoteUiState.Loading
        viewModelScope.launch {
            ordersRepository.getOrderById(orderId).fold(
                onSuccess = { _orderDetailState.value = RemoteUiState.Success(it) },
                onFailure = { _orderDetailState.value = RemoteUiState.Error("No se pudo consultar el pedido. Revisa tu conexión y reintenta.") }
            )
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(productId).onFailure { showError("No se pudo guardar el favorito.") }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkTheme(enabled).onFailure { showError("No se pudo guardar el tema.") }
        }
    }

    fun updateQuery(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, visibleProducts = visibleProducts(it.products, newQuery, it.sort)) }
    }

    fun updateSort(sort: ProductSort) {
        _uiState.update { it.copy(sort = sort, visibleProducts = visibleProducts(it.products, it.query, sort)) }
    }

    fun addProductToOrder(productId: Int, increment: Int = 1) {
        mutateOrder("Producto agregado al pedido.") { cartRepository.addUnits(productId, increment) }
    }

    fun decreaseProductInOrder(productId: Int) {
        mutateOrder("Unidad devuelta a las existencias.") { cartRepository.decreaseUnit(productId) }
    }

    fun removeProductFromOrder(productId: Int) {
        mutateOrder("Producto eliminado y existencias devueltas.") { cartRepository.removeLine(productId) }
    }

    private fun mutateOrder(message: String, action: suspend () -> Result<Unit>) {
        if (_uiState.value.isOrderBusy || !_uiState.value.isLocalReady || _uiState.value.isTesting) return
        _uiState.update { it.copy(isOrderBusy = true, orderFeedback = null) }
        viewModelScope.launch {
            try {
                action().fold(
                    onSuccess = { _uiState.update { it.copy(orderFeedback = OrderFeedback(message, false)) } },
                    onFailure = { showError(it.message ?: "No se pudo actualizar el pedido. Revisa tu conexión.") }
                )
            } finally {
                _uiState.update { it.copy(isOrderBusy = false) }
            }
        }
    }

    fun onConcurrencyTest(productId: Int, attempts: Int = 20) {
        if (_uiState.value.isTesting || _uiState.value.isOrderBusy || attempts !in 1..20) return
        _uiState.update { it.copy(isTesting = true, testResult = null, testProductId = productId) }
        viewModelScope.launch {
            try {
                val results = List(attempts) { async { productRepository.reserveUnit(productId) } }.awaitAll()
                val successes = results.count { it.isSuccess }
                _uiState.update { it.copy(testResult = "Éxitos: $successes · Rechazos: ${attempts - successes}") }
            } finally {
                _uiState.update { it.copy(isTesting = false) }
            }
        }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(orderFeedback = OrderFeedback(message, true)) }
    }

    fun clearOrderFeedback() {
        _uiState.update { currentState ->
            currentState.copy(orderFeedback = null)
        }
    }
}

private fun VersusUiState.withOrderLines(
    lines: List<gt.uvg.lab09.model.OrderLine>,
    feedback: OrderFeedback?
): VersusUiState = copy(
    orderLines = lines,
    orderLineSubtotalsMinorUnits = lines.associate { line ->
        line.product.id to calculateLineSubtotalMinorUnits(line)
    },
    orderUnitCount = calculateOrderUnitCount(lines),
    orderTotalMinorUnits = calculateOrderTotalMinorUnits(lines),
    orderFeedback = feedback
)

private fun visibleProducts(products: List<Product>, query: String, sort: ProductSort): List<Product> {
    val filtered = filterProducts(products, query)
    return when (sort) {
        ProductSort.DEFAULT -> filtered.sortedBy { it.id }
        ProductSort.NAME -> filtered.sortedBy { it.name.lowercase() }
        ProductSort.PRICE_ASCENDING -> filtered.sortedBy { it.price }
        ProductSort.PRICE_DESCENDING -> filtered.sortedByDescending { it.price }
    }
}
