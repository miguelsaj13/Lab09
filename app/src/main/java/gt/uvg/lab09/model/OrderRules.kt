package gt.uvg.lab09.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

enum class OrderRejectionReason {
    PRODUCT_NOT_FOUND,
    NON_POSITIVE_INCREMENT,
    OUT_OF_STOCK,
    STOCK_EXCEEDED
}

sealed interface OrderUpdateResult {
    data class Success(val lines: List<OrderLine>) : OrderUpdateResult
    data class Rejected(val reason: OrderRejectionReason) : OrderUpdateResult
}

/**
 * Agrega unidades sin depender de Compose. Ante cualquier rechazo no produce
 * una lista parcial: el llamador conserva exactamente el pedido anterior.
 */
fun addToOrder(
    catalog: List<Product>,
    currentLines: List<OrderLine>,
    productId: Int,
    increment: Int
): OrderUpdateResult {
    if (increment <= 0) {
        return OrderUpdateResult.Rejected(OrderRejectionReason.NON_POSITIVE_INCREMENT)
    }

    val product = catalog.firstOrNull { it.id == productId }
        ?: return OrderUpdateResult.Rejected(OrderRejectionReason.PRODUCT_NOT_FOUND)

    if (product.stock <= 0) {
        return OrderUpdateResult.Rejected(OrderRejectionReason.OUT_OF_STOCK)
    }

    val existingQuantity = currentLines
        .firstOrNull { it.product.id == productId }
        ?.quantity
        ?: 0
    val requestedQuantity = existingQuantity.toLong() + increment.toLong()

    if (requestedQuantity > product.stock.toLong()) {
        return OrderUpdateResult.Rejected(OrderRejectionReason.STOCK_EXCEEDED)
    }

    val updatedLines = if (existingQuantity == 0) {
        currentLines + OrderLine(product = product, quantity = increment)
    } else {
        currentLines.map { line ->
            if (line.product.id == productId) {
                line.copy(quantity = requestedQuantity.toInt())
            } else {
                line
            }
        }
    }

    return OrderUpdateResult.Success(updatedLines)
}

fun decreaseOrderLine(
    currentLines: List<OrderLine>,
    productId: Int
): List<OrderLine> = currentLines.mapNotNull { line ->
    when {
        line.product.id != productId -> line
        line.quantity <= 1 -> null
        else -> line.copy(quantity = line.quantity - 1)
    }
}

fun removeOrderLine(
    currentLines: List<OrderLine>,
    productId: Int
): List<OrderLine> = currentLines.filterNot { it.product.id == productId }

fun calculateOrderUnitCount(lines: List<OrderLine>): Int =
    lines.sumOf { it.quantity }

fun priceToMinorUnits(price: Double): Long =
    BigDecimal.valueOf(price)
        .movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()

fun calculateLineSubtotalMinorUnits(line: OrderLine): Long =
    priceToMinorUnits(line.product.price) * line.quantity

fun calculateOrderTotalMinorUnits(lines: List<OrderLine>): Long =
    lines.sumOf(::calculateLineSubtotalMinorUnits)

fun formatQuetzales(amountMinorUnits: Long): String =
    String.format(Locale.US, "Q%.2f", amountMinorUnits / 100.0)

fun formatProductPrice(product: Product): String =
    formatQuetzales(priceToMinorUnits(product.price))
