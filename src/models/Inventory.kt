package models

data class Inventory(
    val totalProduct: String,
    val totalValue: Double,
    val lowStockProducts: List<Product>,
    val outOfStockProducts: List<Product>
) {

    companion object {
        const val LOW_STOCK_THRESHOLD = 10
    }

    fun generateReport(): String {
        return buildString {
            appendLine("=== INVENTORY SUMMARY ===")
            appendLine("Total Products:$totalProduct")
            appendLine("Total Inventory Values:$${"%.2f".format(totalValue)}")
            appendLine("Low Stock Items: ${lowStockProducts.size}")
            appendLine("Out of Stock Items: ${outOfStockProducts.size}")

            if (lowStockProducts.isEmpty()) {
                appendLine("\n--- Low Stock Products ---")
                lowStockProducts.forEach {
                    appendLine("${it.name}:${it.quantityInStock} units")
                }
            }

            if (outOfStockProducts.isEmpty()) {
                appendLine("\n--- Out of Stock Products ---")
                outOfStockProducts.forEach {
                    appendLine("${it.name}:${it.quantityInStock} OUT OF STOCK")
                }
            }

        }
    }
}
