package Data.models

data class FareX(
    val discountType: String,
    val priceInCents: Int,
    val product: String,
    val travelClass: String
)