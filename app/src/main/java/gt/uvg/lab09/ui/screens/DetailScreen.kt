package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onProfileClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    var showTechnicalDetails by remember {
        mutableStateOf(false)
    }

    Scaffold(
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(product.description)

            Text(
                text = "Q %.2f".format(product.price),
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = onFavoriteClick
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

            Button(
                onClick = {
                    onProfileClick(product.profileId)
                }
            ) {
                Text("Ver fabricante")
            }
        }
    }
}