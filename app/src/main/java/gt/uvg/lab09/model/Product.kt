package gt.uvg.lab09.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val profileId: Int,
    val technicalDetails: String,
    val stock: Int,
    val imageUrl: String
)