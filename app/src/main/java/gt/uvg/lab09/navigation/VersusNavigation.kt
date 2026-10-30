package gt.uvg.lab09.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.PaymentMethod
import gt.uvg.lab09.model.ProductSort
import gt.uvg.lab09.model.RemoteOrder
import gt.uvg.lab09.ui.screens.CatalogScreen
import gt.uvg.lab09.ui.screens.CheckoutScreen
import gt.uvg.lab09.ui.screens.ConfirmationScreen
import gt.uvg.lab09.ui.screens.DetailScreen
import gt.uvg.lab09.ui.screens.MyOrdersScreen
import gt.uvg.lab09.ui.screens.OrderDetailScreen
import gt.uvg.lab09.ui.screens.OrderScreen
import gt.uvg.lab09.ui.screens.ProfileScreen
import gt.uvg.lab09.viewModel.CheckoutUiState
import gt.uvg.lab09.viewModel.RemoteUiState
import gt.uvg.lab09.viewModel.VersusUiState

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
    checkoutUiState: CheckoutUiState,
    confirmationState: RemoteUiState<RemoteOrder>,
    ordersState: RemoteUiState<List<RemoteOrder>>,
    orderDetailState: RemoteUiState<RemoteOrder>,
    isConfirmEnabled: Boolean,

    onCheckoutNameChange: (String) -> Unit,
    onCheckoutNumberChange: (String) -> Unit,
    onCheckoutNitChange: (String) -> Unit,
    onCheckoutBusinessNameChange: (String) -> Unit,

    onBillingTypeChange: (BillingType) -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,

    onCheckoutNameTouched: () -> Unit,
    onCheckoutNumberTouched: () -> Unit,
    onCheckoutNitTouched: () -> Unit,
    onCheckoutBusinessNameTouched: () -> Unit,
    onConfirmOrder: () -> Unit,
    onResetConfirmationState: () -> Unit,

    onLoadOrders: () -> Unit,
    onLoadOrderDetail: (String) -> Unit,

    onRetryCatalog: () -> Unit,
    onSortChange: (ProductSort) -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onConcurrencyTest: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    onAddToOrder: (Int, Int) -> Unit,
    onDecreaseOrderItem: (Int) -> Unit,
    onRemoveOrderItem: (Int) -> Unit,
    onDismissOrderFeedback: () -> Unit
) {
    val backStack = rememberNavBackStack(VersusNavKey.Catalog)
    val gridState = rememberLazyGridState()

    val returnToCatalog: () -> Unit = {
        backStack.clear()
        backStack.add(VersusNavKey.Catalog)
    }

    LaunchedEffect(confirmationState) {
        if (
            confirmationState is RemoteUiState.Success &&
            backStack.lastOrNull() == VersusNavKey.Checkout
        ) {
            backStack.add(VersusNavKey.Confirmation)
        }
    }

    val isSendingOrder =
        confirmationState == RemoteUiState.Loading

    BackHandler(enabled = backStack.size > 1) {
        if (
            backStack.lastOrNull() == VersusNavKey.Checkout &&
            isSendingOrder
        ) {
            return@BackHandler
        }

        if (backStack.lastOrNull() == VersusNavKey.Confirmation) {
            returnToCatalog()
        } else {
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        transitionSpec = { openTransition },
        popTransitionSpec = { backTransition },
        predictivePopTransitionSpec = { backTransition },
        entryProvider = entryProvider {

            entry<VersusNavKey.Catalog> {
                CatalogScreen(
                    isLoading = uiState.isCatalogLoading,
                    error = uiState.catalogError,
                    isFromCache = uiState.isFromCache,
                    onRetryClick = onRetryCatalog,
                    sort = uiState.sort,
                    onSortChange = onSortChange,
                    darkTheme = uiState.darkTheme,
                    onDarkThemeChange = onDarkThemeChange,
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
                    },
                    onMyOrdersClick = {
                        onLoadOrders()
                        backStack.add(VersusNavKey.MyOrders)
                    }
                )
            }

            entry<VersusNavKey.Detail> { key ->
                val product = uiState.products.firstOrNull {
                    it.id == key.productId
                }

                if (product != null) {
                    DetailScreen(
                        isBusy = uiState.isOrderBusy || !uiState.isLocalReady || uiState.isTesting,
                        isTesting = uiState.isTesting && uiState.testProductId == product.id,
                        testResult = uiState.testResult.takeIf { uiState.testProductId == product.id },
                        onConcurrencyTest = { onConcurrencyTest(product.id) },
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
                val profile = uiState.profiles.firstOrNull {
                    it.id == key.profileId
                }

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
                    isBusy = uiState.isOrderBusy || !uiState.isLocalReady || uiState.isTesting,
                    lines = uiState.orderLines,
                    lineSubtotalsMinorUnits = uiState.orderLineSubtotalsMinorUnits,
                    totalMinorUnits = uiState.orderTotalMinorUnits,
                    orderUnitCount = uiState.orderUnitCount,
                    feedback = uiState.orderFeedback,
                    onIncrease = { productId ->
                        onAddToOrder(productId, 1)
                    },
                    onDecrease = onDecreaseOrderItem,
                    onRemove = onRemoveOrderItem,
                    onDismissFeedback = onDismissOrderFeedback,
                    onCheckoutClick = {
                        onResetConfirmationState()
                        backStack.add(VersusNavKey.Checkout)
                    },
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<VersusNavKey.Checkout> {
                CheckoutScreen(
                    uiState = checkoutUiState,
                    confirmationState = confirmationState,
                    isConfirmEnabled = isConfirmEnabled,

                    onNameChange = onCheckoutNameChange,
                    onNumberChange = onCheckoutNumberChange,
                    onNitChange = onCheckoutNitChange,
                    onBusinessNameChange = onCheckoutBusinessNameChange,

                    onBillingTypeChange = onBillingTypeChange,
                    onPaymentMethodChange = onPaymentMethodChange,

                    onNameTouched = onCheckoutNameTouched,
                    onNumberTouched = onCheckoutNumberTouched,
                    onNitTouched = onCheckoutNitTouched,
                    onBusinessNameTouched = onCheckoutBusinessNameTouched,

                    onConfirmClick = onConfirmOrder,

                    onBackClick = {
                        if (!isSendingOrder) {
                            backStack.removeLastOrNull()
                        }
                    }
                )
            }

            entry<VersusNavKey.Confirmation> {
                ConfirmationScreen(
                    receipt = uiState.latestReceipt,
                    onCatalogClick = returnToCatalog
                )
            }

            entry<VersusNavKey.MyOrders> {
                MyOrdersScreen(
                    ordersState = ordersState,
                    onOrderClick = { orderId ->
                        onLoadOrderDetail(orderId)
                        backStack.add(
                            VersusNavKey.OrderDetail(orderId)
                        )
                    },
                    onRetryClick = onLoadOrders,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<VersusNavKey.OrderDetail> { key ->
                OrderDetailScreen(
                    orderState = orderDetailState,
                    onRetryClick = {
                        onLoadOrderDetail(key.orderId)
                    },
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
