package gt.uvg.lab09.data

import gt.uvg.lab09.model.RemoteOrder
import gt.uvg.lab09.model.RemoteOrderLine
import gt.uvg.lab09.network.OrderDto
import gt.uvg.lab09.network.OrderLineDto
import gt.uvg.lab09.network.OrdersApi
import gt.uvg.lab09.repository.OrdersRepository

class RetrofitOrdersRepository(private val api: OrdersApi) : OrdersRepository {
    override suspend fun createOrder(order: RemoteOrder): Result<RemoteOrder> = repositoryResult {
        api.createOrder(OrderDto(
            customerName = order.customerName,
            billingType = order.billingType,
            paymentMethod = order.paymentMethod,
            lines = order.lines.map { OrderLineDto(it.productId, it.productName, it.quantity, it.unitPriceMinorUnits) },
            totalMinorUnits = order.totalMinorUnits
        )).toDomain()
    }

    override suspend fun getOrders(): Result<List<RemoteOrder>> = repositoryResult {
        api.getOrders().map { it.toDomain() }
            .sortedWith(compareByDescending<RemoteOrder> { it.createdAt }
                .thenByDescending { it.id.toLongOrNull() ?: 0L })
    }

    override suspend fun getOrderById(orderId: String): Result<RemoteOrder> = repositoryResult {
        api.getOrderById(orderId).toDomain()
    }
}

private fun OrderDto.toDomain() = RemoteOrder(
    id = id,
    createdAt = createdAt,
    customerName = customerName,
    billingType = billingType,
    paymentMethod = paymentMethod,
    lines = lines.map { RemoteOrderLine(it.productId, it.productName, it.quantity, it.unitPriceMinorUnits) },
    totalMinorUnits = totalMinorUnits
)
