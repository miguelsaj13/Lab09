package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.Product
import gt.uvg.lab09.model.ProductSort
import kotlinx.coroutines.launch

private const val SCROLL_TO_TOP_THRESHOLD = 4

private val FAB_CLEARANCE = 88.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    products: List<Product>,
    totalCount: Int,
    query: String,
    favoriteIds: Set<Int>,
    gridState: LazyGridState,
    onQueryChange: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    orderUnitCount: Int,
    onOrderClick: () -> Unit,
    onMyOrdersClick: () -> Unit,
    isLoading: Boolean,
    error: String?,
    isFromCache: Boolean,
    onRetryClick: () -> Unit,
    sort: ProductSort,
    onSortChange: (ProductSort) -> Unit,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex >= SCROLL_TO_TOP_THRESHOLD
        }
    }

    val handleQueryChange: (String) -> Unit = { newQuery ->
        onQueryChange(newQuery)

        coroutineScope.launch {
            gridState.scrollToItem(0)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("VERSUS")
                },
                actions = {
                    TextButton(
                        onClick = onMyOrdersClick,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Mis pedidos")
                    }

                    TextButton(
                        onClick = onOrderClick,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Pedido ($orderUnitCount)")
                    }
                }
            )
        },
        floatingActionButton = {
            ScrollToTopButton(
                visible = showScrollToTop,
                onClick = {
                    coroutineScope.launch {
                        gridState.animateScrollToItem(0)
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {

            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Tema oscuro", modifier = Modifier.weight(1f))
                Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
            }
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                ProductSort.entries.forEach { option ->
                    TextButton(onClick = { onSortChange(option) }, enabled = sort != option) {
                        Text(when (option) {
                            ProductSort.DEFAULT -> "Original"
                            ProductSort.NAME -> "Nombre"
                            ProductSort.PRICE_ASCENDING -> "Precio ↑"
                            ProductSort.PRICE_DESCENDING -> "Precio ↓"
                        })
                    }
                }
            }
            if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            if (error != null) {
                Text(error, modifier = Modifier.padding(16.dp))
                TextButton(onClick = onRetryClick) { Text("Reintentar") }
            }
            if (isFromCache && !isLoading) Text("Catálogo guardado; las reservas necesitan conexión.", modifier = Modifier.padding(16.dp))
            ProductSearchField(
                query = query,
                onQueryChange = handleQueryChange,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp
                )
            )

            Text(
                text = "${products.size} de $totalCount productos",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )

            if (products.isEmpty() && !isLoading && error == null) {
                EmptySearchState(
                    onClearClick = {
                        handleQueryChange("")
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = innerPadding.calculateBottomPadding() + FAB_CLEARANCE
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = products,
                        key = { product ->
                            product.id
                        }
                    ) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = product.id in favoriteIds,
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
