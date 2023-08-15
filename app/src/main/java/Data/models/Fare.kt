package Data.models

data class Fare(
    val buyableTicketSupplementPriceInCents: Int,
    val discountType: String,
    val priceInCents: Int,
    val priceInCentsExcludingSupplement: Int,
    val product: String,
    val supplementInCents: Int,
    val travelClass: String
)