package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.RemoteOrder
import gt.uvg.lab09.model.formatQuetzales
import gt.uvg.lab09.viewModel.RemoteUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    ordersState: RemoteUiState<List<RemoteOrder>>,
    onOrderClick: (String) -> Unit,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Mis pedidos")
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (ordersState) {

                RemoteUiState.Idle,
                RemoteUiState.Loading -> {
                    CircularProgressIndicator()
                }

                RemoteUiState.Empty -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Aún no has enviado pedidos.",
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(
                            onClick = onBackClick
                        ) {
                            Text("Ir al catálogo")
                        }
                    }
                }

                is RemoteUiState.Error -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = ordersState.message,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(
                            onClick = onRetryClick
                        ) {
                            Text("Reintentar")
                        }
                    }
                }

                is RemoteUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = ordersState.data,
                            key = { order -> order.id }
                        ) { order ->
                            OrderSummaryCard(
                                order = order,
                                onClick = {
                                    onOrderClick(order.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    order: RemoteOrder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pedido #${order.id}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatQuetzales(order.totalMinorUnits),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = formatOrderDate(order.createdAt),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = order.customerName,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatOrderDate(timestampSeconds: Long): String {
    val formatter = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )

    return formatter.format(Date(timestampSeconds * 1000L))
}
