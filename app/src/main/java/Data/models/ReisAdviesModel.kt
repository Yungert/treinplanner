package Data.models

data class ReisAdviesModel(
    val scrollRequestBackwardContext: String,
    val scrollRequestForwardContext: String,
    val source: String,
    val trips: List<Trip>
)