package gt.uvg.lab09

import gt.uvg.lab09.model.OrderLine
import gt.uvg.lab09.model.OrderRejectionReason
import gt.uvg.lab09.model.OrderUpdateResult
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.addToOrder
import gt.uvg.lab09.model.calculateLineSubtotalMinorUnits
import gt.uvg.lab09.model.calculateOrderTotalMinorUnits
import gt.uvg.lab09.model.calculateOrderUnitCount
import gt.uvg.lab09.model.decreaseOrderLine
import gt.uvg.lab09.model.formatQuetzales
import gt.uvg.lab09.model.removeOrderLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderRulesTest {
    private val productA = product(id = 1, price = 25.90, stock = 3)
    private val productB = product(id = 2, price = 10.50, stock = 4)
    private val soldOut = product(id = 3, price = 8.00, stock = 0)
    private val catalog = listOf(productA, productB, soldOut)

    @Test
    fun addingSameProductAccumulatesInOneLine() {
        val first = addToOrder(catalog, emptyList(), productA.id, 1).successLines()
        val second = addToOrder(catalog, first, productA.id, 1).successLines()

        assertEquals(1, second.size)
        assertEquals(2, second.single().quantity)
        assertEquals(2, calculateOrderUnitCount(second))
    }

    @Test
    fun exceedingStockIsRejectedAndOriginalOrderIsUntouched() {
        val original = listOf(OrderLine(productA, quantity = 3))
        val result = addToOrder(catalog, original, productA.id, 1)

        assertEquals(
            OrderUpdateResult.Rejected(OrderRejectionReason.STOCK_EXCEEDED),
            result
        )
        assertEquals(3, original.single().quantity)
    }

    @Test
    fun soldOutUnknownAndNonPositiveRequestsAreRejected() {
        val original = listOf(OrderLine(productB, quantity = 1))

        assertEquals(
            OrderUpdateResult.Rejected(OrderRejectionReason.OUT_OF_STOCK),
            addToOrder(catalog, original, soldOut.id, 1)
        )
        assertEquals(
            OrderUpdateResult.Rejected(OrderRejectionReason.PRODUCT_NOT_FOUND),
            addToOrder(catalog, original, productId = 999, increment = 1)
        )
        assertEquals(
            OrderUpdateResult.Rejected(OrderRejectionReason.NON_POSITIVE_INCREMENT),
            addToOrder(catalog, original, productB.id, increment = 0)
        )
        assertEquals(listOf(OrderLine(productB, 1)), original)
    }

    @Test
    fun decreasingOneUnitRemovesLineWithoutChangingOthers() {
        val original = listOf(
            OrderLine(productA, quantity = 1),
            OrderLine(productB, quantity = 2)
        )

        val updated = decreaseOrderLine(original, productA.id)

        assertEquals(listOf(OrderLine(productB, quantity = 2)), updated)
    }

    @Test
    fun removingLineDoesNotModifyOtherProducts() {
        val remaining = OrderLine(productB, quantity = 2)
        val original = listOf(OrderLine(productA, quantity = 1), remaining)

        val updated = removeOrderLine(original, productA.id)

        assertEquals(listOf(remaining), updated)
        assertSame(remaining, updated.single())
    }

    @Test
    fun subtotalsTotalAndFormattingUseMinorUnits() {
        val lines = listOf(
            OrderLine(productA, quantity = 2),
            OrderLine(productB, quantity = 1)
        )

        assertEquals(5_180L, calculateLineSubtotalMinorUnits(lines[0]))
        assertEquals(1_050L, calculateLineSubtotalMinorUnits(lines[1]))
        assertEquals(6_230L, calculateOrderTotalMinorUnits(lines))
        assertEquals("Q62.30", formatQuetzales(calculateOrderTotalMinorUnits(lines)))
        assertEquals("Q0.00", formatQuetzales(calculateOrderTotalMinorUnits(emptyList())))
    }

    private fun OrderUpdateResult.successLines(): List<OrderLine> {
        assertTrue(this is OrderUpdateResult.Success)
        return (this as OrderUpdateResult.Success).lines
    }

    private fun product(id: Int, price: Double, stock: Int) = Product(
        id = id,
        name = "Producto $id",
        description = "Descripción",
        price = price,
        profileId = 1,
        technicalDetails = "Detalles",
        stock = stock,
        imageUrl = ""
    )
}
