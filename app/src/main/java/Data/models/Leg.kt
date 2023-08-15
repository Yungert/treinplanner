package Data.models

import com.yungert.treinplanner.presentation.ui.model.Message


data class Leg(
    val alternativeTransport: Boolean,
    val bicycleSpotCount: Int,
    val cancelled: Boolean,
    val changePossible: Boolean,
    val crowdForecast: String,
    val destination: DestinationXX,
    val direction: String,
    val idx: String,
    val journeyDetail: List<JourneyDetail>,
    val journeyDetailRef: String,
    val messages: List<Message>,
    val name: String,
    val nesProperties: NesProperties,
    val origin: OriginXX,
    val plannedDurationInMinutes: Int,
    val product: Product,
    val punctuality: Double,
    val reachable: Boolean,
    val shorterStock: Boolean,
    val stops: List<Stop>,
    val travelType: String,
    val transferMessages: List<TransferMessage>
)