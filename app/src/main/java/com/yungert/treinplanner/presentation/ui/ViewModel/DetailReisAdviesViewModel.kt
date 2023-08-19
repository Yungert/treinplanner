package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import Data.api.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.ui.utils.calculateDelay
import com.yungert.treinplanner.presentation.ui.utils.calculateTimeDiff
import com.yungert.treinplanner.presentation.ui.utils.formatTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.yungert.treinplanner.presentation.ui.model.RitDetail as RitDetail1

sealed class ViewStateDetailReisAdvies {
    object Loading : ViewStateDetailReisAdvies()
    data class Success(val details: List<RitDetail1>) : ViewStateDetailReisAdvies()
    data class Problem(val exception: ErrorState?) : ViewStateDetailReisAdvies()
}

class DetailReisAdviesViewModel() : ViewModel() {
    private val _viewState =
        MutableStateFlow<ViewStateDetailReisAdvies>(ViewStateDetailReisAdvies.Loading)
    val reisavies = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)

    fun getReisadviesDetail(reisAdviesId: String) {
        viewModelScope.launch {
            nsApiRepository.fetchSingleTripById(reisadviesId = reisAdviesId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var ritten = mutableListOf<RitDetail>()
                        result.data?.legs?.forEachIndexed { index, rit ->
                            var ritDetail: RitDetail?

                            var overstap = ""
                            if(index > 0){
                                var lastStop = result.data?.legs[index - 1].stops.getOrNull(result.data?.legs[index - 1].stops.size?.minus(1) ?: 0)
                                var aankomstVorigeTrein = lastStop?.actualArrivalDateTime ?: lastStop?.plannedArrivalDateTime
                                overstap = if(rit?.stops?.getOrNull(0)?.actualDepartureDateTime != null){
                                    calculateTimeDiff(aankomstVorigeTrein, rit?.stops?.getOrNull(0)?.actualDepartureDateTime)
                                } else {
                                    calculateTimeDiff(aankomstVorigeTrein, rit?.stops?.getOrNull(0)?.plannedDepartureDateTime)
                                }
                            }
                            if (!rit.alternativeTransport) {
                                ritDetail = RitDetail(
                                    treinOperator = rit?.product?.operatorName ?: "",
                                    treinOperatorType = rit?.product?.categoryCode ?: "",
                                    ritNummer = rit?.product?.number ?: "",
                                    eindbestemmingTrein = rit?.direction ?: "",
                                    naamVertrekStation = rit?.origin?.name ?: "",
                                    geplandeVertrektijd = formatTime(rit?.stops?.getOrNull(0)?.plannedDepartureDateTime),
                                    vertrekSpoor = rit?.stops?.getOrNull(0)?.actualDepartureTrack ?: rit?.stops?.getOrNull(0)?.plannedDepartureTrack,
                                    vertrekVertraging = calculateDelay(rit?.stops?.getOrNull(0)?.departureDelayInSeconds?.toLong() ?: 0),
                                    naamAankomstStation = rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.name ?: "",
                                    geplandeAankomsttijd = formatTime(rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.plannedArrivalDateTime),
                                    aankomstSpoor = rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.actualArrivalTrack ?: rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.plannedArrivalTrack,
                                    aankomstVertraging = calculateDelay(rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.arrivalDelayInSeconds?.toLong() ?: 0),
                                    berichten = rit?.messages,
                                    transferBericht = rit.transferMessages,
                                    alternatiefVervoer = rit.alternativeTransport,
                                    actueleAankomstTijd = formatTime(rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.actualArrivalDateTime),
                                    actueleVertrektijd = formatTime(rit?.stops?.getOrNull(0)?.actualDepartureDateTime),
                                    ritId = rit.journeyDetailRef,
                                    vertrekStationUicCode = rit.origin.uicCode,
                                    aankomstStationUicCode = rit.destination.uicCode,
                                    datum = rit.origin.plannedDateTime,
                                    overstapTijd = overstap,
                                )
                            } else {
                                ritDetail = RitDetail(
                                    treinOperator = rit?.product?.operatorName ?: "",
                                    treinOperatorType = rit?.product?.longCategoryName ?: "",
                                    ritNummer = "",
                                    eindbestemmingTrein = rit?.direction ?: "",
                                    naamVertrekStation = rit?.origin?.name ?: "",
                                    geplandeVertrektijd = formatTime(rit?.stops?.getOrNull(0)?.plannedDepartureDateTime ?: ""),
                                    vertrekSpoor = null,
                                    vertrekVertraging = "",
                                    naamAankomstStation = rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.name ?: "",
                                    geplandeAankomsttijd = formatTime(rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.plannedArrivalDateTime ?: ""),
                                    aankomstSpoor = null,
                                    aankomstVertraging = "",
                                    berichten = rit?.messages,
                                    transferBericht = rit.transferMessages,
                                    alternatiefVervoer = rit.alternativeTransport,
                                    actueleAankomstTijd = formatTime(rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.actualArrivalDateTime ?: ""),
                                    actueleVertrektijd = formatTime(rit?.stops?.getOrNull(0)?.actualDepartureDateTime ?: ""),
                                    ritId = rit.journeyDetailRef,
                                    vertrekStationUicCode = rit.origin.uicCode,
                                    aankomstStationUicCode = rit.destination.uicCode,
                                    datum = rit.origin.plannedDateTime,
                                    overstapTijd = overstap,
                                )
                            }
                            ritten.add(ritDetail)
                        }
                        _viewState.value = ViewStateDetailReisAdvies.Success(ritten)
                    }

                    is Resource.Loading -> {
                        _viewState.value = ViewStateDetailReisAdvies.Loading
                    }

                    is Resource.Error -> {
                        _viewState.value = ViewStateDetailReisAdvies.Problem(result.state)
                    }
                }

            }
        }
    }
}