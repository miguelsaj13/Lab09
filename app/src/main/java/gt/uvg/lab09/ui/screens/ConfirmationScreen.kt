package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.BillingType
import gt.uvg.lab09.model.OrderReceipt
import gt.uvg.lab09.model.PaymentMethod
import gt.uvg.lab09.model.formatQuetzales

@Composable
fun ConfirmationScreen(
    receipt: OrderReceipt?,
    onCatalogClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "¡Pedido confirmado!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Orden registrada exitosamente en nuestra tienda.",
                textAlign = TextAlign.Center
            )

            if (receipt == null) {
                Text(
                    text = "No hay un recibo disponible.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                ReceiptCard(receipt = receipt)
            }

            Button(
                onClick = onCatalogClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Volver al catálogo")
            }
        }
    }
}

@Composable
private fun ReceiptCard(receipt: OrderReceipt) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReceiptRow(label = "Folio", value = receipt.folio)
            ReceiptRow(label = "Cliente", value = receipt.customerName)
            ReceiptRow(
                label = "Facturación",
                value = when (receipt.billingType) {
                    BillingType.CONSUMIDOR_FINAL -> "CF (Consumidor Final)"
                    BillingType.FACTURA_CON_NIT -> "Factura con NIT"
                }
            )
            if (receipt.billingType == BillingType.FACTURA_CON_NIT) {
                ReceiptRow(label = "NIT", value = receipt.nit.orEmpty())
                ReceiptRow(
                    label = "Razón social",
                    value = receipt.businessName.orEmpty()
                )
            }
            ReceiptRow(
                label = "Método de pago",
                value = when (receipt.paymentMethod) {
                    PaymentMethod.EFECTIVO -> "Efectivo contra entrega"
                    PaymentMethod.TRANSFERENCIA_BANCARIA -> "Transferencia bancaria"
                }
            )
            ReceiptRow(
                label = "Total del pedido",
                value = formatQuetzales(receipt.totalMinorUnits)
            )
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.4f),
            textAlign = TextAlign.End
        )
    }
}
