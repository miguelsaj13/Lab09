package gt.uvg.lab09.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

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
