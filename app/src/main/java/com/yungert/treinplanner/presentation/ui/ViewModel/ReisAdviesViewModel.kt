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
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.utils.CrowdForecast
import com.yungert.treinplanner.presentation.ui.utils.calculateDelay
import com.yungert.treinplanner.presentation.ui.utils.formatTime
import com.yungert.treinplanner.presentation.ui.utils.formatTravelTime
import com.yungert.treinplanner.presentation.ui.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class ViewStateReisAdvies {
    object Loading : ViewStateReisAdvies()
    data class Success(val details: List<ReisAdvies>) : ViewStateReisAdvies()
    data class Problem(val exception: ErrorState?) : ViewStateReisAdvies()
}

class ReisAdviesViewModel : ViewModel() {
    private val _viewState = MutableStateFlow<ViewStateReisAdvies>(ViewStateReisAdvies.Loading)
    val reisavies = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)

    fun getReisadviezen(startStation: String, eindStation: String, context: Context) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateReisAdvies.Problem(ErrorState.NO_CONNECTION)
            return
        }
        viewModelScope.launch {
            nsApiRepository.fetchReisAdviezen(
                vetrekStation = startStation,
                aankomstStation = eindStation
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var reisAdviezen = mutableListOf<ReisAdvies>()

                        result.data?.trips?.forEachIndexed { index, advies ->
                            var icon = Icons.Default.GroupOff
                            var color = Color.Gray
                            var aantal = 1
                            when (advies.crowdForecast) {
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
                            advies.legs.forEachIndexed { index, rit ->
                                treinSoort = if (index == 0) {
                                    treinSoort + rit.product.shortCategoryName.lowercase()
                                } else {
                                    treinSoort + " + " + rit.product.shortCategoryName.lowercase()
                                }
                            }
                            reisAdviezen.add(
                                ReisAdvies(
                                    verstrekStation = advies.legs.getOrNull(0)?.origin?.name
                                        ?: "",
                                    aankomstStation = advies.legs.getOrNull(
                                        advies.legs.size?.minus(
                                            1
                                        ) ?: 0
                                    )?.destination?.name ?: "",
                                    geplandeVertrekTijd = formatTime(advies.legs.getOrNull(0)?.origin?.plannedDateTime),
                                    geplandeAankomstTijd = formatTime(
                                        advies.legs.getOrNull(
                                            advies.legs.size?.minus(
                                                1
                                            ) ?: 0
                                        )?.destination?.plannedDateTime
                                    ),
                                    actueleReistijd = formatTravelTime(
                                        advies.actualDurationInMinutes ?: 0
                                    ),
                                    geplandeReistijd = formatTravelTime(
                                        advies.plannedDurationInMinutes ?: 0
                                    ),
                                    aantalTransfers = advies.transfers,
                                    reinadviesId = advies.ctxRecon,
                                    aankomstVertraging = calculateDelay(
                                        advies.legs.getOrNull(
                                            advies.legs.size?.minus(1) ?: 0
                                        )?.stops?.getOrNull(
                                            advies.legs.getOrNull(
                                                advies.legs.size?.minus(
                                                    1
                                                ) ?: 0
                                            )?.stops?.size?.minus(1) ?: 0
                                        )?.arrivalDelayInSeconds?.toLong() ?: 0
                                    ),
                                    vertrekVertraging = calculateDelay(
                                        advies.legs.getOrNull(
                                            advies.legs.size?.minus(
                                                1
                                            ) ?: 0
                                        )?.stops?.getOrNull(0)?.departureDelayInSeconds?.toLong()
                                            ?: 0
                                    ),
                                    bericht = advies.primaryMessage?.message,
                                    drukte = DrukteIndicator(
                                        icon = icon,
                                        aantalIconen = aantal,
                                        color = color
                                    ),
                                    cancelled = advies.status == "CANCELLED",
                                    treinSoortenOpRit = treinSoort,
                                    alternatiefVervoer = advies.status == "ALTERNATIVE_TRANSPORT"
                                )
                            )
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

