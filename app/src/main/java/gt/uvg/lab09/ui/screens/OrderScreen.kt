package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.OrderLine
import gt.uvg.lab09.model.formatProductPrice
import gt.uvg.lab09.model.formatQuetzales
import gt.uvg.lab09.viewModel.OrderFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    lines: List<OrderLine>,
    lineSubtotalsMinorUnits: Map<Int, Long>,
    totalMinorUnits: Long,
    feedback: OrderFeedback?,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onDismissFeedback: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pedido")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier.heightIn(min = 48.dp)
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
        ) {

            feedback?.let { currentFeedback ->
                OrderFeedbackMessage(
                    feedback = currentFeedback,
                    onDismiss = onDismissFeedback,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp
                    )
                )
            }

            if (lines.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Tu pedido está vacío",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Agrega productos desde su pantalla de detalle."
                        )

                        Text(
                            text = "Total: ${formatQuetzales(0L)}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = lines,
                        key = { line ->
                            line.product.id
                        }
                    ) { line ->

                        OrderLineCard(
                            line = line,
                            subtotalMinorUnits =
                                lineSubtotalsMinorUnits[line.product.id] ?: 0L,
                            onIncrease = {
                                onIncrease(line.product.id)
                            },
                            onDecrease = {
                                onDecrease(line.product.id)
                            },
                            onRemove = {
                                onRemove(line.product.id)
                            }
                        )
                    }

                    item(key = "total") {

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = formatQuetzales(totalMinorUnits),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderFeedbackMessage(
    feedback: OrderFeedback,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
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
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = feedback.message,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
private fun OrderLineCard(
    line: OrderLine,
    subtotalMinorUnits: Long,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = line.product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Precio unitario: ${formatProductPrice(line.product)}"
            )

            Text(
                text = "Subtotal: ${formatQuetzales(subtotalMinorUnits)}",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = onDecrease,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("-")
                }

                Text(
                    text = "Cantidad: ${line.quantity}",
                    style = MaterialTheme.typography.bodyLarge
                )

                TextButton(
                    onClick = onIncrease,
                    enabled = line.quantity < line.product.stock,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("+")
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = onRemove,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}