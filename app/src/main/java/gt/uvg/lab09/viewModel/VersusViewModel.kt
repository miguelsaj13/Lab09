package gt.uvg.lab09.viewModel

import androidx.lifecycle.ViewModel
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.Profile

class VersusViewModel : ViewModel() {

    val profiles = listOf(
        Profile(
            id = 1,
            name = "Nike",
            role = "Fabricante de zapatos de baloncesto",
            location = "Portland, Oregon",
            description = "Marca especializada en calzado y ropa deportiva de alto rendimiento."
        ),
        Profile(
            id = 2,
            name = "Adidas",
            role = "Fabricante de ropa deportiva",
            location = "Los Angeles, California",
            description = "Marca enfocada en productos deportivos modernos para entrenamiento y uso diario."
        )
    )

    val products = listOf(
        Product(
            id = 1,
            name = "Air Jordan",
            description = "Tenis ligeros diseñados para baloncesto y uso diario.",
            price = 1500.00,
            profileId = 1
        ),
        Product(
            id = 2,
            name = "Zamba",
            description = "Calzado deportivo para uso diario.",
            price = 1200.00,
            profileId = 1
        ),
        Product(
            id = 3,
            name = "Camisola de Argentina",
            description = "Camisola de la selección Argentina.",
            price = 500.00,
            profileId = 2
        )
    )
}