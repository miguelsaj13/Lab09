package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    products: List<Product>,
    favoriteIds: Set<Int>,
    onProductClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("VERSUS")
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
                text = "Productos destacados",
                style = MaterialTheme.typography.headlineMedium
            )

            products.forEach { product ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onProductClick(product.id)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(product.description)

                        Text(
                            text = "Q %.2f".format(product.price),
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(
                            onClick = {
                                onFavoriteClick(product.id)
                            }
                        ) {
                            Text(
                                if (product.id in favoriteIds) {
                                    "Favorito"
                                } else {
                                    "Agregar a favoritos"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}