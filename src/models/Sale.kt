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

}