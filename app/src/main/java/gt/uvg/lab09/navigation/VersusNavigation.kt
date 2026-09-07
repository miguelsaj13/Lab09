package gt.uvg.lab09.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import gt.uvg.lab09.ui.screens.CatalogScreen
import gt.uvg.lab09.ui.screens.DetailScreen
import gt.uvg.lab09.ui.screens.ProfileScreen
import gt.uvg.lab09.viewModel.VersusUiState

@Composable
fun VersusNavigation(
    uiState: VersusUiState,
    onFavoriteClick: (Int) -> Unit
) {
    val backStack = rememberNavBackStack(VersusNavKey.Catalog)

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

            entry<VersusNavKey.Catalog> {
                CatalogScreen(
                    products = uiState.products,
                    favoriteIds = uiState.favoriteIds,
                    onProductClick = { productId ->
                        backStack.add(VersusNavKey.Detail(productId))
                    },
                    onFavoriteClick = onFavoriteClick
                )
            }

            entry<VersusNavKey.Detail> { key ->

                val product = uiState.products.firstOrNull { it.id == key.productId }

                if (product != null) {
                    DetailScreen(
                        product = product,
                        isFavorite = product.id in uiState.favoriteIds,
                        onFavoriteClick = {
                            onFavoriteClick(product.id)
                        },
                        onProfileClick = { profileId ->
                            backStack.add(VersusNavKey.ProfileRoute(profileId))
                        },
                        onBackClick = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
            }

            entry<VersusNavKey.ProfileRoute> { key ->
                val profile = uiState.profiles.firstOrNull { it.id == key.profileID}

                if (profile != null) {
                    ProfileScreen(
                        profile = profile,
                        onBackClick = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
            }
        }
    )
}