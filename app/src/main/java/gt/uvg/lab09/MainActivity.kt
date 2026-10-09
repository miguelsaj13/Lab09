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
import gt.uvg.lab09.viewModel.VersusViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Lab09Theme {

                val versusViewModel: VersusViewModel = viewModel()
                
                val versusState =
                    versusViewModel.uiState.collectAsStateWithLifecycle()
                val checkoutState =
                    versusViewModel.checkoutUiState.collectAsStateWithLifecycle()

                val isConfirmEnabled by remember(versusState, checkoutState) {
                    derivedStateOf {
                        checkoutState.value.isFormValid &&
                                versusState.value.orderUnitCount > 0
                    }
                }

                VersusNavigation(
                    uiState = versusState.value,
                    checkoutUiState = checkoutState.value,
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
                    onConfirmOrder = versusViewModel::confirmOrder,

                    onFavoriteClick = versusViewModel::toggleFavorite,
                    onQueryChange = versusViewModel::updateQuery,
                    onAddToOrder = versusViewModel::addProductToOrder,
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
