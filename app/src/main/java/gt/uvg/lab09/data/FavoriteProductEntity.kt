package gt.uvg.lab09.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey val productId: Int
)