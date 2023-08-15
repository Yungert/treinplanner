package Data.models

data class Stop(
    val actualArrivalDateTime: String,
    val actualArrivalTimeZoneOffset: Int,
    val actualArrivalTrack: String,
    val actualDepartureDateTime: String,
    val actualDepartureTimeZoneOffset: Int,
    val actualDepartureTrack: String,
    val arrivalDelayInSeconds: Int,
    val borderStop: Boolean,
    val cancelled: Boolean,
    val countryCode: String,
    val departureDelayInSeconds: Int,
    val lat: Double,
    val lng: Double,
    val name: String,
    val notes: List<Any>,
    val passing: Boolean,
    val plannedArrivalDateTime: String,
    val plannedArrivalTimeZoneOffset: Int,
    val plannedArrivalTrack: String,
    val plannedDepartureDateTime: String,
    val plannedDepartureTimeZoneOffset: Int,
    val plannedDepartureTrack: String,
    val routeIdx: Int,
    val uicCode: String
)