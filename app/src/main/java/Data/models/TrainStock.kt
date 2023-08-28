package Data.models


data class TrainStock(
    val trainType: String,
    val numberOfParts: String,
    val numberOfSeats: String,
    val trainParts: List<TrainParts>,
    val hasSignificantChange : Boolean
)