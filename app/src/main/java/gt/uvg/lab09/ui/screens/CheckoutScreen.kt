package gt.uvg.lab09.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.PaymentMethod
import gt.uvg.lab09.validation.validateBusinessName
import gt.uvg.lab09.validation.validateName
import gt.uvg.lab09.validation.validateNit
import gt.uvg.lab09.validation.validateNumber
import gt.uvg.lab09.viewModel.CheckoutUiState
import androidx.compose.foundation.layout.imePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    uiState: CheckoutUiState,
    isConfirmEnabled: Boolean,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onNitChange: (String) -> Unit,
    onBusinessNameChange: (String) -> Unit,
    onBillingTypeChange: (BillingType) -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onNameTouched: () -> Unit,
    onNumberTouched: () -> Unit,
    onNitTouched: () -> Unit,
    onBusinessNameTouched: () -> Unit,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val nitFocusRequester = remember {
        FocusRequester()
    }

    var nameHadFocus by remember {
        mutableStateOf(false)
    }

    var numberHadFocus by remember {
        mutableStateOf(false)
    }



    val nameError =
        if (uiState.nameIsTouched) {
            validateName(uiState.name)
        } else {
            null
        }

    val numberError =
        if (uiState.numberIsTouched) {
            validateNumber(uiState.number)
        } else {
            null
        }

    val nitError =
        if (
            uiState.billingType == BillingType.FACTURA_CON_NIT &&
            uiState.nitIsTouched
        ) {
            validateNit(uiState.nit)
        } else {
            null
        }

    val businessNameError =
        if (
            uiState.billingType == BillingType.FACTURA_CON_NIT &&
            uiState.businessNameIsTouched
        ) {
            validateBusinessName(uiState.businessName)
        } else {
            null
        }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Checkout")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBackClick
                    ) {
                        Text("Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = {
                    Text("Nombre completo *")
                },
                placeholder = {
                    Text("Ej. María Morales")
                },
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(nameError)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            nameHadFocus = true
                        } else if (nameHadFocus) {
                            onNameTouched()
                        }
                    },
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.number,
                onValueChange = onNumberChange,
                label = {
                    Text("Teléfono / WhatsApp *")
                },
                placeholder = {
                    Text("Ej. 55123456")
                },
                isError = numberError != null,
                supportingText = {
                    if (numberError != null) {
                        Text(numberError)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction =
                        if (
                            uiState.billingType ==
                            BillingType.FACTURA_CON_NIT
                        ) {
                            ImeAction.Next
                        } else {
                            ImeAction.Done
                        }
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (
                            uiState.billingType ==
                            BillingType.FACTURA_CON_NIT
                        ) {
                            nitFocusRequester.requestFocus()
                        }
                    },
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            numberHadFocus = true
                        } else if (numberHadFocus) {
                            onNumberTouched()
                        }
                    },
                singleLine = true
            )

            Text("Facturación *")

            Column(
                modifier = Modifier.selectableGroup()
            ) {

                BillingOption(
                    text = "Consumidor final (CF)",
                    selected =
                        uiState.billingType ==
                                BillingType.CONSUMIDOR_FINAL,
                    onClick = {
                        clearFocusAndKeyboard(
                            focusManager = focusManager,
                            hideKeyboard = {
                                keyboardController?.hide()
                            }
                        )

                        onBillingTypeChange(
                            BillingType.CONSUMIDOR_FINAL
                        )
                    }
                )

                BillingOption(
                    text = "Factura con NIT",
                    selected =
                        uiState.billingType ==
                                BillingType.FACTURA_CON_NIT,
                    onClick = {
                        clearFocusAndKeyboard(
                            focusManager = focusManager,
                            hideKeyboard = {
                                keyboardController?.hide()
                            }
                        )

                        onBillingTypeChange(
                            BillingType.FACTURA_CON_NIT
                        )
                    }
                )
            }

            AnimatedVisibility(
                visible =
                    uiState.billingType ==
                            BillingType.FACTURA_CON_NIT
            ) {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)
                ) {
                    var nitHadFocus by remember {
                        mutableStateOf(false)
                    }

                    var businessNameHadFocus by remember {
                        mutableStateOf(false)
                    }

                    OutlinedTextField(
                        value = uiState.nit,
                        onValueChange = onNitChange,
                        label = {
                            Text("NIT *")
                        },
                        placeholder = {
                            Text("Ej. 1234567")
                        },
                        isError = nitError != null,
                        supportingText = {
                            if (nitError != null) {
                                Text(nitError)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Next
                                )
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nitFocusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    nitHadFocus = true
                                } else if (nitHadFocus) {
                                    onNitTouched()
                                }
                            },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.businessName,
                        onValueChange = onBusinessNameChange,
                        label = {
                            Text("Razón Social *")
                        },
                        placeholder = {
                            Text("Ej. Comercial La Ceiba")
                        },
                        isError = businessNameError != null,
                        supportingText = {
                            if (businessNameError != null) {
                                Text(businessNameError)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization =
                                KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    businessNameHadFocus = true
                                } else if (businessNameHadFocus) {
                                    onBusinessNameTouched()
                                }
                            },
                        singleLine = true
                    )
                }
            }

            Text("Método de pago *")

            Column(
                modifier = Modifier.selectableGroup()
            ) {

                PaymentOption(
                    text = "Efectivo contra entrega",
                    selected =
                        uiState.paymentMethod ==
                                PaymentMethod.EFECTIVO,
                    onClick = {
                        clearFocusAndKeyboard(
                            focusManager = focusManager,
                            hideKeyboard = {
                                keyboardController?.hide()
                            }
                        )

                        onPaymentMethodChange(
                            PaymentMethod.EFECTIVO
                        )
                    }
                )

                PaymentOption(
                    text = "Transferencia bancaria",
                    selected =
                        uiState.paymentMethod ==
                                PaymentMethod.TRANSFERENCIA_BANCARIA,
                    onClick = {
                        clearFocusAndKeyboard(
                            focusManager = focusManager,
                            hideKeyboard = {
                                keyboardController?.hide()
                            }
                        )

                        onPaymentMethodChange(
                            PaymentMethod.TRANSFERENCIA_BANCARIA
                        )
                    }
                )
            }

            Button(
                onClick = onConfirmClick,
                enabled = isConfirmEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar pedido")
            }
        }
    }
}

@Composable
private fun BillingOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = null
        )

        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PaymentOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = null
        )

        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

private fun clearFocusAndKeyboard(
    focusManager: FocusManager,
    hideKeyboard: () -> Unit
) {
    focusManager.clearFocus()
    hideKeyboard()
}