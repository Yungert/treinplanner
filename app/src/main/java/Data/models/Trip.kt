package Data.models

import com.yungert.treinplanner.presentation.ui.model.Message

data class Trip(
    val actualDurationInMinutes: Int,
    val checksum: String,
    val crowdForecast: String,
    val ctxRecon: String,
    val fareLegs: List<FareLeg>,
    val fareOptions: FareOptions,
    val fareRoute: FareRoute,
    val fares: List<FareX>,
    val idx: Int,
    val legs: List<Leg>,
    val messages: List<Any>,
    val modalityListItems: List<ModalityItems>,
    val nsiLink: NsiLink,
    val optimal: Boolean,
    val plannedDurationInMinutes: Int,
    val productFare: ProductFare,
    val punctuality: Double,
    val realtime: Boolean,
    val registerJourney: RegisterJourney,
    val routeId: String,
    val shareUrl: ShareUrl,
    val status: String,
    val transfers: Int,
    val type: String,
    val uid: String,
    val primaryMessage: Message,
    val alternativeTransport : Boolean,
)