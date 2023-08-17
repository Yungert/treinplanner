package Data.models

data class PayloadX(
    val allowCrowdReporting: Boolean,
    val notes: List<Any>,
    val productNumbers: List<String>,
    val source: String,
    val stops: List<StopX>
)