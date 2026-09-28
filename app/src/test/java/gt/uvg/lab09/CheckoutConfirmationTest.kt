package gt.uvg.lab09

import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.PaymentMethod
import gt.uvg.lab09.viewModel.VersusViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckoutConfirmationTest {
    @Test
    fun validConfirmationStoresReceiptAndResetsOrderAndForm() {
        val viewModel = VersusViewModel()
        viewModel.addProductToOrder(productId = 2, increment = 2)
        completeValidCheckout(viewModel)
        val totalBeforeConfirmation = viewModel.uiState.value.orderTotalMinorUnits

        assertTrue(viewModel.confirmOrder())

        val receipt = requireNotNull(viewModel.uiState.value.latestReceipt)
        assertEquals("#ORD-00001", receipt.folio)
        assertEquals("María López", receipt.customerName)
        assertEquals(totalBeforeConfirmation, receipt.totalMinorUnits)
        assertEquals(0, viewModel.uiState.value.orderUnitCount)
        assertTrue(viewModel.uiState.value.orderLines.isEmpty())
        assertEquals("", viewModel.checkoutUiState.value.name)
        assertFalse(viewModel.checkoutUiState.value.isFormValid)
    }

    @Test
    fun invalidConfirmationDoesNotCreateReceiptOrEmptyOrder() {
        val viewModel = VersusViewModel()
        viewModel.addProductToOrder(productId = 2)

        assertFalse(viewModel.confirmOrder())

        assertNull(viewModel.uiState.value.latestReceipt)
        assertEquals(1, viewModel.uiState.value.orderUnitCount)
    }

    @Test
    fun folioOnlyAdvancesAfterSuccessfulConfirmation() {
        val viewModel = VersusViewModel()

        assertFalse(viewModel.confirmOrder())
        viewModel.addProductToOrder(productId = 2)
        completeValidCheckout(viewModel)
        assertTrue(viewModel.confirmOrder())
        assertEquals("#ORD-00001", viewModel.uiState.value.latestReceipt?.folio)

        viewModel.addProductToOrder(productId = 2)
        completeValidCheckout(viewModel)
        assertTrue(viewModel.confirmOrder())
        assertEquals("#ORD-00002", viewModel.uiState.value.latestReceipt?.folio)
    }

    private fun completeValidCheckout(viewModel: VersusViewModel) {
        viewModel.updateCheckoutName("  María López  ")
        viewModel.updateCheckoutNumber("55123456")
        viewModel.updateBillingType(BillingType.CONSUMIDOR_FINAL)
        viewModel.updatePaymentMethod(PaymentMethod.EFECTIVO)
    }
}
