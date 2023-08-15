package Data.models

data class FareLeg(
    val destination: Destination,
    val fares: List<Fare>,
    val `operator`: String,
    val origin: Origin,
    val productTypes: List<String>
)