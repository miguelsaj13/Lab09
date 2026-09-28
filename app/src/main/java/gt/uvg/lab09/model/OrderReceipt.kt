package gt.uvg.lab09.model

/** Datos inmutables de una compra ya confirmada. */
data class OrderReceipt(
    val folio: String,
    val customerName: String,
    val billingType: BillingType,
    val nit: String?,
    val businessName: String?,
    val paymentMethod: PaymentMethod,
    val totalMinorUnits: Long
)
