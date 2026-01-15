import repository.ProductRepository
import repository.SaleRepository
import services.IProductService
import services.ISaleService
import services.ProductService
import services.SaleService
import storage.IFileStorage
import storage.TextFileStorage
import ui.ConsoleUI
import ui.IUserInterface

// ==================== Main.kt ====================
/**
 * Main Entry Point
 *
 * SOLID PRINCIPLES IN ACTION:
 * - D (Dependency Inversion): Main creates concrete classes but injects them as interfaces
 * - S (Single Responsibility): Each class has clear responsibilities
 *
 * DESIGN PATTERN: Dependency Injection (Manual/Constructor Injection)
 * - Dependencies are created and injected through constructors
 * - Makes code testable and flexible
 *
 * LEARNING:
 * - Main function is the composition root
 * - Create all dependencies here
 * - Wire them together
 * - Start the application
 */
fun main() {
    println("Initializing Supermarket Management System...")

    // 1. Create storage layer (lowest level)
    val fileStorage: IFileStorage = TextFileStorage("data")

    // 2. Create repositories (data access layer)
    val productRepository = ProductRepository(fileStorage)
    val saleRepository = SaleRepository(fileStorage)

    // 3. Create services (business logic layer)
    val productService: IProductService = ProductService(productRepository)
    val saleService: ISaleService = SaleService(saleRepository, productRepository)

    // 4. Create UI (presentation layer)
    val ui: IUserInterface = ConsoleUI(productService, saleService)

    // 5. Start the application
    try {
        ui.start()
    } catch (e: Exception) {
        println("Application error: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * LEARNING SUMMARY:
 *
 * PROJECT ARCHITECTURE:
 * ┌──────────────────────────────────────┐
 * │         Presentation Layer           │
 * │            (ConsoleUI)               │
 * ├──────────────────────────────────────┤
 * │         Business Logic Layer         │
 * │    (ProductService, SaleService)     │
 * ├──────────────────────────────────────┤
 * │         Data Access Layer            │
 * │  (ProductRepository, SaleRepository) │
 * ├──────────────────────────────────────┤
 * │         Storage Layer                │
 * │        (TextFileStorage)             │
 * └──────────────────────────────────────┘
 *
 * KEY CONCEPTS:
 * - Separation of Concerns: Each layer has specific responsibilities
 * - Dependency Flow: Upper layers depend on lower layers through interfaces
 * - Testability: Each layer can be tested independently
 * - Maintainability: Changes in one layer don't affect others
 * - Extensibility: Easy to add new features or swap implementations
 */