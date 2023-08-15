package Data.models

data class FareOptions(
    val isEticketBuyable: Boolean,
    val isInternational: Boolean,
    val isInternationalBookable: Boolean,
    val isPossibleWithOvChipkaart: Boolean,
    val isTotalPriceUnknown: Boolean
)