package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.formatProductPrice
import gt.uvg.lab09.viewModel.OrderFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onProfileClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    quantityInOrder: Int,
    orderFeedback: OrderFeedback?,
    onAddToOrder: () -> Unit,
    onDismissOrderFeedback: () -> Unit,
    isBusy: Boolean,
    isTesting: Boolean,
    testResult: String?,
    onConcurrencyTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTechnicalDetails by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(product.name)
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
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(product.description)

            Text(
                text = formatProductPrice(product),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = if (product.stock > 0) {
                    "Existencias: ${product.stock} · En el pedido: $quantityInOrder"
                } else {
                    "Agotado"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = onAddToOrder,
                enabled = product.stock > 0 && !isBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(if (isBusy) "Procesando reserva…" else "Agregar al pedido")
            }

            Text("La prueba reserva existencias reales sin agregarlas al pedido. Úsala con un producto preparado para pruebas.")
            OutlinedButton(onClick = onConcurrencyTest, enabled = !isBusy) {
                Text(if (isTesting) "Probando…" else "Prueba de concurrencia · 20 intentos")
            }
            testResult?.let { Text(it) }

            orderFeedback?.let { feedback ->
                Surface(
                    color = if (feedback.isError) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    contentColor = if (feedback.isError) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = feedback.message,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = onDismissOrderFeedback,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onFavoriteClick,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    if (isFavorite) {
                        "Quitar de favoritos"
                    } else {
                        "Agregar a favoritos"
                    }
                )
            }

            HorizontalDivider()

            TextButton(
                onClick = {
                    showTechnicalDetails = !showTechnicalDetails
                }
            ) {
                Text(
                    if (showTechnicalDetails) {
                        "Ocultar detalles técnicos"
                    } else {
                        "Ver detalles técnicos"
                    }
                )
            }

            if (showTechnicalDetails) {
                Text(product.technicalDetails)
            }

            HorizontalDivider()

            OutlinedButton(
                onClick = {
                    onProfileClick(product.profileId)
                },
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Ver fabricante")
            }
        }
    }
}
