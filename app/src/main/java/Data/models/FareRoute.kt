package Data.models

data class FareRoute(
    val destination: DestinationX,
    val origin: OriginX,
    val routeId: String
)