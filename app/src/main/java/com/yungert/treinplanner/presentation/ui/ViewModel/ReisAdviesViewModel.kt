package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import Data.api.Resource
import Data.models.PrimaryMessage
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.Adviezen
import com.yungert.treinplanner.presentation.ui.model.DrukteIndicator
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.utils.CrowdForecast
import com.yungert.treinplanner.presentation.ui.utils.DrukteIndicatorFormatter
import com.yungert.treinplanner.presentation.ui.utils.calculateTimeDiff
import com.yungert.treinplanner.presentation.ui.utils.formatTime
import com.yungert.treinplanner.presentation.ui.utils.formatTravelTime
import com.yungert.treinplanner.presentation.ui.utils.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class ViewStateReisAdvies {
    object Loading : ViewStateReisAdvies()
    data class Success(val details: ReisAdvies) : ViewStateReisAdvies()
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
                        var adviezen = mutableListOf<Adviezen>()
                        var primaryMessage : PrimaryMessage? = null
                        result.data?.trips?.forEachIndexed { index, advies ->
                            var treinSoort = ""
                            primaryMessage = advies.primaryMessage
                            advies.legs.forEachIndexed { index, rit ->
                                treinSoort = if (index == 0) {
                                    treinSoort + rit.product.shortCategoryName.lowercase()
                                } else {
                                    treinSoort + " + " + rit.product.shortCategoryName.lowercase()
                                }
                            }
                            adviezen.add(
                                Adviezen(
                                    verstrekStation = startStation,
                                    aankomstStation = eindStation,
                                    geplandeVertrekTijd = formatTime(advies.legs.getOrNull(0)?.origin?.plannedDateTime),
                                    geplandeAankomstTijd = formatTime(advies.legs.getOrNull(advies.legs.size - 1)?.destination?.plannedDateTime),
                                    actueleReistijd = formatTravelTime(advies.actualDurationInMinutes ?: 0),
                                    geplandeReistijd = formatTravelTime(advies.plannedDurationInMinutes),
                                    aantalTransfers = advies.transfers,
                                    reinadviesId = advies.ctxRecon,
                                    aankomstVertraging = calculateTimeDiff(advies.legs.getOrNull(advies.legs.size - 1)?.destination?.plannedDateTime, advies.legs.getOrNull(advies.legs.size - 1)?.destination?.actualDateTime),
                                    vertrekVertraging = calculateTimeDiff(advies.legs.getOrNull(0)?.origin?.plannedDateTime, advies.legs.getOrNull(0)?.origin?.actualDateTime),
                                    bericht = advies.messages,
                                    drukte = DrukteIndicatorFormatter(advies.crowdForecast),
                                    cancelled = advies.status == "CANCELED",
                                    treinSoortenOpRit = treinSoort,
                                    alternatiefVervoer = advies.status == "ALTERNATIVE_TRANSPORT"
                                )
                            )
                        }

                        _viewState.value = ViewStateReisAdvies.Success(ReisAdvies(
                            primaryMessage = primaryMessage,
                            advies = adviezen,
                            verstrekStation = startStation,
                            aankomstStation = eindStation
                        ))
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

