package gt.uvg.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.lab09.navigation.VersusNavigation
import gt.uvg.lab09.ui.theme.Lab09Theme
import gt.uvg.lab09.viewModel.RemoteUiState
import gt.uvg.lab09.viewModel.VersusViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val versusViewModel: VersusViewModel = viewModel(factory = VersusViewModel.Factory)
            val themeState = versusViewModel.uiState.collectAsStateWithLifecycle()
            Lab09Theme(darkTheme = themeState.value.darkTheme) {

                val versusState =
                    versusViewModel.uiState.collectAsStateWithLifecycle()

                val checkoutState =
                    versusViewModel.checkoutUiState.collectAsStateWithLifecycle()

                val confirmationState =
                    versusViewModel.confirmationState.collectAsStateWithLifecycle()

                val ordersState =
                    versusViewModel.ordersState.collectAsStateWithLifecycle()

                val orderDetailState =
                    versusViewModel.orderDetailState.collectAsStateWithLifecycle()

                val isConfirmEnabled by remember(
                    versusState,
                    checkoutState,
                    confirmationState
                ) {
                    derivedStateOf {
                        checkoutState.value.isFormValid &&
                                versusState.value.orderUnitCount > 0 &&
                                !versusState.value.isOrderBusy &&
                                !versusState.value.isTesting &&
                                confirmationState.value != RemoteUiState.Loading
                    }
                }

                VersusNavigation(
                    uiState = versusState.value,
                    checkoutUiState = checkoutState.value,
                    confirmationState = confirmationState.value,
                    ordersState = ordersState.value,
                    orderDetailState = orderDetailState.value,
                    isConfirmEnabled = isConfirmEnabled,

                    onCheckoutNameChange =
                        versusViewModel::updateCheckoutName,
                    onCheckoutNumberChange =
                        versusViewModel::updateCheckoutNumber,
                    onCheckoutNitChange =
                        versusViewModel::updateCheckoutNit,
                    onCheckoutBusinessNameChange =
                        versusViewModel::updateCheckoutBusinessName,

                    onBillingTypeChange =
                        versusViewModel::updateBillingType,
                    onPaymentMethodChange =
                        versusViewModel::updatePaymentMethod,

                    onCheckoutNameTouched =
                        versusViewModel::touchCheckoutName,
                    onCheckoutNumberTouched =
                        versusViewModel::touchCheckoutNumber,
                    onCheckoutNitTouched =
                        versusViewModel::touchCheckoutNit,
                    onCheckoutBusinessNameTouched =
                        versusViewModel::touchCheckoutBusinessName,

                    onConfirmOrder =
                        versusViewModel::confirmOrder,
                    onResetConfirmationState =
                        versusViewModel::resetConfirmationState,

                    onLoadOrders =
                        versusViewModel::loadOrders,
                    onLoadOrderDetail =
                        versusViewModel::loadOrderDetail,

                    onRetryCatalog = versusViewModel::retryCatalog,
                    onSortChange = versusViewModel::updateSort,
                    onDarkThemeChange = versusViewModel::setDarkTheme,
                    onConcurrencyTest = { id -> versusViewModel.onConcurrencyTest(id, 20) },
                    onFavoriteClick =
                        versusViewModel::toggleFavorite,
                    onQueryChange =
                        versusViewModel::updateQuery,
                    onAddToOrder =
                        versusViewModel::addProductToOrder,
                    onDecreaseOrderItem =
                        versusViewModel::decreaseProductInOrder,
                    onRemoveOrderItem =
                        versusViewModel::removeProductFromOrder,
                    onDismissOrderFeedback =
                        versusViewModel::clearOrderFeedback
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab09Theme {
    }
}
