package gt.uvg.lab09.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {


    @Query("SELECT * FROM favorite_products")
    fun observeFavorites(): Flow<List<FavoriteProductEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteProductEntity)

    @Query("DELETE FROM favorite_products WHERE productId = :productId")
    suspend fun deleteFavorite(productId: Int)

    @Query("SELECT * FROM order_lines")
    fun observeOrderLines(): Flow<List<OrderLineEntity>>

    // REPLACE porque productId es la clave primaria: si ya existe la línea,
    // sobreescribe su cantidad en vez de fallar.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrderLine(line: OrderLineEntity)

    @Query("DELETE FROM order_lines WHERE productId = :productId")
    suspend fun deleteOrderLine(productId: Int)

    // Se usa al confirmar el pedido (Lab 11): vacía todas las líneas de una vez.
    @Query("DELETE FROM order_lines")
    suspend fun clearOrderLines()
}