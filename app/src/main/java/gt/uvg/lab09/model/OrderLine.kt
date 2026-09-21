package gt.uvg.lab09.model

/** Una línea única del pedido y la cantidad acumulada de ese producto. */
data class OrderLine(
    val product: Product,
    val quantity: Int
)
