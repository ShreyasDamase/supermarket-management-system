package ui

import services.IProductService
import services.ISaleService

/**
 * User Interface Interface
 *
 * SOLID PRINCIPLE: Dependency Inversion Principle (DIP)
 * - Application logic depends on this interface
 * - Can be implemented as Console, GUI, Web, Mobile, etc.
 */
interface IUserInterface {
    fun start()
    fun displayMessage(message: String)
    fun displayError(message: String)
}
