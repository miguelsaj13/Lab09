package gt.uvg.lab09.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.formatProductPrice

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onProductClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    //Para catlog
    DisposableEffect(product.id) {
        Log.d(
            "CatalogProbe",
            "ENTER id=${product.id}"
        )

        onDispose {
            Log.d(
                "CatalogProbe",
                "EXIT id=${product.id}"
            )
        }
    }

    Card(
        modifier = modifier
            .clickable {
                onProductClick(product.id)
            }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProductImage(
                imageUrl = product.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = formatProductPrice(product),
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = if (product.stock > 0) {
                    "${product.stock} disponibles"
                } else {
                    "Agotado"
                },
                style = MaterialTheme.typography.bodySmall
            )

            TextButton(
                onClick = {
                    onFavoriteClick(product.id)
                }
            ) {
                Text(
                    if (isFavorite) {
                        "Favorito"
                    } else {
                        "Agregar a favoritos"
                    }
                )
            }
        }
    }
}
