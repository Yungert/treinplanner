package Data.models

data class ActualStock(
    val hasSignificantChange: Boolean,
    val numberOfParts: Int,
    val numberOfSeats: Int,
    val trainParts: List<TrainPart>,
    val trainType: String
)