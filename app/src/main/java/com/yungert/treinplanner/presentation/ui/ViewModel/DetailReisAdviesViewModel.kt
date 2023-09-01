package com.yungert.treinplanner.presentation.ui.ViewModel

import com.yungert.treinplanner.presentation.Data.Repository.NsApiRepository
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.utils.calculateTimeDiff
import com.yungert.treinplanner.presentation.utils.formatTime
import com.yungert.treinplanner.presentation.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.yungert.treinplanner.presentation.ui.model.RitDetail as RitDetail1

sealed class ViewStateDetailReisAdvies {
    object Loading : ViewStateDetailReisAdvies()
    data class Success(val details: List<RitDetail1>) : ViewStateDetailReisAdvies()
    data class Problem(val exception: ErrorState?) : ViewStateDetailReisAdvies()
}

class DetailReisAdviesViewModel : ViewModel() {
    private val _viewState =
        MutableStateFlow<ViewStateDetailReisAdvies>(ViewStateDetailReisAdvies.Loading)
    val reisavies = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)

    fun getReisadviesDetail(reisAdviesId: String, context: Context) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateDetailReisAdvies.Problem(ErrorState.NO_CONNECTION)
            return
        }
        viewModelScope.launch {
            nsApiRepository.fetchSingleTripById(reisadviesId = reisAdviesId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var ritten = mutableListOf<RitDetail>()
                        result.data?.legs?.forEachIndexed { index, advies ->
                            var ritDetail: RitDetail? = null
                            var overstap = ""
                            val alternatievVervoerInzet = advies.alternativeTransport
                            if (index > 0) {
                                var lastStop = result.data.legs[index - 1].stops.getOrNull(
                                    result.data.legs[index - 1].stops.size.minus(1)
                                )
                                var aankomstVorigeTrein = lastStop?.actualArrivalDateTime
                                    ?: lastStop?.plannedArrivalDateTime
                                overstap =
                                    if (result.data.legs.getOrNull(0)?.stops?.getOrNull(0)?.actualDepartureDateTime != null) {
                                        calculateTimeDiff(
                                            aankomstVorigeTrein,
                                            advies.stops.getOrNull(0)?.actualDepartureDateTime
                                        )
                                    } else {
                                        calculateTimeDiff(
                                            aankomstVorigeTrein,
                                            advies.stops.getOrNull(0)?.plannedDepartureDateTime
                                        )
                                    }
                            }
                            ritDetail = RitDetail(
                                treinOperator = advies.product.operatorName,
                                treinOperatorType = if (!alternatievVervoerInzet) advies.product.categoryCode else advies.product.longCategoryName,
                                ritNummer = if (!alternatievVervoerInzet) advies.product.number else "",
                                eindbestemmingTrein = advies.direction,
                                naamVertrekStation = advies.origin.name,
                                geplandeVertrektijd = formatTime(advies.origin.plannedDateTime),
                                vertrekSpoor = if (alternatievVervoerInzet) "" else advies.origin.actualTrack
                                    ?: advies.origin.plannedTrack,
                                naamAankomstStation = advies.stops.getOrNull(
                                    advies.stops.size.minus(
                                        1
                                    )
                                )?.name ?: "",
                                geplandeAankomsttijd = formatTime(
                                    advies.stops.getOrNull(
                                        advies.stops.size.minus(
                                            1
                                        )
                                    )?.plannedArrivalDateTime
                                ),
                                aankomstSpoor = if (alternatievVervoerInzet) "" else advies.destination.actualTrack
                                    ?: advies.destination.plannedTrack,
                                vertrekVertraging = calculateTimeDiff(
                                    advies.origin.plannedDateTime,
                                    advies.origin.actualDateTime
                                ),
                                aankomstVertraging = calculateTimeDiff(
                                    advies.destination.plannedDateTime,
                                    advies.destination.actualDateTime
                                ),
                                berichten = advies.messages,
                                hoofdBericht = result.data.primaryMessage?.message?.text,
                                transferBericht = advies.transferMessages,
                                alternatiefVervoer = alternatievVervoerInzet,
                                ritId = advies.journeyDetailRef,
                                vertrekStationUicCode = advies.origin.uicCode,
                                aankomstStationUicCode = advies.destination.uicCode,
                                datum = advies.origin.plannedDateTime,
                                overstapTijd = overstap,
                                kortereTreinDanGepland = advies.shorterStock
                            )

                            ritDetail.let { ritten.add(it) }
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