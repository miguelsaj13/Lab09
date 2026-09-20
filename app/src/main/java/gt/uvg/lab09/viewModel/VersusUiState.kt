package gt.uvg.lab09.viewModel

import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.Profile

data class VersusUiState(
    val products: List<Product> = emptyList(),
    val profiles: List<Profile> = emptyList(),
    val favoriteIds: Set<Int> = emptySet()
)