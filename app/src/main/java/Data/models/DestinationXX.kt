package Data.models

data class DestinationXX(
    val actualDateTime: String,
    val actualTimeZoneOffset: Int,
    val actualTrack: String,
    val checkinStatus: String,
    val countryCode: String,
    val exitSide: String,
    val lat: Double,
    val lng: Double,
    val name: String,
    val notes: List<Any>,
    val plannedDateTime: String,
    val plannedTimeZoneOffset: Int,
    val plannedTrack: String,
    val stationCode: String,
    val type: String,
    val uicCode: String
)