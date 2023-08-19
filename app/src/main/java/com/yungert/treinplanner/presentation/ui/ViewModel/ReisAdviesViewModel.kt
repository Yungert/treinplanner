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
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.utils.CrowdForecast
import com.yungert.treinplanner.presentation.ui.utils.calculateDelay
import com.yungert.treinplanner.presentation.ui.utils.formatTime
import com.yungert.treinplanner.presentation.ui.utils.formatTravelTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class ViewStateReisAdvies {
    object Loading : ViewStateReisAdvies()
    data class Success(val details: List<ReisAdvies>) : ViewStateReisAdvies()
    data class Problem(val exception: ErrorState?) : ViewStateReisAdvies()
}

class ReisAdviesViewModel () : ViewModel() {
    private val _viewState = MutableStateFlow<ViewStateReisAdvies>(ViewStateReisAdvies.Loading)
    val reisavies = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)

    fun getReisadviezen(startStation: String, eindStation: String) {
        viewModelScope.launch {
            nsApiRepository.fetchReisAdviezen(
                vetrekStation = startStation,
                aankomstStation = eindStation
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var reisAdviezen = mutableListOf<ReisAdvies>()

                        result.data?.trips?.forEachIndexed { index, trip ->
                            var icon = Icons.Default.GroupOff
                            var color = Color.Gray
                            var aantal = 1

                            when (trip.crowdForecast) {
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
                            var treinSoort = ""
                            trip.legs.forEachIndexed { index, rit ->
                                treinSoort = if(index == 0){
                                    treinSoort + rit.product.shortCategoryName.lowercase()
                                } else {
                                    treinSoort + " + " + rit.product.shortCategoryName.lowercase()
                                }
                            }
                            reisAdviezen.add(ReisAdvies(
                                verstrekStation = trip.legs?.getOrNull(0)?.origin?.name ?: "",
                                aankomstStation = trip.legs?.getOrNull(trip.legs?.size?.minus(1) ?: 0)?.destination?.name ?: "",
                                geplandeVertrekTijd = formatTime(trip.legs?.getOrNull(0)?.origin?.plannedDateTime),
                                geplandeAankomstTijd = formatTime(trip.legs?.getOrNull(trip.legs?.size?.minus(1) ?: 0)?.destination?.plannedDateTime),
                                actueleReistijd = formatTravelTime(trip.actualDurationInMinutes ?: trip.plannedDurationInMinutes ?: 0),
                                geplandeReistijd = formatTravelTime(trip.plannedDurationInMinutes ?: trip.plannedDurationInMinutes ?: 0),
                                aantalTransfers = trip.transfers ?: 0,
                                reinadviesId = trip.ctxRecon ?: "",
                                aankomstVertraging = calculateDelay(trip.legs?.getOrNull(trip.legs?.size?.minus(1) ?: 0)?.stops?.getOrNull(trip.legs?.getOrNull(trip.legs?.size?.minus(1) ?: 0)?.stops?.size?.minus(1) ?: 0)?.arrivalDelayInSeconds?.toLong() ?: 0),
                                vertrekVertraging = calculateDelay(trip.legs?.getOrNull(trip.legs?.size?.minus(1) ?: 0)?.stops?.getOrNull(0)?.departureDelayInSeconds?.toLong() ?: 0),
                                bericht = trip.primaryMessage,
                                drukte = DrukteIndicator(
                                    icon = icon,
                                    aantalIconen = aantal,
                                    color = color
                                ),
                                cancelled = trip.legs?.getOrNull(0)?.cancelled ?: false,
                                treinSoortenOpRit = treinSoort
                            ))
                        }
                        _viewState.value = ViewStateReisAdvies.Success(reisAdviezen)
                    }

                    is Resource.Loading -> {
                        _viewState.value = ViewStateReisAdvies.Loading
                    }

                    is Resource.Error -> {
                        _viewState.value = ViewStateReisAdvies.Problem(result.state)
                    }
                }

            }
        }
    }
}

