package gt.uvg.lab09.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import gt.uvg.lab09.ui.screens.CatalogScreen
import gt.uvg.lab09.ui.screens.DetailScreen
import gt.uvg.lab09.ui.screens.OrderScreen
import gt.uvg.lab09.ui.screens.ProfileScreen
import gt.uvg.lab09.viewModel.VersusUiState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

private const val TRANSITION_DURATION_MS = 300

private val openTransition: ContentTransform =
    slideInHorizontally(
        animationSpec = tween(TRANSITION_DURATION_MS),
        initialOffsetX = { fullWidth -> fullWidth }
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(TRANSITION_DURATION_MS),
        targetOffsetX = { fullWidth -> -fullWidth }
    )

private val backTransition: ContentTransform =
    slideInHorizontally(
        animationSpec = tween(TRANSITION_DURATION_MS),
        initialOffsetX = { fullWidth -> -fullWidth }
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(TRANSITION_DURATION_MS),
        targetOffsetX = { fullWidth -> fullWidth }
    )
@Composable
fun VersusNavigation(
    uiState: VersusUiState,
    onFavoriteClick: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    onAddToOrder: (Int, Int) -> Unit,
    onDecreaseOrderItem: (Int) -> Unit,
    onRemoveOrderItem: (Int) -> Unit,
    onDismissOrderFeedback: () -> Unit
) {
    val backStack = rememberNavBackStack(VersusNavKey.Catalog)
    val gridState = rememberLazyGridState()
    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        transitionSpec = { openTransition },
        popTransitionSpec = { backTransition },
        predictivePopTransitionSpec = { backTransition },
        entryProvider = entryProvider {

            entry<VersusNavKey.Catalog> {
                CatalogScreen(
                    products = uiState.visibleProducts,
                    totalCount = uiState.products.size,
                    query = uiState.query,
                    favoriteIds = uiState.favoriteIds,
                    gridState = gridState,
                    onQueryChange = onQueryChange,
                    onProductClick = { productId ->
                        onDismissOrderFeedback()
                        backStack.add(VersusNavKey.Detail(productId))
                    },
                    onFavoriteClick = onFavoriteClick,
                    orderUnitCount = uiState.orderUnitCount,
                    onOrderClick = {
                        onDismissOrderFeedback()
                        backStack.add(VersusNavKey.Order)
                    }
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
                        },
                        quantityInOrder = uiState.orderLines
                            .firstOrNull { it.product.id == product.id }
                            ?.quantity
                            ?: 0,
                        orderFeedback = uiState.orderFeedback,
                        onAddToOrder = {
                            onAddToOrder(product.id, 1)
                        },
                        onDismissOrderFeedback = onDismissOrderFeedback
                    )
                }
            }

            entry<VersusNavKey.ProfileRoute> { key ->
                val profile = uiState.profiles.firstOrNull { it.id == key.profileId}

                if (profile != null) {
                    ProfileScreen(
                        profile = profile,
                        onBackClick = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
            }

            entry<VersusNavKey.Order> {
                OrderScreen(
                    lines = uiState.orderLines,
                    lineSubtotalsMinorUnits = uiState.orderLineSubtotalsMinorUnits,
                    totalMinorUnits = uiState.orderTotalMinorUnits,
                    feedback = uiState.orderFeedback,
                    onIncrease = { productId ->
                        onAddToOrder(productId, 1)
                    },
                    onDecrease = onDecreaseOrderItem,
                    onRemove = onRemoveOrderItem,
                    onDismissFeedback = onDismissOrderFeedback,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
