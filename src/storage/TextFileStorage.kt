package storage

import java.io.File
import java.io.IOException

/**
 * Text File Storage Implementation
 *
 * SOLID PRINCIPLE: Single Responsibility Principle (SRP)
 * - This class has ONE job: handle text file operations
 * - No business logic, no data validation, no UI
 *
 * BEST PRACTICES IMPLEMENTED:
 * 1. Proper exception handling with try-catch
 * 2. Using Kotlin's use{} for automatic resource management
 * 3. Creating directory structure if it doesn't exist
 * 4. UTF-8 encoding for text files
 */
class TextFileStorage(
    private val dataDirectory: String = "data"
) : IFileStorage {

    /**
     * Initialize storage by creating data directory
     * LEARNING: init{} blocks run when object is created
     */
    init {
        val dir = File(dataDirectory)
        if (!dir.exists()) {
            dir.mkdirs()  // Create directory and parent directories
            println("[Storage] Created data directory: ${dir.absolutePath}")
        }
    }

    /**
     * Get full file path
     * LEARNING: Helper functions keep code DRY (Don't Repeat Yourself)
     */
    private fun getFilePath(fileName: String): String {
        return "$dataDirectory/$fileName"
    }

    /**
     * Read all lines from a file
     *
     * LEARNING:
     * - File.readLines() reads entire file into memory
     * - Good for small to medium files
     * - For large files, use File.useLines{} or BufferedReader
     */
    override fun readLines(fileName: String): List<String> {
        return try {
            val file = File(getFilePath(fileName))
            if (file.exists()) {
                file.readLines(Charsets.UTF_8)  // UTF-8 encoding
            } else {
                println("[Storage] File not found: $fileName, returning empty list")
                emptyList()
            }
        } catch (e: IOException) {
            println("[Storage] Error reading file $fileName: ${e.message}")
            emptyList()
        }
    }

    /**
     * Write lines to a file (overwrites existing content)
     *
     * LEARNING:
     * - bufferedWriter() creates a BufferedWriter for efficient writing
     * - use{} automatically closes the writer, even if exception occurs
     * - This prevents resource leaks
     */
    override fun writeLines(fileName: String, lines: List<String>) {
        try {
            val file = File(getFilePath(fileName))

            // Ensure parent directory exists
            file.parentFile?.mkdirs()

            // Write lines with buffered writer
            file.bufferedWriter(Charsets.UTF_8).use { writer ->
                lines.forEach { line ->
                    writer.write(line)
                    writer.newLine()
                }
            }
            println("[Storage] Successfully wrote ${lines.size} lines to $fileName")
        } catch (e: IOException) {
            println("[Storage] Error writing to file $fileName: ${e.message}")
        }
    }

    /**
     * Append a line to a file
     *
     * LEARNING:
     * - appendText() adds content to end of file
     * - Creates file if it doesn't exist
     * - Efficient for logging or adding single records
     */
    override fun appendLine(fileName: String, line: String) {
        try {
            val file = File(getFilePath(fileName))

            // Ensure parent directory exists
            file.parentFile?.mkdirs()

            // Append with newline
            file.appendText(line + System.lineSeparator(), Charsets.UTF_8)
            println("[Storage] Appended line to $fileName")
        } catch (e: IOException) {
            println("[Storage] Error appending to file $fileName: ${e.message}")
        }
    }

    /**
     * Check if file exists
     */
    override fun fileExists(fileName: String): Boolean {
        return File(getFilePath(fileName)).exists()
    }

    /**
     * Initialize file with headers or empty content
     *
     * LEARNING: Idempotent operation - safe to call multiple times
     */
    override fun initializeFile(fileName: String) {
        val file = File(getFilePath(fileName))
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
            println("[Storage] Initialized file: $fileName")
        }
    }

    /**
     * ADVANCED: Read large file line by line
     * USE CASE: When file is too large to fit in memory
     *
     * LEARNING:
     * - useLines{} reads file lazily (one line at a time)
     * - Returns a Sequence for memory efficiency
     * - Automatically closes file after processing
     */
    fun readLinesLazy(fileName: String): Sequence<String> {
        return try {
            val file = File(getFilePath(fileName))
            if (file.exists()) {
                file.useLines { it }  // Returns Sequence<String>
            } else {
                emptySequence()
            }
        } catch (e: IOException) {
            println("[Storage] Error reading file lazily $fileName: ${e.message}")
            emptySequence()
        }
    }

    /**
     * Delete a file
     * LEARNING: Additional utility method
     */
    fun deleteFile(fileName: String): Boolean {
        return try {
            val file = File(getFilePath(fileName))
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            println("[Storage] Error deleting file $fileName: ${e.message}")
            false
        }
    }

    /**
     * List all files in data directory
     */
    fun listFiles(): List<String> {
        val dir = File(dataDirectory)
        return dir.listFiles()?.map { it.name } ?: emptyList()
    }
}
