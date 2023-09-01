package com.yungert.treinplanner.presentation.Data.models

data class RitDetailApiResponse(
    val payload: Payload
)

data class Payload(
    val notes: List<JourneyNote>,
    val productNumbers: List<String>,
    val stops: List<JourneyStop>,
    val allowCrowdReporting: Boolean,
    val source: String,
)

data class JourneyNote(
    val value: String?,
    val accessibilityValue: String?,
    val key: String?,
    val noteType: String?,
    val priority: Int?,
    val routeIdxFrom: Int?,
    val routeIdxTo: Int?,
    val link: Link?,
    val isPresentationRequired: Boolean?,
    val category: String?
)

data class JourneyStop(
    val id: String,
    val stop: StopDetails,
    val previousStopId: List<String>,
    val nextStopId: List<String>,
    val destination: String,
    val status: String,
    val kind: String,
    val arrivals: List<Arrival>,
    val departures: List<Departure>,
    val actualStock: TrainStock?,
    val plannedStock: TrainStock?,
    val platformFeatures: List<PlatformFeature>,
    val coachCrowdForecast: List<CoachCrowdForecast>
)

data class StopDetails(
    val name: String,
    val lng: Double,
    val lat: Double,
    val countryCode: String,
    val uicCode: String
)

data class Departure(
    val product: JourneyProduct,
    val origin: StopDetails,
    val destination: StopDetails,
    val plannedTime: String,
    val actualTime: String,
    val delayInSeconds: Int,
    val plannedTrack: String,
    val actualTrack: String,
    val cancelled: Boolean,
    val crowdForecast: String,
    val stockIdentifiers: List<String>
)

data class JourneyProduct(
    val number: String,
    val categoryCode: String,
    val shortCategoryName: String,
    val longCategoryName: String,
    val operatorCode: String,
    val operatorName: String,
    val type: String
)

data class TrainStock(
    val trainType: String,
    val numberOfSeats: Int,
    val numberOfParts: Int,
    val trainParts: List<TrainPart>,
    val hasSignificantChange: Boolean?
)

data class TrainPart(
    val stockIdentifier: String,
    val facilities: List<String>,
    val image: Image,
    val destination: Station,
)

data class Arrival(
    val product: JourneyProduct,
    val origin: StopDetails,
    val destination: StopDetails,
    val plannedTime: String,
    val actualTime: String,
    val delayInSeconds: Int,
    val punctuality: Double,
    val plannedTrack: String,
    val actualTrack: String,
    val cancelled: Boolean,
    val crowdForecast: String,
    val stockIdentifiers: List<String>
)

data class Image(
    val uri: String
)

data class Station(
    val UICCode: String?,
    val stationType: String?,
    val EVACode: String?,
    val code: String?,
    val sporen: List<Track>?,
    val synoniemen: List<String>?,
    val heeftFaciliteiten: Boolean?,
    val heeftVertrektijden: Boolean?,
    val heeftReisassistentie: Boolean?,
    val namen: StationsNamen?,
    val land: String?,
    val lat: Double?,
    val lng: Double?,
    val radius: Int?,
    val naderenRadius: Int?,
    val distance: Double?,
    val ingangsDatum: String?,
    val eindDatum: String?,
    val nearbyMeLocationId: NearbyMeLocationId?
)

data class Track(
    val spoorNummer: String?
)

data class StationsNamen(
    val lang: String?,
    val middel: String?,
    val kort: String?,
    val festive: String?
)

data class NearbyMeLocationId(
    val type: String?,
    val value: String?
)

data class PlatformFeature(
    val paddingLeft: Int?,
    val width: Int?,
    val type: String?,
    val description: String?
)

data class CoachCrowdForecast(
    val paddingLeft: Int?,
    val width: Int?,
    val classification: String?
)
