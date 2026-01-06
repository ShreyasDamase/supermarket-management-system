package models


data class Product(
    val id: String,
    val name: String,
    val category: Category,
    val price: Double,
    val quantityInStock: Int
) {

    fun isValid(): Boolean {
        return id.isNotBlank() && name.isNotBlank() && price > 0 && quantityInStock >= 0
    }

    //custom formating for csv
    fun toCsvLine(): String {
        return "$id,$name,${category.name},$price,$quantityInStock"
    }

    companion object {

        fun fromCsvLine(line: String): Product? {
            return try {
                val parts = line.split(",")
                if (parts.size != 5) return null // why 5 because we have 5 values means 5 col so if its not match data is corrupted
                Product(
                    id = parts[0],
                    name = parts[1],
                    category = Category.valueOf(parts[2]),
                    price = parts[3].toDouble(),
                    quantityInStock = parts[4].toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
