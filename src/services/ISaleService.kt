package services

import models.Sale
import repository.ProductRepository
import repository.SaleRepository

/**
 * Sale Service Interface
 *
 * SOLID PRINCIPLE: Interface Segregation Principle (ISP)
 * - Focused on sale-related operations only
 * - Separate from product operations
 */
interface ISaleService {
    fun getAllSales(): List<Sale>
    fun getSaleById(id: String): Sale?
    fun recordSale(productId: String, quantity: Int): Boolean
    fun getTodaysSales(): List<Sale>
    fun getTotalRevenue(): Double
    fun getTodaysRevenue(): Double
    fun generateSalesReport(): String
}

// ==================== services/SaleService.kt ====================
/**
 * Sale Service Implementation
 *
 * SOLID PRINCIPLES:
 * - S (SRP): Handles sale-related business logic
 * - D (DIP): Depends on interfaces, not concrete classes
 *
 * LEARNING:
 * - Service coordinates between Product and Sale repositories
 * - Enforces business rules (stock availability, price validation)
 * - Handles transactional operations (sale + stock update)
 */
class SaleService(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository  // Needs both repositories
) : ISaleService {

    /**
     * Get all sales
     */
    override fun getAllSales(): List<Sale> {
        return saleRepository.getAll()
    }

    /**
     * Get sale by ID
     */
    override fun getSaleById(id: String): Sale? {
        return saleRepository.getById(id)
    }

    /**
     * Record a sale
     * LEARNING: Complex business operation
     * - Validates product availability
     * - Creates sale record
     * - Updates inventory
     * - All or nothing (transactional)
     */
    override fun recordSale(productId: String, quantity: Int): Boolean {
        // Validation: quantity must be positive
        if (quantity <= 0) {
            println("[SaleService] Sale quantity must be positive")
            return false
        }

        // Get product
        val product = productRepository.getById(productId)
        if (product == null) {
            println("[SaleService] Product not found")
            return false
        }

        // Business rule: Check stock availability
        if (product.quantityInStock < quantity) {
            println("[SaleService] Insufficient stock. Available: ${product.quantityInStock}, Requested: $quantity")
            return false
        }

        try {
            // Create sale record
            val sale = Sale(
                id = generateSaleId(),
                productId = product.id,
                productName = product.name,
                quantity = quantity,
                pricePerUnit = product.price,
                timestamp = System.currentTimeMillis()
            )

            // Add sale to repository
            if (!saleRepository.add(sale)) {
                println("[SaleService] Failed to record sale")
                return false
            }

            // Update product quantity
            val newQuantity = product.quantityInStock - quantity
            if (!productRepository.updateQuantity(productId, newQuantity)) {
                // Rollback: remove the sale we just added
                saleRepository.delete(sale.id)
                println("[SaleService] Failed to update inventory, sale rolled back")
                return false
            }

            println("[SaleService] Sale recorded successfully: ${sale.id}")
            println("  Product: ${product.name}")
            println("  Quantity: $quantity")
            println("  Total: ${"%.2f".format(sale.getTotalAmount())}")
            println("  Remaining stock: $newQuantity")

            return true
        } catch (e: Exception) {
            println("[SaleService] Error recording sale: ${e.message}")
            return false
        }
    }

    /**
     * Generate unique sale ID
     * LEARNING: Simple ID generation strategy
     * In production, you might use UUID or database sequences
     */
    private fun generateSaleId(): String {
        return "SALE${System.currentTimeMillis()}"
    }

    /**
     * Get today's sales
     */
    override fun getTodaysSales(): List<Sale> {
        return saleRepository.getTodaySales()
    }

    /**
     * Get total revenue (all time)
     */
    override fun getTotalRevenue(): Double {
        return saleRepository.getTotalRevenue()
    }

    /**
     * Get today's revenue
     * LEARNING: Calculates sum of today's sales
     */
    override fun getTodaysRevenue(): Double {
        return getTodaysSales().sumOf { it.getTotalAmount() }
    }

    /**
     * Generate sales report
     * LEARNING: Business logic for formatting reports
     */
    override fun generateSalesReport(): String {
        val allSales = saleRepository.getAll()
        val todaysSales = getTodaysSales()
        val totalRevenue = getTotalRevenue()
        val todaysRevenue = getTodaysRevenue()

        return buildString {
            appendLine("=== SALES REPORT ===")
            appendLine("Total Sales: ${allSales.size}")
            appendLine("Total Revenue: $${"%.2f".format(totalRevenue)}")
            appendLine()
            appendLine("Today's Sales: ${todaysSales.size}")
            appendLine("Today's Revenue: $${"%.2f".format(todaysRevenue)}")
            appendLine()

            if (todaysSales.isNotEmpty()) {
                appendLine("--- Recent Sales (Today) ---")
                todaysSales.take(10).forEach { sale ->
                    appendLine(
                        "${sale.getFormattedDate()} | ${sale.productName} x${sale.quantity} = ${
                            "%.2f".format(
                                sale.getTotalAmount()
                            )
                        }"
                    )
                }
            }

            // Top selling products
            appendLine()
            appendLine("--- Top Selling Products ---")
            val productSales = allSales.groupBy { it.productId }
            val topProducts = productSales.entries
                .map { (productId, sales) ->
                    val totalQuantity = sales.sumOf { it.quantity }
                    val productName = sales.firstOrNull()?.productName ?: "Unknown"
                    Pair(productName, totalQuantity)
                }
                .sortedByDescending { it.second }
                .take(5)

            topProducts.forEachIndexed { index, (name, quantity) ->
                appendLine("${index + 1}. $name - $quantity units sold")
            }
        }
    }
}