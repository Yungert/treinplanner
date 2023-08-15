package Data.models

data class Destination(
    val countryCode: String,
    val lat: Double,
    val lng: Double,
    val name: String,
    val stationCode: String,
    val type: String,
    val uicCode: String
)