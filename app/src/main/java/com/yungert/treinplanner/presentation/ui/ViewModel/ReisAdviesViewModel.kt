package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import Data.api.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
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

                        result.data?.trips?.forEach {trip ->
                            reisAdviezen.add(ReisAdvies(
                                verstrekStation = trip?.legs?.get(0)?.origin?.name ?: "",
                                aankomstStation = trip?.legs?.get(0)?.destination?.name ?: "",
                                vertrekTijd = trip?.legs?.getOrNull(0)?.origin?.actualDateTime ?: trip?.legs?.getOrNull(0)?.origin?.plannedDateTime ?: "",
                                aankomstTijd = trip?.legs?.getOrNull(trip?.legs?.size?.minus(1) ?: 0)?.destination?.actualDateTime ?: trip?.legs?.getOrNull(trip?.legs?.size?.minus(1) ?: 0)?.destination?.plannedDateTime ?: "",
                                reisTijdInMinuten = trip?.legs?.get(0)?.plannedDurationInMinutes ?: 0,
                                aantalTransfers = trip?.transfers ?: 0,
                                reinadviesId = trip?.ctxRecon ?: ""
                            ))
                        }
                        _viewState.value = ViewStateReisAdvies.Success(reisAdviezen)
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

