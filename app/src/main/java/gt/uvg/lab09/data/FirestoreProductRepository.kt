package gt.uvg.lab09.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.TransactionOptions
import gt.uvg.lab09.model.CatalogSnapshot
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.repository.AuthRepository
import gt.uvg.lab09.repository.ProductRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreProductRepository(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ProductRepository {
    override fun observeProducts(): Flow<CatalogSnapshot> = callbackFlow {
        authRepository.ensureSession().getOrThrow()
        val registration = firestore.collection("products")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    close(error)
                } else if (snapshot != null) {
                    try {
                        trySend(CatalogSnapshot(
                            products = snapshot.documents.map { it.toProduct() }.sortedBy { it.id },
                            isFromCache = snapshot.metadata.isFromCache
                        ))
                    } catch (exception: Exception) {
                        close(exception)
                    }
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun reserveUnit(productId: Int): Result<Unit> =
        reserveUnits(productId, 1)

    override suspend fun reserveUnits(productId: Int, quantity: Int): Result<Unit> =
        changeStock(productId, quantity, reserve = true)

    override suspend fun releaseUnits(productId: Int, quantity: Int): Result<Unit> =
        changeStock(productId, quantity, reserve = false)

    private suspend fun changeStock(
        productId: Int,
        quantity: Int,
        reserve: Boolean
    ): Result<Unit> = repositoryResult {
        require(quantity > 0) { "La cantidad debe ser mayor que cero." }
        authRepository.ensureSession().getOrThrow()
        val reference = firestore.collection("products").document(productId.toString())
        val options = TransactionOptions.Builder().setMaxAttempts(20).build()
        firestore.runTransaction(options) { transaction ->
            val document = transaction.get(reference)
            check(document.exists()) { "El producto ya no existe." }
            val stock = document.getLong("stock")
                ?: error("El producto no tiene existencias válidas.")
            check(stock >= 0L) { "El producto tiene existencias inválidas." }
            if (reserve) {
                check(stock >= quantity.toLong()) { "Agotado o sin existencias suficientes. El pedido no cambió." }
            }
            val updatedStock = if (reserve) stock - quantity else Math.addExact(stock, quantity.toLong())
            check(updatedStock <= Int.MAX_VALUE) { "Las existencias exceden el límite permitido." }
            transaction.update(reference, "stock", updatedStock)
            Unit
        }.await()
    }
}

private fun DocumentSnapshot.toProduct(): Product {
    fun integer(field: String): Int {
        val value = getLong(field) ?: error("Falta $field en el producto $id.")
        require(value in 0..Int.MAX_VALUE.toLong()) { "Valor inválido en $field del producto $id." }
        return value.toInt()
    }
    fun text(field: String): String = getString(field)
        ?: error("Falta $field en el producto $id.")
    val productId = id.toIntOrNull() ?: error("ID de producto inválido: $id.")
    val price = (get("price") as? Number)?.toDouble()
        ?: error("Precio inválido en el producto $id.")
    require(price.isFinite() && price >= 0.0) { "Precio inválido en el producto $id." }
    return Product(
        id = productId,
        name = text("name"),
        description = text("description"),
        price = price,
        profileId = integer("profileId"),
        technicalDetails = text("technicalDetails"),
        stock = integer("stock"),
        imageUrl = text("imageUrl")
    )
}
