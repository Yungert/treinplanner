package com.yungert.treinplanner.presentation.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.Data.Repository.NsApiRepository
import com.yungert.treinplanner.presentation.Data.Repository.SharedPreferencesRepository
import com.yungert.treinplanner.presentation.Data.api.NSApiClient
import com.yungert.treinplanner.presentation.Data.api.Resource
import com.yungert.treinplanner.presentation.Data.models.PrimaryMessage
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.Adviezen
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.utils.DrukteIndicatorFormatter
import com.yungert.treinplanner.presentation.utils.calculateTimeDiff
import com.yungert.treinplanner.presentation.utils.formatTime
import com.yungert.treinplanner.presentation.utils.formatTravelTime
import com.yungert.treinplanner.presentation.utils.hasInternetConnection
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
    private val sharedPreferencesRepository: SharedPreferencesRepository =
        SharedPreferencesRepository()

    fun getReisadviezen(startStation: String, eindStation: String, context: Context) {
        if (!hasInternetConnection(context)) {
            _viewState.value = ViewStateReisAdvies.Problem(ErrorState.NO_CONNECTION)
            return
        }

        viewModelScope.launch {
            setLaatstGeplandeReis(context = context, key = "vertrekStation", value = startStation)
            setLaatstGeplandeReis(context = context, key = "aankomstStation", value = eindStation)
            nsApiRepository.fetchReisAdviezen(
                vetrekStation = startStation,
                aankomstStation = eindStation
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        var adviezen = mutableListOf<Adviezen>()
                        var primaryMessage = mutableListOf<PrimaryMessage>()
                        result.data?.trips?.forEachIndexed { index, advies ->
                            var treinSoort = ""
                            val uniek = primaryMessage.any { message ->
                                message.message?.id == advies.primaryMessage?.message?.id
                            }
                            if (!uniek && advies.primaryMessage?.type != "LEG_TRANSFER_IMPOSSIBLE" && advies.primaryMessage?.type != "LEG_CANCELLED" && advies.primaryMessage?.type != "TRIP_CANCELLED" && advies.primaryMessage?.type != "ADDITIONAL") {
                                advies.primaryMessage?.let { primaryMessage.add(it) }
                            }
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
                                    actueleReistijd = formatTravelTime(
                                        advies.actualDurationInMinutes
                                    ),
                                    geplandeReistijd = formatTravelTime(advies.plannedDurationInMinutes),
                                    aantalTransfers = advies.transfers,
                                    reinadviesId = advies.ctxRecon,
                                    aankomstVertraging = calculateTimeDiff(
                                        advies.legs.getOrNull(
                                            advies.legs.size - 1
                                        )?.destination?.plannedDateTime,
                                        advies.legs.getOrNull(advies.legs.size - 1)?.destination?.actualDateTime
                                    ),
                                    vertrekVertraging = calculateTimeDiff(
                                        advies.legs.getOrNull(0)?.origin?.plannedDateTime,
                                        advies.legs.getOrNull(0)?.origin?.actualDateTime
                                    ),
                                    bericht = advies.messages,
                                    drukte = DrukteIndicatorFormatter(advies.crowdForecast),
                                    cancelled = advies.status == "CANCELLED",
                                    aandachtsPunten = if(advies.status == "CANCELLED") advies.primaryMessage?.message?.text ?: advies.primaryMessage?.title else null,
                                    treinSoortenOpRit = treinSoort,
                                    alternatiefVervoer = advies.status == "ALTERNATIVE_TRANSPORT",
                                )
                            )
                        }

                        _viewState.value = ViewStateReisAdvies.Success(
                            ReisAdvies(
                                primaryMessage = primaryMessage,
                                advies = adviezen,
                                verstrekStation = startStation,
                                aankomstStation = eindStation
                            )
                        )
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

    suspend fun setLaatstGeplandeReis(context: Context, key: String, value: String) {
        sharedPreferencesRepository.editLastRoute(context = context, key = key, value = value)
    }
}

