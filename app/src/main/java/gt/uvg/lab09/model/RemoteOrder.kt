package gt.uvg.lab09.model

data class RemoteOrderLine(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val unitPriceMinorUnits: Long
)

data class RemoteOrder(
    val id: String = "",
    val createdAt: Long = 0L,
    val customerName: String,
    val billingType: String,
    val paymentMethod: String,
    val lines: List<RemoteOrderLine>,
    val totalMinorUnits: Long
)

data class CartItem(val productId: Int, val quantity: Int)

data class CatalogSnapshot(
    val products: List<Product>,
    val isFromCache: Boolean
)

enum class ProductSort { DEFAULT, NAME, PRICE_ASCENDING, PRICE_DESCENDING }