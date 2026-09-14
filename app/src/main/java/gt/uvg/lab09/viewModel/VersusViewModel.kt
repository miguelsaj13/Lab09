package gt.uvg.lab09.viewModel

import androidx.lifecycle.ViewModel
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class VersusViewModel : ViewModel() {

    //Profiles que vienen del lab 09
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

    //Productos del lab 09
    private val initialProducts = listOf(
        Product(
            id = 1,
            name = "Air Jordan",
            description = "Tenis ligeros diseñados para baloncesto y uso diario.",
            price = 1500.00,
            profileId = 1,
            technicalDetails = "Cuenta con amortiguación para absorber impactos, soporte en el tobillo y suela de goma para mejorar la tracción.",
            stock = 0,
            imageUrl = "https://picsum.photos/seed/air-jordan/600/400"
        ),
        Product(
            id = 2,
            name = "Zamba",
            description = "Calzado deportivo para uso diario.",
            price = 1200.00,
            profileId = 1,
            technicalDetails = "Cuenta con una parte superior de cuero, detalles de gamuza y suela de goma.",
            stock = 3,
            imageUrl = "https://picsum.photos/seed/zamba/600/400"
        ),
        Product(
            id = 3,
            name = "Camisola de Argentina",
            description = "Camisola de la selección Argentina.",
            price = 500.00,
            profileId = 2,
            technicalDetails = "Fabricada con tejido ligero y transpirable para ofrecer mayor comodidad durante la actividad física.",
            stock = 8,
            imageUrl = "https://picsum.photos/seed/camisola-argentina/600/400"
        )
    )

    //Plantilla para describir una categoria de productos y que tenga sentido
    private data class ProductTemplate(
        val names: List<String>,
        val descriptions: List<String>,
        val technicalDetails: List<String>,
        val profileId: Int,
        val priceRange: IntRange
    )

    private val productTemplates = listOf(
        //tennis de Nike
        ProductTemplate(
            names = listOf(
                "Air Court",
                "Zoom Flight",
                "Air Max Sport",
                "Court Vision",
                "Precision Run",
                "Street Runner"
            ),
            descriptions = listOf(
                "Tenis deportivos diseñados para baloncesto y entrenamiento.",
                "Calzado ligero para actividad física y uso diario.",
                "Tenis con soporte y amortiguación para movimientos de alta intensidad."
            ),
            technicalDetails = listOf(
                "Cuenta con amortiguación de impacto, soporte reforzado y suela de goma de alta tracción.",
                "Incorpora una entresuela ligera, tejido transpirable y suela resistente al desgaste.",
                "Diseñado con soporte lateral, amortiguación y materiales flexibles para mayor comodidad."
            ),
            profileId = 1,
            priceRange = 900..1900
        ),

        //Camisolas de nike
        ProductTemplate(
            names = listOf(
                "Dri-FIT Training",
                "Court Performance",
                "Pro Training Tee",
                "Sport Academy",
                "Elite Match"
            ),
            descriptions = listOf(
                "Camisola deportiva diseñada para entrenamiento y actividad física.",
                "Prenda ligera y cómoda para deportes y uso diario.",
                "Camisola transpirable para sesiones de entrenamiento de alta intensidad."
            ),
            technicalDetails = listOf(
                "Fabricada con tejido transpirable que ayuda a controlar la humedad.",
                "Utiliza materiales ligeros y flexibles para facilitar el movimiento.",
                "Cuenta con tejido de secado rápido y costuras diseñadas para reducir la fricción."
            ),
            profileId = 1,
            priceRange = 300..750
        ),

        //Shorts de nike
        ProductTemplate(
            names = listOf(
                "Flex Training Shorts",
                "Court Dry Shorts",
                "Academy Sport Shorts",
                "Performance Flex",
                "Pro Training Shorts"
            ),
            descriptions = listOf(
                "Short deportivo para entrenamiento y uso cotidiano.",
                "Short ligero diseñado para facilitar el movimiento.",
                "Prenda deportiva cómoda para gimnasio, carrera y entrenamiento."
            ),
            technicalDetails = listOf(
                "Cuenta con tejido flexible, cintura ajustable y material transpirable.",
                "Fabricado con fibras ligeras de secado rápido.",
                "Diseñado con ajuste deportivo y ventilación para actividades de alta intensidad."
            ),
            profileId = 1,
            priceRange = 250..650
        ),

        //Hoodies de nike
        ProductTemplate(
            names = listOf(
                "Club Fleece",
                "Sport Hoodie",
                "Training Fleece",
                "Academy Hoodie",
                "Urban Sport Hoodie"
            ),
            descriptions = listOf(
                "Sudadera deportiva para clima fresco y uso diario.",
                "Sudadera cómoda diseñada para entrenamiento ligero y descanso.",
                "Prenda de abrigo deportiva con ajuste cómodo."
            ),
            technicalDetails = listOf(
                "Fabricada con tejido suave y cálido con puños elásticos.",
                "Cuenta con capucha ajustable, bolsillo frontal y tejido de alta durabilidad.",
                "Combina una capa interior suave con materiales resistentes para uso frecuente."
            ),
            profileId = 1,
            priceRange = 500..1100
        ),

        //Tennis de adidas
        ProductTemplate(
            names = listOf(
                "Run Falcon",
                "Court Boost",
                "Street Classic",
                "Response Runner",
                "Training Bounce",
                "Urban Sprint"
            ),
            descriptions = listOf(
                "Calzado deportivo diseñado para correr y entrenar.",
                "Tenis cómodos para entrenamiento y uso diario.",
                "Calzado ligero con soporte para actividades deportivas."
            ),
            technicalDetails = listOf(
                "Cuenta con entresuela amortiguada, suela de goma y tejido transpirable.",
                "Incorpora soporte en el talón y materiales flexibles para mejorar la comodidad.",
                "Diseñado con amortiguación ligera y una suela de alta tracción."
            ),
            profileId = 2,
            priceRange = 750..1700
        ),

        //Camisolas de adidas
        ProductTemplate(
            names = listOf(
                "Performance Jersey",
                "Training Essentials",
                "Match Ready",
                "Aeroready Sport",
                "Club Jersey"
            ),
            descriptions = listOf(
                "Camisola deportiva para entrenamiento y competición.",
                "Prenda ligera diseñada para mantener comodidad durante la actividad física.",
                "Camisola para deportes y entrenamiento de uso frecuente."
            ),
            technicalDetails = listOf(
                "Fabricada con tejido transpirable y tecnología de control de humedad.",
                "Cuenta con fibras ligeras de secado rápido y ajuste deportivo.",
                "Diseñada para facilitar la ventilación durante actividades de alta intensidad."
            ),
            profileId = 2,
            priceRange = 300..800
        ),

        //Pantalones de adidas
        ProductTemplate(
            names = listOf(
                "Tiro Training Pants",
                "Essentials Track Pants",
                "Performance Jogger",
                "Sport Training Pants",
                "Aeroready Pants"
            ),
            descriptions = listOf(
                "Pantalón deportivo diseñado para entrenamiento y uso diario.",
                "Pantalón cómodo para calentamiento y actividades deportivas.",
                "Prenda deportiva flexible para entrenamiento y descanso."
            ),
            technicalDetails = listOf(
                "Cuenta con cintura ajustable y tejido flexible de secado rápido.",
                "Fabricado con material transpirable y ajuste deportivo.",
                "Diseñado con tejido resistente, bolsillos laterales y piernas de corte cómodo."
            ),
            profileId = 2,
            priceRange = 400..900
        ),

        //Hoodies de adidas
        ProductTemplate(
            names = listOf(
                "Tiro Track Jacket",
                "Essentials Sport Jacket",
                "Training Windbreaker",
                "Performance Jacket",
                "Club Track Jacket"
            ),
            descriptions = listOf(
                "Chaqueta deportiva ligera para entrenamiento y uso cotidiano.",
                "Chaqueta diseñada para proteger durante actividades al aire libre.",
                "Prenda deportiva para calentamiento y clima fresco."
            ),
            technicalDetails = listOf(
                "Fabricada con material ligero y cierre frontal completo.",
                "Cuenta con tejido resistente al viento y bolsillos laterales.",
                "Diseñada con materiales transpirables y puños ajustados para mayor comodidad."
            ),
            profileId = 2,
            priceRange = 550..1200
        )
    )

    //Esta funcion genera los productos faltantes (497) para completar el catalogo.
    private fun generateProducts(): List<Product> {
        val random = Random(2026)

        val generatedProducts = (4..500).map { id ->

            val template = productTemplates.random(random)

            val name = template.names.random(random)
            val description = template.descriptions.random(random)
            val technicalDetails = template.technicalDetails.random(random)

            val generatedName = "$name $id"

            val price = template.priceRange.random(random).toDouble()

            val stock = random.nextInt(
                from = 0,
                until = 11
            )

            Product(
                id = id,
                name = generatedName,
                description = description,
                price = price,
                profileId = template.profileId,
                technicalDetails = technicalDetails,
                stock = stock,
                imageUrl = "https://picsum.photos/seed/product-$id/600/400"
            )
        }

        return initialProducts + generatedProducts
    }
    private val products = generateProducts()

    private val _uiState = MutableStateFlow(
        VersusUiState(
            products = products,
            profiles = initialProfiles,
            favoriteIds = emptySet()
        )
    )

    val uiState: StateFlow<VersusUiState> = _uiState.asStateFlow()

    fun toggleFavorite(productId: Int) {
        _uiState.update { currentState ->

            val favoritosActuales = currentState.favoriteIds

            val nuevosFavoritos =
                if (productId in favoritosActuales) {
                    favoritosActuales - productId
                } else {
                    favoritosActuales + productId
                }

            currentState.copy(
                favoriteIds = nuevosFavoritos
            )
        }
    }
}