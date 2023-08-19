package Data.models

data class Stock(
    val hasSignificantChange: Boolean,
    val numberOfParts: Int,
    val numberOfSeats: Int,
    val trainParts: List<TrainPart>,
    val trainType: String
)