package gt.uvg.lab09.viewModel

import gt.uvg.lab09.model.Product

fun filterProducts(
    products: List<Product>,
    query: String
): List<Product> {
    val normalizedQuery = query.trim()

    if (normalizedQuery.isEmpty()) {
        return products
    }

    return products.filter { product ->
        product.name.contains(
            other = normalizedQuery,
            ignoreCase = true
        )
    }
}