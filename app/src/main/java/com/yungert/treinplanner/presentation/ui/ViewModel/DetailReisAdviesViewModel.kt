package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import Data.api.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.Message
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.model.RitDetail

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
                        result.data?.legs?.forEach { rit ->
                            ritten.add(
                                RitDetail(
                                    treinOperator = rit?.product?.operatorName ?: "",
                                    treinOperatorType = rit?.product?.categoryCode ?: "",
                                    ritNummer = rit?.product?.number ?: "",
                                    eindbestemmingTrein = rit?.direction ?: "",
                                    naamVertrekStation = rit?.origin?.name ?: "",
                                    geplandeVertrektijd = rit?.stops?.getOrNull(0)?.plannedDepartureDateTime ?: "",
                                    vertrekSpoor = rit?.stops?.getOrNull(0)?.actualDepartureTrack ?: rit?.stops?.getOrNull(0)?.plannedDepartureTrack ?: "",
                                    vertragingInSecondeVertrekStation = rit?.stops?.getOrNull(0)?.departureDelayInSeconds ?: 0,
                                    naamAankomstStation = rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.name ?: "",
                                    geplandeAankomsttijd = rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.plannedArrivalDateTime ?: "",
                                    aankomstSpoor = rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.actualArrivalTrack ?: rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.plannedArrivalTrack ?: "",
                                    vertragingInSecondeAankomstStation = rit.stops.getOrNull(rit?.stops?.size?.minus(1) ?: 0)?.arrivalDelayInSeconds ?: 0,
                                    berichten = rit?.messages,
                                    transferBericht = rit.transferMessages
                                )
                            )
                        }
                        _viewState.value = ViewStateDetailReisAdvies.Success(ritten)
                    }

                    is Resource.Loading -> {

                    }

                    is Resource.Error -> {

                    }
                }

            }
        }
    }
}