package storage

/**
 * File Storage Interface
 *
 * SOLID PRINCIPLES DEMONSTRATED:
 * - D (Dependency Inversion): High-level modules depend on this abstraction
 * - I (Interface Segregation): Focused interface for file operations only
 * - O (Open/Closed): Can create new implementations without changing existing code
 *
 * LEARNING: Interfaces define WHAT, not HOW
 * Any class implementing this can provide different storage mechanisms
 * (text files, JSON files, databases, cloud storage, etc.)
 */

interface IFileStorage {
    /**
     * Read all lines from a file
     * @param fileName Name of the file to read
     * @return List of lines, empty list if file doesn't exist
     */
    fun readLines(fileName: String): List<String>

    /**
     * Write lines to a file (overwrites existing content)
     * @param fileName Name of the file to write
     * @param lines List of lines to write
     */
    fun writeLines(fileName: String, lines: List<String>)

    /**
     * Append a line to a file
     * @param fileName Name of the file to append to
     * @param line Line to append
     */
    fun appendLine(fileName: String, line: String)

    /**
     * Check if file exists
     * @param fileName Name of the file to check
     * @return true if file exists, false otherwise
     */
    fun fileExists(fileName: String): Boolean

    /**
     * Initialize file if it doesn't exist
     * @param fileName Name of the file to create
     */
    fun initializeFile(fileName: String)
}