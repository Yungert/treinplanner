package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import Data.api.Resource
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.DrukteIndicator
import com.yungert.treinplanner.presentation.ui.model.TreinRitDetail
import com.yungert.treinplanner.presentation.ui.utils.CrowdForecast
import com.yungert.treinplanner.presentation.ui.utils.calculateDelay
import com.yungert.treinplanner.presentation.ui.utils.formatTime
import com.yungert.treinplanner.presentation.ui.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ViewStateRitDetail {
    object Loading : ViewStateRitDetail()
    data class Success(val details: List<TreinRitDetail>) : ViewStateRitDetail()
    data class Problem(val exception: ErrorState?) : ViewStateRitDetail()
}

class RitDetailViewModel : ViewModel() {
    private val _viewState = MutableStateFlow<ViewStateRitDetail>(ViewStateRitDetail.Loading)
    val stops = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)
    fun getReisadviezen(
        depatureUicCode: String,
        arrivalUicCode: String,
        reisId: String,
        dateTime: String,
        context: Context
    ) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateRitDetail.Problem(ErrorState.NO_CONNECTION)
            return
        }
        viewModelScope.launch {
            nsApiRepository.fetchRitById(
                depatureUicCode = depatureUicCode,
                arrivalUicCode = arrivalUicCode,
                dateTime = dateTime,
                reisId = reisId
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var treinStops = mutableListOf<TreinRitDetail>()
                        var stopOpRoute = false
                        result.data?.payload?.stops?.forEach { stop ->
                            if (stop.kind == "DEPARTURE") {
                                stopOpRoute = true
                            }
                            if (stop.status == "PASSING") {
                                return@forEach
                            }
                            if (!stopOpRoute) {
                                return@forEach
                            }
                            val departure = stop.departures.getOrNull(0)
                            val arrival = stop.arrivals.getOrNull(0)
                            var icon = Icons.Default.GroupOff
                            var color = Color.Gray
                            var aantal = 1

                            when (stop.departures.getOrNull(0)?.crowdForecast) {
                                CrowdForecast.rustig.value -> {
                                    icon = Icons.Default.Person
                                    color = Color.Green
                                }

                                CrowdForecast.gemiddeld.value -> {
                                    icon = Icons.Default.Person
                                    color = Color.Yellow
                                    aantal = 2
                                }

                                CrowdForecast.druk.value -> {
                                    icon = Icons.Default.Person
                                    color = Color.Red
                                    aantal = 3
                                }
                            }
                            val materieelNummer = mutableListOf<String>()
                            if (stop.actualStock != null || stop.plannedStock != null) {
                                var materieel =
                                    if (stop.actualStock != null) stop.actualStock else stop.plannedStock
                                materieel.trainParts.forEach { part ->
                                    materieelNummer.add(part.stockIdentifier)
                                }
                            }

                            treinStops.add(
                                TreinRitDetail(
                                    eindbestemmingTrein = stop.destination,
                                    ritNummer = result.data.payload.productNumbers.getOrNull(0) ?: "0",
                                    stationNaam = stop.stop.name,
                                    spoor = departure?.actualTrack ?: departure?.plannedTrack
                                    ?: arrival?.actualTrack ?: arrival?.plannedTrack,
                                    ingekort = stop.actualStock.hasSignificantChange ?: false,
                                    aantalZitplaatsen = stop.actualStock.numberOfSeats?.toString()
                                        ?: stop.plannedStock.numberOfSeats?.toString() ?: "",
                                    aantalTreinDelen = stop.actualStock.numberOfParts?.toString()
                                        ?: stop.plannedStock.numberOfParts?.toString() ?: "",
                                    actueleAankomstTijd = formatTime(arrival?.actualTime),
                                    geplandeAankomstTijd = formatTime(arrival?.plannedTime),
                                    aankomstVertraging = calculateDelay(
                                        arrival?.delayInSeconds?.toLong() ?: 0
                                    ),
                                    actueleVertrekTijd = formatTime(departure?.actualTime),
                                    geplandeVertrektTijd = formatTime(departure?.plannedTime),
                                    vertrekVertraging = calculateDelay(
                                        departure?.delayInSeconds?.toLong() ?: 0
                                    ),
                                    materieelType = stop.actualStock.trainType ?: "",
                                    drukte = DrukteIndicator(
                                        icon = icon,
                                        color = color,
                                        aantalIconen = aantal
                                    ),
                                    punctualiteit = arrival?.punctuality?.toString() ?: "0",
                                    materieelNummers = materieelNummer,

                                    )
                            )
                            if (stop.kind == "ARRIVAL") {
                                stopOpRoute = false
                            }
                        }
                        _viewState.value = ViewStateRitDetail.Success(treinStops)
                    }

                    is Resource.Loading -> {
                        _viewState.value = ViewStateRitDetail.Loading
                    }

                    is Resource.Error -> {
                        _viewState.value = ViewStateRitDetail.Problem(result.state)
                    }
                }

            }
        }
    }
}