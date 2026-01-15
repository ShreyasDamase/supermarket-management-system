package repository

/**
 * Generic Repository Interface
 *
 * DESIGN PATTERN: Repository Pattern
 * - Mediates between domain and data mapping layers
 * - Provides collection-like interface for accessing domain objects
 *
 * SOLID PRINCIPLE: Interface Segregation Principle (ISP)
 * - Generic interface with common CRUD operations
 * - Each entity can extend with specific methods
 *
 * LEARNING: Generic interfaces allow code reuse across different entity types
 * <T> is a type parameter - could be Product, Sale, etc.
 */
interface IRepository<T> {
    /**
     * Get all entities
     */
    fun getAll(): List<T>

    /**
     * Get entity by ID
     */
    fun getById(id: String): T?

    /**
     * Add new entity
     * @return true if successful, false otherwise
     */
    fun add(entity: T): Boolean

    /**
     * Update existing entity
     * @return true if successful, false otherwise
     */
    fun update(entity: T): Boolean

    /**
     * Delete entity by ID
     * @return true if successful, false otherwise
     */
    fun delete(id: String): Boolean

    /**
     * Get count of all entities
     */
    fun count(): Int
}