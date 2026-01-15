package ui


import models.Category
import models.Inventory
import models.Product
import services.IProductService
import services.ISaleService
import java.util.*

/**
 * Console-based User Interface
 *
 * SOLID PRINCIPLE: Single Responsibility Principle (SRP)
 * - Only handles user interaction through console
 * - Delegates business logic to services
 * - No data access, no business rules
 *
 * LEARNING:
 * - UI layer is the presentation layer
 * - Displays information and gets user input
 * - Calls services to perform operations
 */
class ConsoleUI(
    private val productService: IProductService,
    private val saleService: ISaleService
) : IUserInterface {

    private val scanner = Scanner(System.`in`)  // For reading user input

    /**
     * Start the application
     * LEARNING: Main loop of the application
     */
    override fun start() {
        displayWelcome()

        var running = true
        while (running) {
            displayMainMenu()

            when (readChoice()) {
                1 -> productMenu()
                2 -> salesMenu()
                3 -> reportsMenu()
                4 -> {
                    displayMessage("Thank you for using Supermarket Management System!")
                    running = false
                }

                else -> displayError("Invalid choice. Please try again.")
            }
        }
    }

    /**
     * Display welcome message
     */
    private fun displayWelcome() {
        println("\n" + "=".repeat(60))
        println("     SUPERMARKET MANAGEMENT SYSTEM")
        println("     Built with SOLID Principles in Kotlin")
        println("=".repeat(60))
    }

    /**
     * Display main menu
     * LEARNING: Clear menu structure improves UX
     */
    private fun displayMainMenu() {
        println("\n--- MAIN MENU ---")
        println("1. Product Management")
        println("2. Sales Management")
        println("3. Reports")
        println("4. Exit")
        print("\nEnter your choice: ")
    }

    /**
     * Read user choice
     * LEARNING: Input validation and error handling
     */
    private fun readChoice(): Int {
        return try {
            scanner.nextLine().toInt()
        } catch (e: Exception) {
            -1  // Invalid input
        }
    }

    /**
     * Read string input with prompt
     */
    private fun readString(prompt: String): String {
        print(prompt)
        return scanner.nextLine().trim()
    }

    /**
     * Read integer input with validation
     */
    private fun readInt(prompt: String): Int? {
        print(prompt)
        return try {
            scanner.nextLine().toInt()
        } catch (e: Exception) {
            displayError("Invalid number format")
            null
        }
    }

    /**
     * Read double input with validation
     */
    private fun readDouble(prompt: String): Double? {
        print(prompt)
        return try {
            scanner.nextLine().toDouble()
        } catch (e: Exception) {
            displayError("Invalid number format")
            null
        }
    }

    // ========== PRODUCT MANAGEMENT ==========

    /**
     * Product management submenu
     */
    private fun productMenu() {
        while (true) {
            println("\n--- PRODUCT MANAGEMENT ---")
            println("1. View All Products")
            println("2. Add New Product")
            println("3. Update Product")
            println("4. Delete Product")
            println("5. Search Products")
            println("6. View by Category")
            println("7. Restock Product")
            println("8. Back to Main Menu")
            print("\nEnter your choice: ")

            when (readChoice()) {
                1 -> viewAllProducts()
                2 -> addNewProduct()
                3 -> updateProduct()
                4 -> deleteProduct()
                5 -> searchProducts()
                6 -> viewByCategory()
                7 -> restockProduct()
                8 -> return
                else -> displayError("Invalid choice")
            }
        }
    }

    /**
     * View all products
     */
    private fun viewAllProducts() {
        val products = productService.getAllProducts()

        if (products.isEmpty()) {
            displayMessage("No products found.")
            return
        }

        println("\n" + "-".repeat(100))
        println("%-10s %-25s %-15s %-10s %-10s".format("ID", "Name", "Category", "Price", "Stock"))
        println("-".repeat(100))

        products.forEach { product ->
            println(
                "%-10s %-25s %-15s $%-9.2f %-10d".format(
                    product.id,
                    product.name.take(25),
                    product.category.displayName.take(15),
                    product.price,
                    product.quantityInStock
                )
            )
        }

        println("-".repeat(100))
        println("Total Products: ${products.size}")
    }

    /**
     * Add new product
     * LEARNING: Collecting user input step by step
     */
    private fun addNewProduct() {
        println("\n--- ADD NEW PRODUCT ---")

        val id = readString("Product ID: ")
        if (id.isBlank()) {
            displayError("Product ID cannot be empty")
            return
        }

        val name = readString("Product Name: ")
        if (name.isBlank()) {
            displayError("Product name cannot be empty")
            return
        }

        // Display categories
        println("\nAvailable Categories:")
        Category.getAllCategories().forEachIndexed { index, category ->
            println("${index + 1}. ${category.displayName}")
        }

        val categoryIndex = readInt("Select Category (1-${Category.values().size}): ")
        if (categoryIndex == null || categoryIndex !in 1..Category.values().size) {
            displayError("Invalid category selection")
            return
        }
        val category = Category.getAllCategories()[categoryIndex - 1]

        val price = readDouble("Price: ")
        if (price == null || price <= 0) {
            displayError("Price must be positive")
            return
        }

        val quantity = readInt("Initial Quantity: ")
        if (quantity == null || quantity < 0) {
            displayError("Quantity cannot be negative")
            return
        }

        // Create and add product
        val product = Product(id, name, category, price, quantity)

        if (productService.addProduct(product)) {
            displayMessage("Product added successfully!")
        } else {
            displayError("Failed to add product")
        }
    }

    /**
     * Update product
     */
    private fun updateProduct() {
        println("\n--- UPDATE PRODUCT ---")

        val id = readString("Enter Product ID to update: ")
        val existing = productService.getProductById(id)

        if (existing == null) {
            displayError("Product not found")
            return
        }

        println("Current Product: ${existing.name} - $${existing.price} - ${existing.quantityInStock} units")

        val name = readString("New Name (press Enter to keep current): ")
        val newName = if (name.isBlank()) existing.name else name

        val priceStr = readString("New Price (press Enter to keep current): ")
        val newPrice = if (priceStr.isBlank()) existing.price else priceStr.toDoubleOrNull() ?: existing.price

        val quantityStr = readString("New Quantity (press Enter to keep current): ")
        val newQuantity = if (quantityStr.isBlank()) existing.quantityInStock else quantityStr.toIntOrNull()
            ?: existing.quantityInStock

        val updatedProduct = existing.copy(
            name = newName,
            price = newPrice,
            quantityInStock = newQuantity
        )

        if (productService.updateProduct(updatedProduct)) {
            displayMessage("Product updated successfully!")
        } else {
            displayError("Failed to update product")
        }
    }

    /**
     * Delete product
     */
    private fun deleteProduct() {
        println("\n--- DELETE PRODUCT ---")

        val id = readString("Enter Product ID to delete: ")
        val product = productService.getProductById(id)

        if (product == null) {
            displayError("Product not found")
            return
        }

        println("Product: ${product.name}")
        val confirm = readString("Are you sure you want to delete? (yes/no): ")

        if (confirm.equals("yes", ignoreCase = true)) {
            if (productService.deleteProduct(id)) {
                displayMessage("Product deleted successfully!")
            } else {
                displayError("Failed to delete product")
            }
        } else {
            displayMessage("Deletion cancelled")
        }
    }

    /**
     * Search products
     */
    private fun searchProducts() {
        println("\n--- SEARCH PRODUCTS ---")

        val query = readString("Enter search query: ")
        val results = productService.searchProducts(query)

        if (results.isEmpty()) {
            displayMessage("No products found matching '$query'")
            return
        }

        println("\nSearch Results (${results.size} found):")
        results.forEach { product ->
            println("- ${product.id}: ${product.name} ($${product.price}) - ${product.quantityInStock} in stock")
        }
    }

    /**
     * View products by category
     */
    private fun viewByCategory() {
        println("\n--- VIEW BY CATEGORY ---")

        println("Available Categories:")
        Category.getAllCategories().forEachIndexed { index, category ->
            println("${index + 1}. ${category.displayName}")
        }

        val categoryIndex = readInt("Select Category: ")
        if (categoryIndex == null || categoryIndex !in 1..Category.values().size) {
            displayError("Invalid category selection")
            return
        }

        val category = Category.getAllCategories()[categoryIndex - 1]
        val products = productService.getProductsByCategory(category)

        if (products.isEmpty()) {
            displayMessage("No products in ${category.displayName}")
            return
        }

        println("\n${category.displayName} (${products.size} products):")
        products.forEach { product ->
            println("- ${product.id}: ${product.name} ($${product.price}) - ${product.quantityInStock} in stock")
        }
    }

    /**
     * Restock product
     */
    private fun restockProduct() {
        println("\n--- RESTOCK PRODUCT ---")

        val id = readString("Enter Product ID: ")
        val product = productService.getProductById(id)

        if (product == null) {
            displayError("Product not found")
            return
        }

        println("Current Stock: ${product.quantityInStock} units")

        val additionalQuantity = readInt("Enter quantity to add: ")
        if (additionalQuantity == null || additionalQuantity <= 0) {
            displayError("Quantity must be positive")
            return
        }

        if (productService.restockProduct(id, additionalQuantity)) {
            displayMessage("Product restocked successfully! New stock: ${product.quantityInStock + additionalQuantity}")
        } else {
            displayError("Failed to restock product")
        }
    }

    // ========== SALES MANAGEMENT ==========

    /**
     * Sales management submenu
     */
    private fun salesMenu() {
        while (true) {
            println("\n--- SALES MANAGEMENT ---")
            println("1. Record New Sale")
            println("2. View All Sales")
            println("3. View Today's Sales")
            println("4. Back to Main Menu")
            print("\nEnter your choice: ")

            when (readChoice()) {
                1 -> recordNewSale()
                2 -> viewAllSales()
                3 -> viewTodaysSales()
                4 -> return
                else -> displayError("Invalid choice")
            }
        }
    }

    /**
     * Record new sale
     */
    private fun recordNewSale() {
        println("\n--- RECORD NEW SALE ---")

        val productId = readString("Enter Product ID: ")
        val product = productService.getProductById(productId)

        if (product == null) {
            displayError("Product not found")
            return
        }

        println("Product: ${product.name}")
        println("Price: $${product.price}")
        println("Available Stock: ${product.quantityInStock}")

        val quantity = readInt("Enter quantity to sell: ")
        if (quantity == null || quantity <= 0) {
            displayError("Quantity must be positive")
            return
        }

        if (saleService.recordSale(productId, quantity)) {
            displayMessage("Sale recorded successfully!")
        } else {
            displayError("Failed to record sale")
        }
    }

    /**
     * View all sales
     */
    private fun viewAllSales() {
        val sales = saleService.getAllSales()

        if (sales.isEmpty()) {
            displayMessage("No sales recorded yet.")
            return
        }

        println("\n" + "-".repeat(110))
        println(
            "%-20s %-20s %-25s %-8s %-10s %-10s".format(
                "Sale ID", "Date/Time", "Product", "Qty", "Price", "Total"
            )
        )
        println("-".repeat(110))

        sales.take(20).forEach { sale ->  // Show last 20 sales
            println(
                "%-20s %-20s %-25s %-8d $%-9.2f $%-9.2f".format(
                    sale.id.take(20),
                    sale.getFormattedDate(),
                    sale.productName.take(25),
                    sale.quantity,
                    sale.pricePerUnit,
                    sale.getTotalAmount()
                )
            )
        }

        println("-".repeat(110))
        println("Showing ${minOf(20, sales.size)} of ${sales.size} total sales")
    }

    /**
     * View today's sales
     */
    private fun viewTodaysSales() {
        val sales = saleService.getTodaysSales()

        if (sales.isEmpty()) {
            displayMessage("No sales today.")
            return
        }

        println("\n--- TODAY'S SALES ---")
        sales.forEach { sale ->
            println("${sale.getFormattedDate()} | ${sale.productName} x${sale.quantity} = $${sale.getTotalAmount()}")
        }

        val todaysRevenue = saleService.getTodaysRevenue()
        println("\nToday's Total Revenue: $${"%.2f".format(todaysRevenue)}")
    }

    // ========== REPORTS ==========

    /**
     * Reports submenu
     */
    private fun reportsMenu() {
        while (true) {
            println("\n--- REPORTS ---")
            println("1. Inventory Report")
            println("2. Sales Report")
            println("3. Low Stock Alert")
            println("4. Back to Main Menu")
            print("\nEnter your choice: ")

            when (readChoice()) {
                1 -> showInventoryReport()
                2 -> showSalesReport()
                3 -> showLowStockAlert()
                4 -> return
                else -> displayError("Invalid choice")
            }
        }
    }

    /**
     * Show inventory report
     */
    private fun showInventoryReport() {
        val inventory = productService.generateInventoryReport()
        println("\n" + inventory.generateReport())
    }

    /**
     * Show sales report
     */
    private fun showSalesReport() {
        val report = saleService.generateSalesReport()
        println("\n" + report)
    }

    /**
     * Show low stock alert
     */
    private fun showLowStockAlert() {
        val inventory = productService.generateInventoryReport()

        println("\n--- LOW STOCK ALERT ---")

        if (inventory.outOfStockProducts.isNotEmpty()) {
            println("OUT OF STOCK:")
            inventory.outOfStockProducts.forEach {
                println("! ${it.name} (${it.id}) - OUT OF STOCK")
            }
        }

        if (inventory.lowStockProducts.isNotEmpty()) {
            println("\nLOW STOCK (below ${Inventory.LOW_STOCK_THRESHOLD} units):")
            inventory.lowStockProducts.forEach {
                println("  ${it.name} (${it.id}) - ${it.quantityInStock} units")
            }
        }

        if (inventory.outOfStockProducts.isEmpty() && inventory.lowStockProducts.isEmpty()) {
            displayMessage("All products are adequately stocked!")
        }
    }

    /**
     * Display normal message
     */
    override fun displayMessage(message: String) {
        println("\n✓ $message")
    }

    /**
     * Display error message
     */
    override fun displayError(message: String) {
        println("\n✗ ERROR: $message")
    }
}
