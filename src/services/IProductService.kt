package services

import models.Category
import models.Inventory
import models.Product

/**
 * Product Service Interface
 *
 * SOLID PRINCIPLE: Dependency Inversion Principle (DIP)
 * - High-level UI module depends on this interface
 * - Not on concrete ProductService implementation
 *
 * LEARNING: Service layer contains business logic
 * - Coordinates between repositories
 * - Validates business rules
 * - Performs complex operations
 */
interface IProductService {
    fun getAllProducts(): List<Product>
    fun getProductById(id: String): Product?
    fun addProduct(product: Product): Boolean
    fun updateProduct(product: Product): Boolean
    fun deleteProduct(id: String): Boolean
    fun searchProducts(query: String): List<Product>
    fun getProductsByCategory(category: Category): List<Product>
    fun generateInventoryReport(): Inventory
    fun restockProduct(productId: String, additionalQuantity: Int): Boolean
}
