package repository

import models.Category
import models.Product
import storage.IFileStorage

/**
 * Product Repository Implementation
 *
 * SOLID PRINCIPLES:
 * - S (SRP): Only handles product data access
 * - O (OCP): Can extend with new methods without modifying existing ones
 * - D (DIP): Depends on IFileStorage abstraction, not concrete implementation
 *
 * LEARNING:
 * - Repository pattern separates data access from business logic
 * - In-memory cache improves performance (reads from memory instead of file)
 * - Changes are immediately persisted to file
 */
class ProductRepository(
    private val fileStorage: IFileStorage  // Dependency injection via constructor
) : IRepository<Product> {

    companion object {
        private const val FILE_NAME = "products.txt"
    }

    /**
     * In-memory cache of products
     * LEARNING:
     * - MutableList allows modifications
     * - private set: others can read but only this class can modify
     * - Loaded from file on first access
     */
    private val products: MutableList<Product> = mutableListOf()

    /**
     * Initialization block - loads data from file
     * LEARNING: init{} runs after constructor
     */
    init {
        fileStorage.initializeFile(FILE_NAME)
        loadFromFile()
        println("[ProductRepo] Loaded ${products.size} products from file")
    }

    /**
     * Load products from file into memory
     * LEARNING: Private helper method for internal use only
     */
    private fun loadFromFile() {
        products.clear()  // Clear existing data
        val lines = fileStorage.readLines(FILE_NAME)

        lines.forEach { line ->
            // Parse each line and add to list
            Product.fromCsvLine(line)?.let { product ->
                products.add(product)
            }
        }
    }

    /**
     * Save all products to file
     * LEARNING: Persists in-memory changes to disk
     */
    private fun saveToFile() {
        val lines = products.map { it.toCsvLine() }
        fileStorage.writeLines(FILE_NAME, lines)
    }

    /**
     * Get all products
     * LEARNING: Returns immutable list (defensive copy)
     */
    override fun getAll(): List<Product> {
        return products.toList()  // Creates a copy
    }

    /**
     * Get product by ID
     * LEARNING:
     * - find{} is a higher-order function that takes a lambda
     * - Returns first match or null
     */
    override fun getById(id: String): Product? {
        return products.find { it.id == id }
    }

    /**
     * Add new product
     * LEARNING: Validates before adding
     */
    override fun add(entity: Product): Boolean {
        return try {
            // Check if ID already exists
            if (products.any { it.id == entity.id }) {
                println("[ProductRepo] Product with ID ${entity.id} already exists")
                return false
            }

            // Validate product
            if (!entity.isValid()) {
                println("[ProductRepo] Invalid product data")
                return false
            }

            // Add to memory and save to file
            products.add(entity)
            saveToFile()
            println("[ProductRepo] Added product: ${entity.name}")
            true
        } catch (e: Exception) {
            println("[ProductRepo] Error adding product: ${e.message}")
            false
        }
    }

    /**
     * Update existing product
     * LEARNING:
     * - indexOfFirst{} finds first matching index
     * - Returns -1 if not found
     */
    override fun update(entity: Product): Boolean {
        return try {
            val index = products.indexOfFirst { it.id == entity.id }
            if (index == -1) {
                println("[ProductRepo] Product not found: ${entity.id}")
                return false
            }

            if (!entity.isValid()) {
                println("[ProductRepo] Invalid product data")
                return false
            }

            // Update in memory and save
            products[index] = entity
            saveToFile()
            println("[ProductRepo] Updated product: ${entity.name}")
            true
        } catch (e: Exception) {
            println("[ProductRepo] Error updating product: ${e.message}")
            false
        }
    }

    /**
     * Delete product by ID
     * LEARNING: removeIf{} removes all matching elements
     */
    override fun delete(id: String): Boolean {
        return try {
            val removed = products.removeIf { it.id == id }
            if (removed) {
                saveToFile()
                println("[ProductRepo] Deleted product: $id")
            } else {
                println("[ProductRepo] Product not found: $id")
            }
            removed
        } catch (e: Exception) {
            println("[ProductRepo] Error deleting product: ${e.message}")
            false
        }
    }

    /**
     * Get count of products
     */
    override fun count(): Int {
        return products.size
    }

    // === PRODUCT-SPECIFIC METHODS ===

    /**
     * Search products by name (partial match)
     * LEARNING: filter{} returns all matching elements
     */
    fun searchByName(query: String): List<Product> {
        return products.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    /**
     * Get products by category
     */
    fun getByCategory(category: Category): List<Product> {
        return products.filter { it.category == category }
    }

    /**
     * Get low stock products
     * LEARNING: Configurable threshold
     */
    fun getLowStockProducts(threshold: Int = 10): List<Product> {
        return products.filter { it.quantityInStock <= threshold }
    }

    /**
     * Get out of stock products
     */
    fun getOutOfStockProducts(): List<Product> {
        return products.filter { it.quantityInStock == 0 }
    }

    /**
     * Update product quantity (for sales/restocking)
     * LEARNING: Specific business operation
     */
    fun updateQuantity(productId: String, newQuantity: Int): Boolean {
        val product = getById(productId) ?: return false
        val updatedProduct = product.copy(quantityInStock = newQuantity)
        return update(updatedProduct)
    }
}
