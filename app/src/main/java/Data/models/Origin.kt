package Data.models

data class Origin(
    val countryCode: String,
    val lat: Double,
    val lng: Double,
    val name: String,
    val stationCode: String,
    val type: String,
    val uicCode: String
)