package gt.uvg.lab09.viewModel

import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.PaymentMethod

data class CheckoutUiState(
    //Productos, nombre completo, numero de telefono
    val name: String = "",
    val number: String = "",
    val billingType: BillingType = BillingType.CONSUMIDOR_FINAL,
    val nit: String = "",
    val businessName: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val nameIsTouched: Boolean = false,
    val numberIsTouched: Boolean = false,
    val nitIsTouched: Boolean = false,
    val businessNameIsTouched: Boolean = false
)