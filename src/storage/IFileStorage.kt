package storage

interface IFileStorage {

    fun readLine(fileName: String): List<String>
    fun writeLine(fileName: String, line: List<String>)
}