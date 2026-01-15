package models

data class Inventory(
    val totalProducts: Int,
    val totalValue: Double,
    val lowStockProducts: List<Product>,  // Products below threshold
    val outOfStockProducts: List<Product>
) {
    companion object {
        const val LOW_STOCK_THRESHOLD = 10  // Items below this are "low stock"
    }

    /**
     * Generate inventory report
     */
    fun generateReport(): String {
        return buildString {
            appendLine("=== INVENTORY SUMMARY ===")
            appendLine("Total Products: $totalProducts")
            appendLine("Total Inventory Value: $${"%.2f".format(totalValue)}")
            appendLine("Low Stock Items: ${lowStockProducts.size}")
            appendLine("Out of Stock Items: ${outOfStockProducts.size}")

            if (lowStockProducts.isNotEmpty()) {
                appendLine("\n--- Low Stock Products ---")
                lowStockProducts.forEach {
                    appendLine("${it.name}: ${it.quantityInStock} units")
                }
            }

            if (outOfStockProducts.isNotEmpty()) {
                appendLine("\n--- Out of Stock Products ---")
                outOfStockProducts.forEach {
                    appendLine("${it.name}: OUT OF STOCK")
                }
            }
        }
    }
}