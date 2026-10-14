
package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.formatQuetzales
import gt.uvg.lab09.network.OrderDto
import gt.uvg.lab09.network.OrderLineDto
import gt.uvg.lab09.viewModel.RemoteUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderState: RemoteUiState<OrderDto>,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del pedido") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (orderState) {

                RemoteUiState.Idle,
                RemoteUiState.Loading -> {
                    CircularProgressIndicator()
                }

                RemoteUiState.Empty -> {
                    Text("No hay información del pedido.")
                }

                is RemoteUiState.Error -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = orderState.message,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(onClick = onRetryClick) {
                            Text("Reintentar")
                        }
                    }
                }

                is RemoteUiState.Success -> {
                    val order = orderState.data

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Pedido #${order.id}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }

                        item {
                            Text("Cliente: ${order.customerName}")
                            Text("Fecha: ${formatDetailDate(order.createdAt)}")
                            Text("Facturación: ${order.billingType}")
                            Text("Método de pago: ${order.paymentMethod}")
                        }

                        item {
                            HorizontalDivider()
                            Text(
                                text = "Productos",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        items(
                            items = order.lines
                        ) { line ->
                            OrderDetailLineCard(line)
                        }

                        item {
                            HorizontalDivider()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Text(
                                    text = formatQuetzales(
                                        order.totalMinorUnits
                                    ),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailLineCard(
    line: OrderLineDto
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = line.productName,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Cantidad: ${line.quantity}")

            Text(
                text = "Precio unitario: ${
                    formatQuetzales(line.unitPriceMinorUnits)
                }"
            )

            Text(
                text = "Subtotal: ${
                    formatQuetzales(
                        line.unitPriceMinorUnits * line.quantity
                    )
                }"
            )
        }
    }
}

private fun formatDetailDate(timestampSeconds: Long): String {
    val formatter = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )

    return formatter.format(Date(timestampSeconds * 1000L))
}