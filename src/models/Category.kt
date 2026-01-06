package models

enum class Category(val displayName: String) {
    GROCERIES("Groceries"),
    DAIRY("Dairy Products"),
    BAKERY("Bakery"),
    BEVERAGES("Beverages"),
    SNACKS("Snacks"),
    FROZEN("Frozen Food"),
    HOUSEHOLD("Household Items"),
    ELECTRONICS("Electronics"),
    OTHER("Other"); //semicolon is required as because member follow Expecting ';' after the last enum entry or '}' to close enum class body

    companion object {
        //get all category to display menu
        fun getAllCategories(): List<Category> = values().toList()
        fun fromString(value: String): Category? {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }

    }
}
