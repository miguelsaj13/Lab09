package gt.uvg.lab09.network

import kotlinx.serialization.Serializable

@Serializable
data class OrderLineDto(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val unitPriceMinorUnits: Long
)

@Serializable
data class OrderDto(
    val id: String = "",
    val createdAt: Long = 0L,
    val customerName: String,
    val billingType: String,
    val paymentMethod: String,
    val lines: List<OrderLineDto>,
    val totalMinorUnits: Long
)