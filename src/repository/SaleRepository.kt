package repository

import models.Sale
import storage.IFileStorage

/**
 * Sale Repository Implementation
 *
 * SIMILAR TO ProductRepository BUT:
 * - Sales are append-only (no updates/deletes typically)
 * - Sales are ordered by timestamp
 * - Includes reporting methods
 */
class SaleRepository(
    private val fileStorage: IFileStorage
) : IRepository<Sale> {

    companion object {
        private const val FILE_NAME = "sales.txt"
    }

    private val sales: MutableList<Sale> = mutableListOf()

    init {
        fileStorage.initializeFile(FILE_NAME)
        loadFromFile()
        println("[SaleRepo] Loaded ${sales.size} sales from file")
    }

    private fun loadFromFile() {
        sales.clear()
        val lines = fileStorage.readLines(FILE_NAME)

        lines.forEach { line ->
            Sale.fromCsvLine(line)?.let { sale ->
                sales.add(sale)
            }
        }

        // Sort by timestamp (most recent first)
        sales.sortByDescending { it.timestamp }
    }

    private fun saveToFile() {
        val lines = sales.map { it.toCsvLine() }
        fileStorage.writeLines(FILE_NAME, lines)
    }

    override fun getAll(): List<Sale> {
        return sales.toList()
    }

    override fun getById(id: String): Sale? {
        return sales.find { it.id == id }
    }

    /**
     * Add new sale
     * LEARNING: Sales are typically append-only
     */
    override fun add(entity: Sale): Boolean {
        return try {
            sales.add(0, entity)  // Add at beginning (most recent)
            saveToFile()
            println("[SaleRepo] Added sale: ${entity.id}")
            true
        } catch (e: Exception) {
            println("[SaleRepo] Error adding sale: ${e.message}")
            false
        }
    }

    /**
     * Update sale - typically not allowed
     */
    override fun update(entity: Sale): Boolean {
        println("[SaleRepo] Warning: Sale updates are not recommended")
        return false
    }

    /**
     * Delete sale - for corrections only
     */
    override fun delete(id: String): Boolean {
        return try {
            val removed = sales.removeIf { it.id == id }
            if (removed) {
                saveToFile()
                println("[SaleRepo] Deleted sale: $id")
            }
            removed
        } catch (e: Exception) {
            println("[SaleRepo] Error deleting sale: ${e.message}")
            false
        }
    }

    override fun count(): Int {
        return sales.size
    }

    // === SALE-SPECIFIC METHODS ===

    /**
     * Get sales by product ID
     */
    fun getSalesByProduct(productId: String): List<Sale> {
        return sales.filter { it.productId == productId }
    }

    /**
     * Get sales in date range
     * LEARNING: Working with timestamps
     */
    fun getSalesInRange(startTime: Long, endTime: Long): List<Sale> {
        return sales.filter { it.timestamp in startTime..endTime }
    }

    /**
     * Get total revenue
     * LEARNING: sumOf{} is aggregate function
     */
    fun getTotalRevenue(): Double {
        return sales.sumOf { it.getTotalAmount() }
    }

    /**
     * Get today's sales
     */
    fun getTodaySales(): List<Sale> {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis

        return sales.filter { it.timestamp >= startOfDay }
    }
}