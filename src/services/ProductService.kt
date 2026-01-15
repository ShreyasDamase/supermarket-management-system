package services

import models.Category
import models.Inventory
import models.Product
import repository.ProductRepository

/**
 * Product Service Implementation
 *
 * SOLID PRINCIPLES:
 * - S (SRP): Handles product-related business logic only
 * - O (OCP): Can extend functionality without modifying existing code
 * - D (DIP): Depends on IRepository interface, not concrete implementation
 *
 * LEARNING:
 * - Service layer is where business rules are enforced
 * - Service coordinates multiple repositories if needed
 * - Keeps business logic separate from data access
 */
class ProductService(
    private val productRepository: ProductRepository  // Injected dependency
) : IProductService {

    /**
     * Get all products
     * LEARNING: Service just delegates to repository
     */
    override fun getAllProducts(): List<Product> {
        return productRepository.getAll()
    }

    /**
     * Get product by ID
     */
    override fun getProductById(id: String): Product? {
        return productRepository.getById(id)
    }

    /**
     * Add new product
     * LEARNING: Service can add additional validation/business rules
     */
    override fun addProduct(product: Product): Boolean {
        // Business rule: Check for duplicate names (optional)
        val existingProducts = productRepository.searchByName(product.name)
        if (existingProducts.any { it.name.equals(product.name, ignoreCase = true) }) {
            println("[ProductService] Warning: Product with similar name exists")
        }

        return productRepository.add(product)
    }

    /**
     * Update existing product
     */
    override fun updateProduct(product: Product): Boolean {
        // Business rule: Verify product exists
        val existing = productRepository.getById(product.id)
        if (existing == null) {
            println("[ProductService] Cannot update non-existent product")
            return false
        }

        return productRepository.update(product)
    }

    /**
     * Delete product
     * LEARNING: Service can check business rules before deleting
     */
    override fun deleteProduct(id: String): Boolean {
        // Business rule: Could check if product has pending orders
        // For now, simple deletion
        return productRepository.delete(id)
    }

    /**
     * Search products by name
     */
    override fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) {
            return emptyList()
        }
        return productRepository.searchByName(query)
    }

    /**
     * Get products by category
     */
    override fun getProductsByCategory(category: Category): List<Product> {
        return productRepository.getByCategory(category)
    }

    /**
     * Generate comprehensive inventory report
     * LEARNING:
     * - Business logic for generating reports
     * - Aggregates data from repository
     * - Applies business rules (low stock threshold)
     */
    override fun generateInventoryReport(): Inventory {
        val allProducts = productRepository.getAll()

        // Calculate total inventory value
        val totalValue = allProducts.sumOf {
            it.price * it.quantityInStock
        }

        // Find low stock products
        val lowStock = productRepository.getLowStockProducts(
            Inventory.LOW_STOCK_THRESHOLD
        )

        // Find out of stock products
        val outOfStock = productRepository.getOutOfStockProducts()

        return Inventory(
            totalProducts = allProducts.size,
            totalValue = totalValue,
            lowStockProducts = lowStock,
            outOfStockProducts = outOfStock
        )
    }

    /**
     * Restock product - add quantity to existing stock
     * LEARNING: Business operation that modifies data
     */
    override fun restockProduct(productId: String, additionalQuantity: Int): Boolean {
        // Validation
        if (additionalQuantity <= 0) {
            println("[ProductService] Restock quantity must be positive")
            return false
        }

        // Get current product
        val product = productRepository.getById(productId)
        if (product == null) {
            println("[ProductService] Product not found")
            return false
        }

        // Calculate new quantity
        val newQuantity = product.quantityInStock + additionalQuantity

        // Update quantity
        return productRepository.updateQuantity(productId, newQuantity)
    }
}