package Data.models

data class Departure(
    val actualTime: String,
    val actualTrack: String,
    val cancelled: Boolean,
    val crowdForecast: String,
    val delayInSeconds: Int,
    val destination: DestinationXXX,
    val origin: OriginXXX,
    val plannedTime: String,
    val plannedTrack: String,
    val product: ProductX,
    val stockIdentifiers: List<String>
)