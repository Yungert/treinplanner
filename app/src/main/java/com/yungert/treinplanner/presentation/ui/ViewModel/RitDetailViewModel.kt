package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import Data.api.Resource
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ViewStateRitDetail {
    object Loading : ViewStateRitDetail()
    data class Success(val details: List<TreinRitDetail>) : ViewStateRitDetail()
    data class Problem(val exception: ErrorState?) : ViewStateRitDetail()
}
class RitDetailViewModel() : ViewModel() {
    private val _viewState = MutableStateFlow<ViewStateRitDetail>(ViewStateRitDetail.Loading)
    val stops = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)
    fun getReisadviezen(depatureUicCode: String, arrivalUicCode: String, reisId: String, dateTime: String) {
        viewModelScope.launch {
            nsApiRepository.fetchRitById(depatureUicCode = depatureUicCode, arrivalUicCode = arrivalUicCode, dateTime = dateTime, reisId = reisId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var treinStops = mutableListOf<TreinRitDetail>()
                        var stopOpRoute = false
                        result.data?.payload?.stops?.forEach { stop ->
                            if(stop.kind == "DEPARTURE"){
                                stopOpRoute = true
                            }
                            if(!stopOpRoute){
                                return@forEach
                            }
                            val departure = stop.departures.getOrNull(0)
                            val arrival = stop.arrivals.getOrNull(0)
                            var icon = Icons.Default.GroupOff
                            var color = Color.Gray
                            var aantal = 1

                            if (stop.departures.getOrNull(0)?.crowdForecast == CrowdForecast.rustig.value) {
                                icon = Icons.Default.Person
                                color = Color.Green
                            } else if (stop.departures.getOrNull(0)?.crowdForecast == CrowdForecast.gemiddeld.value) {
                                icon = Icons.Default.Person
                                color = Color.Yellow
                                aantal = 2
                            } else if (stop.departures.getOrNull(0)?.crowdForecast == CrowdForecast.druk.value) {
                                icon = Icons.Default.Person
                                color = Color.Red
                                aantal = 3
                            }
                            val materieelNummer = mutableListOf<String>()
                            stop.actualStock.trainParts.forEach { part ->
                               materieelNummer.add(part.stockIdentifier)
                            }
                            treinStops.add(TreinRitDetail(
                                eindbestemmingTrein = stop.destination,
                                ritNummer = result.data.payload.productNumbers.getOrNull(0) ?: "0",
                                stationNaam = stop.stop.name,
                                spoor = departure?.actualTrack ?: departure?.plannedTrack ?: "",
                                ingekort = stop.actualStock.hasSignificantChange,
                                aantalZitplaatsen = stop.actualStock.numberOfSeats.toString(),
                                aantalTreinDelen = stop.actualStock.numberOfParts.toString(),
                                actueleAankomstTijd = formatTime(arrival?.actualTime),
                                geplandeAankomstTijd = formatTime(arrival?.plannedTime),
                                aankomstVertraging = calculateDelay(arrival?.delayInSeconds?.toLong() ?: 0),
                                actueleVertrekTijd = formatTime(departure?.actualTime),
                                geplandeVertrektTijd = formatTime(departure?.plannedTime),
                                vertrekVertraging = calculateDelay(departure?.delayInSeconds?.toLong() ?: 0),
                                materieelType = stop.actualStock.trainType,
                                drukte = DrukteIndicator(
                                    icon = icon,
                                    color = color,
                                    aantalIconen = aantal
                                ),
                                punctualiteit = arrival?.punctuality?.toString() ?: "0",
                                materieelNummers = materieelNummer,

                            ))
                            if(stop.kind == "ARRIVAL"){
                                stopOpRoute = false
                            }
                        }
                        _viewState.value = ViewStateRitDetail.Success(treinStops)
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