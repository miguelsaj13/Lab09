package gt.uvg.lab09.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.OrderRejectionReason
import gt.uvg.lab09.model.OrderReceipt
import gt.uvg.lab09.model.OrderUpdateResult
import gt.uvg.lab09.model.PaymentMethod
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.Profile
import gt.uvg.lab09.model.addToOrder
import gt.uvg.lab09.model.calculateLineSubtotalMinorUnits
import gt.uvg.lab09.model.calculateOrderTotalMinorUnits
import gt.uvg.lab09.model.calculateOrderUnitCount
import gt.uvg.lab09.model.decreaseOrderLine
import gt.uvg.lab09.model.removeOrderLine
import gt.uvg.lab09.model.priceToMinorUnits
import gt.uvg.lab09.network.OrderDto
import gt.uvg.lab09.network.OrderLineDto
import gt.uvg.lab09.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import kotlin.random.Random

class VersusViewModel : ViewModel() {

    private val ordersApi = RetrofitInstance.ordersApi

    private val _confirmationState =
        MutableStateFlow<RemoteUiState<OrderDto>>(
            RemoteUiState.Idle
        )

    val confirmationState: StateFlow<RemoteUiState<OrderDto>> =
        _confirmationState.asStateFlow()

    private val _ordersState =
        MutableStateFlow<RemoteUiState<List<OrderDto>>>(
            RemoteUiState.Idle
        )

    val ordersState: StateFlow<RemoteUiState<List<OrderDto>>> =
        _ordersState.asStateFlow()

    private val _orderDetailState =
        MutableStateFlow<RemoteUiState<OrderDto>>(
            RemoteUiState.Idle
        )

    val orderDetailState: StateFlow<RemoteUiState<OrderDto>> =
        _orderDetailState.asStateFlow()

    //Profiles que vienen del lab 09
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

    //Productos del lab 09
    private val initialProducts = listOf(
        Product(
            id = 1,
            name = "Air Jordan",
            description = "Tenis ligeros diseñados para baloncesto y uso diario.",
            price = 1500.00,
            profileId = 1,
            technicalDetails = "Cuenta con amortiguación para absorber impactos, soporte en el tobillo y suela de goma para mejorar la tracción.",
            stock = 0,
            imageUrl = "https://picsum.photos/seed/air-jordan/600/400"
        ),
        Product(
            id = 2,
            name = "Zamba",
            description = "Calzado deportivo para uso diario.",
            price = 1200.00,
            profileId = 1,
            technicalDetails = "Cuenta con una parte superior de cuero, detalles de gamuza y suela de goma.",
            stock = 3,
            imageUrl = "https://picsum.photos/seed/zamba/600/400"
        ),
        Product(
            id = 3,
            name = "Camisola de Argentina",
            description = "Camisola de la selección Argentina.",
            price = 500.00,
            profileId = 2,
            technicalDetails = "Fabricada con tejido ligero y transpirable para ofrecer mayor comodidad durante la actividad física.",
            stock = 8,
            imageUrl = "https://picsum.photos/seed/camisola-argentina/600/400"
        )
    )

    //Plantilla para describir una categoria de productos y que tenga sentido
    private data class ProductTemplate(
        val names: List<String>,
        val descriptions: List<String>,
        val technicalDetails: List<String>,
        val profileId: Int,
        val priceRange: IntRange
    )

    private val productTemplates = listOf(
        //tennis de Nike
        ProductTemplate(
            names = listOf(
                "Air Court",
                "Zoom Flight",
                "Air Max Sport",
                "Court Vision",
                "Precision Run",
                "Street Runner"
            ),
            descriptions = listOf(
                "Tenis deportivos diseñados para baloncesto y entrenamiento.",
                "Calzado ligero para actividad física y uso diario.",
                "Tenis con soporte y amortiguación para movimientos de alta intensidad."
            ),
            technicalDetails = listOf(
                "Cuenta con amortiguación de impacto, soporte reforzado y suela de goma de alta tracción.",
                "Incorpora una entresuela ligera, tejido transpirable y suela resistente al desgaste.",
                "Diseñado con soporte lateral, amortiguación y materiales flexibles para mayor comodidad."
            ),
            profileId = 1,
            priceRange = 900..1900
        ),

        //Camisolas de nike
        ProductTemplate(
            names = listOf(
                "Dri-FIT Training",
                "Court Performance",
                "Pro Training Tee",
                "Sport Academy",
                "Elite Match"
            ),
            descriptions = listOf(
                "Camisola deportiva diseñada para entrenamiento y actividad física.",
                "Prenda ligera y cómoda para deportes y uso diario.",
                "Camisola transpirable para sesiones de entrenamiento de alta intensidad."
            ),
            technicalDetails = listOf(
                "Fabricada con tejido transpirable que ayuda a controlar la humedad.",
                "Utiliza materiales ligeros y flexibles para facilitar el movimiento.",
                "Cuenta con tejido de secado rápido y costuras diseñadas para reducir la fricción."
            ),
            profileId = 1,
            priceRange = 300..750
        ),

        //Shorts de nike
        ProductTemplate(
            names = listOf(
                "Flex Training Shorts",
                "Court Dry Shorts",
                "Academy Sport Shorts",
                "Performance Flex",
                "Pro Training Shorts"
            ),
            descriptions = listOf(
                "Short deportivo para entrenamiento y uso cotidiano.",
                "Short ligero diseñado para facilitar el movimiento.",
                "Prenda deportiva cómoda para gimnasio, carrera y entrenamiento."
            ),
            technicalDetails = listOf(
                "Cuenta con tejido flexible, cintura ajustable y material transpirable.",
                "Fabricado con fibras ligeras de secado rápido.",
                "Diseñado con ajuste deportivo y ventilación para actividades de alta intensidad."
            ),
            profileId = 1,
            priceRange = 250..650
        ),

        //Hoodies de nike
        ProductTemplate(
            names = listOf(
                "Club Fleece",
                "Sport Hoodie",
                "Training Fleece",
                "Academy Hoodie",
                "Urban Sport Hoodie"
            ),
            descriptions = listOf(
                "Sudadera deportiva para clima fresco y uso diario.",
                "Sudadera cómoda diseñada para entrenamiento ligero y descanso.",
                "Prenda de abrigo deportiva con ajuste cómodo."
            ),
            technicalDetails = listOf(
                "Fabricada con tejido suave y cálido con puños elásticos.",
                "Cuenta con capucha ajustable, bolsillo frontal y tejido de alta durabilidad.",
                "Combina una capa interior suave con materiales resistentes para uso frecuente."
            ),
            profileId = 1,
            priceRange = 500..1100
        ),

        //Tennis de adidas
        ProductTemplate(
            names = listOf(
                "Run Falcon",
                "Court Boost",
                "Street Classic",
                "Response Runner",
                "Training Bounce",
                "Urban Sprint"
            ),
            descriptions = listOf(
                "Calzado deportivo diseñado para correr y entrenar.",
                "Tenis cómodos para entrenamiento y uso diario.",
                "Calzado ligero con soporte para actividades deportivas."
            ),
            technicalDetails = listOf(
                "Cuenta con entresuela amortiguada, suela de goma y tejido transpirable.",
                "Incorpora soporte en el talón y materiales flexibles para mejorar la comodidad.",
                "Diseñado con amortiguación ligera y una suela de alta tracción."
            ),
            profileId = 2,
            priceRange = 750..1700
        ),

        //Camisolas de adidas
        ProductTemplate(
            names = listOf(
                "Performance Jersey",
                "Training Essentials",
                "Match Ready",
                "Aeroready Sport",
                "Club Jersey"
            ),
            descriptions = listOf(
                "Camisola deportiva para entrenamiento y competición.",
                "Prenda ligera diseñada para mantener comodidad durante la actividad física.",
                "Camisola para deportes y entrenamiento de uso frecuente."
            ),
            technicalDetails = listOf(
                "Fabricada con tejido transpirable y tecnología de control de humedad.",
                "Cuenta con fibras ligeras de secado rápido y ajuste deportivo.",
                "Diseñada para facilitar la ventilación para actividades de alta intensidad."
            ),
            profileId = 2,
            priceRange = 300..800
        ),

        //Pantalones de adidas
        ProductTemplate(
            names = listOf(
                "Tiro Training Pants",
                "Essentials Track Pants",
                "Performance Jogger",
                "Sport Training Pants",
                "Aeroready Pants"
            ),
            descriptions = listOf(
                "Pantalón deportivo diseñado para entrenamiento y uso diario.",
                "Pantalón cómodo para calentamiento y actividades deportivas.",
                "Prenda deportiva flexible para entrenamiento y descanso."
            ),
            technicalDetails = listOf(
                "Cuenta con cintura ajustable y tejido flexible de secado rápido.",
                "Fabricado con material transpirable y ajuste deportivo.",
                "Diseñado con tejido resistente, bolsillos laterales y piernas de corte cómodo."
            ),
            profileId = 2,
            priceRange = 400..900
        ),

        //Hoodies de adidas
        ProductTemplate(
            names = listOf(
                "Tiro Track Jacket",
                "Essentials Sport Jacket",
                "Training Windbreaker",
                "Performance Jacket",
                "Club Track Jacket"
            ),
            descriptions = listOf(
                "Chaqueta deportiva ligera para entrenamiento y uso cotidiano.",
                "Chaqueta diseñada para proteger durante actividades al aire libre.",
                "Prenda de abrigo diseñada para calentamiento y clima fresco."
            ),
            technicalDetails = listOf(
                "Fabricada con material ligero y cierre frontal completo.",
                "Cuenta con tejido resistente al viento y bolsillos laterales.",
                "Diseñada con materiales transpirables y puños ajustados para mayor comodidad."
            ),
            profileId = 2,
            priceRange = 550..1200
        )
    )

    //Esta funcion genera los productos faltantes (497) para completar el catalogo.
    private fun generateProducts(): List<Product> {
        val random = Random(2026)

        val generatedProducts = (4..500).map { id ->

            val template = productTemplates.random(random)

            val name = template.names.random(random)
            val description = template.descriptions.random(random)
            val technicalDetails = template.technicalDetails.random(random)

            val generatedName = "$name $id"

            val price = template.priceRange.random(random).toDouble()

            val stock = random.nextInt(
                from = 0,
                until = 11
            )

            Product(
                id = id,
                name = generatedName,
                description = description,
                price = price,
                profileId = template.profileId,
                technicalDetails = technicalDetails,
                stock = stock,
                imageUrl = "https://picsum.photos/seed/product-$id/600/400"
            )
        }

        return initialProducts + generatedProducts
    }

    private val products = generateProducts()

    private val _uiState = MutableStateFlow(
        VersusUiState(
            products = products,
            visibleProducts = products,
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
        if (_confirmationState.value == RemoteUiState.Loading) {
            return
        }

        val checkoutState = _checkoutUiState.value
        val orderState = _uiState.value

        if (!checkoutState.isFormValid || orderState.orderUnitCount < 1) {
            _confirmationState.value = RemoteUiState.Error(
                "Completa el formulario y agrega productos al pedido."
            )
            return
        }

        val orderDto = OrderDto(
            customerName = checkoutState.name.trim(),
            billingType = checkoutState.billingType.name,
            paymentMethod = checkoutState.paymentMethod.name,
            lines = orderState.orderLines.map { line ->
                OrderLineDto(
                    productId = line.product.id,
                    productName = line.product.name,
                    quantity = line.quantity,
                    unitPriceMinorUnits = priceToMinorUnits(line.product.price)
                )
            },
            totalMinorUnits = orderState.orderTotalMinorUnits
        )

        _confirmationState.value = RemoteUiState.Loading

        viewModelScope.launch {
            try {
                val savedOrder = ordersApi.createOrder(orderDto)

                val receipt = OrderReceipt(
                    folio = savedOrder.id,
                    customerName = savedOrder.customerName,
                    billingType = checkoutState.billingType,
                    nit = if (
                        checkoutState.billingType == BillingType.INVOICE_WITH_NIT
                    ) {
                        checkoutState.nit.trim()
                    } else {
                        null
                    },
                    businessName = if (
                        checkoutState.billingType == BillingType.INVOICE_WITH_NIT
                    ) {
                        checkoutState.businessName.trim()
                    } else {
                        null
                    },
                    paymentMethod = checkoutState.paymentMethod,
                    totalMinorUnits = savedOrder.totalMinorUnits
                )

                _uiState.update { currentState ->
                    currentState
                        .withOrderLines(
                            lines = emptyList(),
                            feedback = null
                        )
                        .copy(latestReceipt = receipt)
                }

                _checkoutUiState.value = CheckoutUiState()

                _confirmationState.value =
                    RemoteUiState.Success(savedOrder)

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: IOException) {
                _confirmationState.value = RemoteUiState.Error(
                    "No se pudo conectar con el servidor. Revisa tu conexión e inténtalo nuevamente."
                )

            } catch (exception: HttpException) {
                _confirmationState.value = RemoteUiState.Error(
                    "El servidor no pudo procesar el pedido. Inténtalo nuevamente."
                )

            } catch (exception: SerializationException) {
                _confirmationState.value = RemoteUiState.Error(
                    "No pudimos procesar la respuesta del servidor."
                )

            } catch (exception: Exception) {
                _confirmationState.value = RemoteUiState.Error(
                    "Ocurrió un error inesperado. Inténtalo nuevamente."
                )
            }
        }
    }

    fun resetConfirmationState() {
        if (_confirmationState.value != RemoteUiState.Loading) {
            _confirmationState.value = RemoteUiState.Idle
        }
    }


    fun loadOrders() {
        if (_ordersState.value == RemoteUiState.Loading) {
            return
        }

        _ordersState.value = RemoteUiState.Loading

        viewModelScope.launch {
            try {
                val orders = ordersApi.getOrders()
                    .sortedWith(
                        compareByDescending<OrderDto> { it.createdAt }
                            .thenByDescending { it.id.toLongOrNull() ?: 0L }
                    )

                _ordersState.value =
                    if (orders.isEmpty()) {
                        RemoteUiState.Empty
                    } else {
                        RemoteUiState.Success(orders)
                    }

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: IOException) {
                _ordersState.value = RemoteUiState.Error(
                    "No se pudo conectar con el servidor. Revisa tu conexión e inténtalo nuevamente."
                )

            } catch (exception: HttpException) {
                _ordersState.value = RemoteUiState.Error(
                    "No se pudieron consultar los pedidos. Inténtalo nuevamente."
                )

            } catch (exception: SerializationException) {
                _ordersState.value = RemoteUiState.Error(
                    "No pudimos procesar los pedidos recibidos."
                )

            } catch (exception: Exception) {
                _ordersState.value = RemoteUiState.Error(
                    "Ocurrió un error inesperado al consultar los pedidos."
                )
            }
        }
    }


    fun loadOrderDetail(orderId: String) {
        if (_orderDetailState.value == RemoteUiState.Loading) {
            return
        }

        _orderDetailState.value = RemoteUiState.Loading

        viewModelScope.launch {
            try {
                val order = ordersApi.getOrderById(orderId)
                _orderDetailState.value = RemoteUiState.Success(order)

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: IOException) {
                _orderDetailState.value = RemoteUiState.Error(
                    "No se pudo conectar con el servidor. Revisa tu conexión e inténtalo nuevamente."
                )

            } catch (exception: HttpException) {
                _orderDetailState.value = RemoteUiState.Error(
                    if (exception.code() == 404) {
                        "Este pedido ya no existe en el servidor."
                    } else {
                        "No se pudo consultar el pedido. Inténtalo nuevamente."
                    }
                )

            } catch (exception: SerializationException) {
                _orderDetailState.value = RemoteUiState.Error(
                    "No pudimos procesar la información del pedido."
                )

            } catch (exception: Exception) {
                _orderDetailState.value = RemoteUiState.Error(
                    "Ocurrió un error inesperado al consultar el pedido."
                )
            }
        }
    }

    fun toggleFavorite(productId: Int) {
        _uiState.update { currentState ->

            val currentFavorites = currentState.favoriteIds

            val newFavorites =
                if (productId in currentFavorites) {
                    currentFavorites - productId
                } else {
                    currentFavorites + productId
                }

            currentState.copy(
                favoriteIds = newFavorites
            )
        }
    }

    fun updateQuery(newQuery: String) {
        _uiState.update { currentState ->
            currentState.copy(
                query = newQuery,
                visibleProducts = filterProducts(
                    products = currentState.products,
                    query = newQuery
                )
            )
        }
    }

    fun addProductToOrder(productId: Int, increment: Int = 1) {
        _uiState.update { currentState ->
            when (
                val result = addToOrder(
                    catalog = currentState.products,
                    currentLines = currentState.orderLines,
                    productId = productId,
                    increment = increment
                )
            ) {
                is OrderUpdateResult.Success -> currentState.withOrderLines(
                    lines = result.lines,
                    feedback = OrderFeedback(
                        message = if (increment == 1) {
                            "Producto agregado al pedido."
                        } else {
                            "$increment unidades agregadas al pedido."
                        },
                        isError = false
                    )
                )

                is OrderUpdateResult.Rejected -> currentState.copy(
                    orderFeedback = OrderFeedback(
                        message = result.reason.toVisibleMessage(),
                        isError = true
                    )
                )
            }
        }
    }

    fun decreaseProductInOrder(productId: Int) {
        _uiState.update { currentState ->
            val updatedLines = decreaseOrderLine(
                currentLines = currentState.orderLines,
                productId = productId
            )
            currentState.withOrderLines(
                lines = updatedLines,
                feedback = null
            )
        }
    }

    fun removeProductFromOrder(productId: Int) {
        _uiState.update { currentState ->
            val updatedLines = removeOrderLine(
                currentLines = currentState.orderLines,
                productId = productId
            )
            currentState.withOrderLines(
                lines = updatedLines,
                feedback = null
            )
        }
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

private fun OrderRejectionReason.toVisibleMessage(): String = when (this) {
    OrderRejectionReason.PRODUCT_NOT_FOUND ->
        "No se encontró el producto solicitado. El pedido no cambió."
    OrderRejectionReason.NON_POSITIVE_INCREMENT ->
        "La cantidad por agregar debe ser mayor que cero. El pedido no cambió."
    OrderRejectionReason.OUT_OF_STOCK ->
        "El producto está agotado. El pedido no cambió."
    OrderRejectionReason.STOCK_EXCEEDED ->
        "No hay existencias suficientes para agregar otra unidad. El pedido no cambió."
}