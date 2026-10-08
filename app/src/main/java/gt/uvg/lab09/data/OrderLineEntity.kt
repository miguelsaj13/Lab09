package gt.uvg.lab09.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "order_lines")
data class OrderLineEntity(
    @PrimaryKey val productId: Int,
    val quantity: Int
)