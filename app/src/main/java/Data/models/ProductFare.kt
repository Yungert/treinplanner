package Data.models

data class ProductFare(
    val buyableTicketPriceInCents: Int,
    val buyableTicketPriceInCentsExcludingSupplement: Int,
    val discountType: String,
    val priceInCents: Int,
    val priceInCentsExcludingSupplement: Int,
    val product: String,
    val travelClass: String
)