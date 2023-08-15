package Data.models

data class Product(
    val categoryCode: String,
    val displayName: String,
    val longCategoryName: String,
    val number: String,
    val operatorAdministrativeCode: Int,
    val operatorCode: String,
    val operatorName: String,
    val shortCategoryName: String,
    val type: String
)