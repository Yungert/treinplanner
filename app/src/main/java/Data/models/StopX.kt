package Data.models

data class StopX(
    val actualStock: Stock,
    val arrivals: List<Arrival>,
    val coachCrowdForecast: List<Any>,
    val departures: List<Departure>,
    val destination: String,
    val id: String,
    val kind: String,
    val nextStopId: List<String>,
    val plannedStock: Stock,
    val platformFeatures: List<Any>,
    val previousStopId: List<String>,
    val status: String,
    val stop: StopXX
)