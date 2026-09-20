package gt.uvg.lab09.viewModel

import androidx.lifecycle.ViewModel
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VersusViewModel : ViewModel() {

    private val initialProfiles = listOf(
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

    private val initialProducts = listOf(
        Product(
            id = 1,
            name = "Air Jordan",
            description = "Tenis ligeros diseñados para baloncesto y uso diario.",
            price = 1500.00,
            profileId = 1,
            technicalDetails = "Cuenta con amortiguación para absorber impactos, soporte en el tobillo y suela de goma para mejorar la tracción."
        ),
        Product(
            id = 2,
            name = "Zamba",
            description = "Calzado deportivo para uso diario.",
            price = 1200.00,
            profileId = 1,
            technicalDetails = "Cuenta con una parte superior de cuero, detalles de gamuza y suela de goma."
        ),
        Product(
            id = 3,
            name = "Camisola de Argentina",
            description = "Camisola de la selección Argentina.",
            price = 500.00,
            profileId = 2,
            technicalDetails = "Fabricada con tejido ligero y transpirable para ofrecer mayor comodidad durante la actividad física."
        )
    )

    private val _uiState = MutableStateFlow(
        VersusUiState(
            products = initialProducts,
            profiles = initialProfiles,
            favoriteIds = emptySet()
        )
    )

    val uiState: StateFlow<VersusUiState> = _uiState.asStateFlow()

    fun toggleFavorite(productId: Int) {
        _uiState.update { currentState ->
            val favoritosActuales = currentState.favoriteIds
            val nuevosFavoritos = if (productId in favoritosActuales) {
                favoritosActuales - productId
            } else {
                favoritosActuales + productId
            }
            currentState.copy(favoriteIds = nuevosFavoritos)
        }
    }
}