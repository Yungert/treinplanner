package Data.models

data class RegisterJourney(
    val bicycleReservationRequired: Boolean,
    val searchUrl: String,
    val status: String,
    val url: String
)