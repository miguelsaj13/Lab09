package gt.uvg.lab09.viewModel

import gt.uvg.lab09.model.OrderLine
import gt.uvg.lab09.model.OrderReceipt
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.ProductSort
import gt.uvg.lab09.model.Profile

data class OrderFeedback(
    val message: String,
    val isError: Boolean
)

data class VersusUiState(
    val isCatalogLoading: Boolean = true,
    val catalogError: String? = null,
    val isFromCache: Boolean = false,
    val isLocalReady: Boolean = false,
    val isOrderBusy: Boolean = false,
    val darkTheme: Boolean = false,
    val sort: ProductSort = ProductSort.DEFAULT,
    val isTesting: Boolean = false,
    val testProductId: Int? = null,
    val testResult: String? = null,
    val products: List<Product> = emptyList(),
    val visibleProducts: List <Product> = emptyList(),
    val profiles: List<Profile> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val query: String = "",
    val orderLines: List<OrderLine> = emptyList(),
    val orderLineSubtotalsMinorUnits: Map<Int, Long> = emptyMap(),
    val orderUnitCount: Int = 0,
    val orderTotalMinorUnits: Long = 0L,
    val orderFeedback: OrderFeedback? = null,
    val latestReceipt: OrderReceipt? = null
)
