package gt.uvg.lab09.viewModel

import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.Profile
import gt.uvg.lab09.model.OrderLine

data class OrderFeedback(
    val message: String,
    val isError: Boolean
)

data class VersusUiState(
    val products: List<Product> = emptyList(),
    val visibleProducts: List <Product> = emptyList(),
    val profiles: List<Profile> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val query: String = "",
    val orderLines: List<OrderLine> = emptyList(),
    val orderLineSubtotalsMinorUnits: Map<Int, Long> = emptyMap(),
    val orderUnitCount: Int = 0,
    val orderTotalMinorUnits: Long = 0L,
    val orderFeedback: OrderFeedback? = null
)
