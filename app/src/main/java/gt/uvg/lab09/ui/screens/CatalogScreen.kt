package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.Product
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
    onFavoriteClick: (Int) -> Unit
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
        topBar = {
            TopAppBar(
                title = {
                    Text("VERSUS")
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

            if (products.isEmpty()) {
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