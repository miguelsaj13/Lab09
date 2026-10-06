package gt.uvg.lab09.viewModel

import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.PaymentMethod
import gt.uvg.lab09.validation.isCheckoutValid

data class CheckoutUiState(
    val name: String = "",
    val number: String = "",
    val billingType: BillingType = BillingType.FINAL_CONSUMER,
    val nit: String = "",
    val businessName: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val nameIsTouched: Boolean = false,
    val numberIsTouched: Boolean = false,
    val nitIsTouched: Boolean = false,
    val businessNameIsTouched: Boolean = false
) {
    val isFormValid: Boolean
        get() = isCheckoutValid(
            name = name,
            number = number,
            billingType = billingType,
            nit = nit,
            businessName = businessName
        )
}