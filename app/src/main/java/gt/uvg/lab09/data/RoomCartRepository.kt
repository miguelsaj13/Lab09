package gt.uvg.lab09.data

import gt.uvg.lab09.model.CartItem
import gt.uvg.lab09.model.RemoteOrder
import gt.uvg.lab09.repository.CartRepository
import gt.uvg.lab09.repository.OrdersRepository
import gt.uvg.lab09.repository.ProductRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class RoomCartRepository(
    private val dao: StoreDao,
    private val settings: LocalSettings,
    private val products: ProductRepository,
    private val orders: OrdersRepository
) : CartRepository {
    private val mutex = Mutex()
    private var prepared = false

    private suspend fun prepareLocked() {
        if (!prepared) {
            if (!settings.areReservationsReady()) {
                dao.clearOrderLines()
                settings.markReservationsReady()
            }
            prepared = true
        }
    }

    override fun observeItems(): Flow<List<CartItem>> = flow {
        mutex.withLock { prepareLocked() }
        emitAll(dao.observeOrderLines().map { lines -> lines.map { CartItem(it.productId, it.quantity) } })
    }

    override suspend fun addUnits(productId: Int, quantity: Int): Result<Unit> = repositoryResult {
        mutex.withLock {
            prepareLocked()
            require(quantity > 0) { "La cantidad debe ser mayor que cero." }
            val current = dao.getOrderLine(productId)?.quantity ?: 0
            val next = Math.addExact(current, quantity)
            withContext(NonCancellable) {
                products.reserveUnits(productId, quantity).getOrThrow()
                try {
                    dao.upsertOrderLine(OrderLineEntity(productId, next))
                } catch (exception: Exception) {
                    val compensation = products.releaseUnits(productId, quantity)
                    if (compensation.isFailure) {
                        throw IllegalStateException("La reserva se realizó, pero falló el guardado local y la devolución. Revisa las existencias antes de reintentar.", exception)
                    }
                    throw exception
                }
            }
        }
    }

    override suspend fun decreaseUnit(productId: Int): Result<Unit> = changeLine(productId, false)

    override suspend fun removeLine(productId: Int): Result<Unit> = changeLine(productId, true)

    private suspend fun changeLine(productId: Int, remove: Boolean): Result<Unit> = repositoryResult {
        mutex.withLock {
            prepareLocked()
            val line = dao.getOrderLine(productId) ?: return@withLock
            val released = if (remove) line.quantity else 1
            withContext(NonCancellable) {
                products.releaseUnits(productId, released).getOrThrow()
                try {
                    if (released == line.quantity) dao.deleteOrderLine(productId)
                    else dao.upsertOrderLine(line.copy(quantity = line.quantity - released))
                } catch (exception: Exception) {
                    throw IllegalStateException("Se devolvieron existencias, pero falló el guardado local. Revisa el pedido antes de reintentar.", exception)
                }
            }
        }
    }

    override suspend fun confirmOrder(order: RemoteOrder): Result<RemoteOrder> = repositoryResult {
        mutex.withLock {
            prepareLocked()
            val current = dao.observeOrderLines().first().associate { it.productId to it.quantity }
            check(current.isNotEmpty() && current == order.lines.associate { it.productId to it.quantity }) {
                "El pedido cambió. Revisa sus cantidades e inténtalo nuevamente."
            }
            withContext(NonCancellable) {
                val saved = orders.createOrder(order).getOrThrow()
                try {
                    dao.clearOrderLines()
                } catch (exception: Exception) {
                    throw IllegalStateException("El pedido ${saved.id} fue enviado, pero no se pudo limpiar el pedido local. Revísalo en Mis pedidos antes de reintentar.", exception)
                }
                saved
            }
        }
    }
}
