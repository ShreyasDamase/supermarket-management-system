package models

data class Sale(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val pricePerUnit: Double,
    val timestamp: Long
) {

    //total amount for sale
    fun getTotalAmount(): Double {
        return quantity * pricePerUnit
    }

    //formated date string
    fun getFormattedDate(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(timestamp))
    }


    fun toCsvLine(): String {
        return "$id,$productId,$productName,$quantity,$pricePerUnit,$timestamp"
    }

    companion object {
        fun fromCsvLine(line: String): Sale? {
            return try {
                val parts = line.split(",")
                if (parts.size != 6) return null
                Sale(
                    id = parts[0],
                    productId = parts[1],
                    productName = parts[2],
                    quantity = parts[3].toInt(),
                    pricePerUnit = parts[4].toDouble(),
                    timestamp = parts[5].toLong()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}