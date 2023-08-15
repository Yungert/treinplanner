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
    data class Success(val details: ReisAdvies) : ViewStateReisAdvies()
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
                        _viewState.value = ViewStateReisAdvies.Success(
                            ReisAdvies(
                                verstrekStation = result.data?.trips?.getOrNull(0)?.legs?.get(0)?.origin?.name ?: "",
                                aankomstStation = result.data?.trips?.getOrNull(0)?.legs?.get(0)?.destination?.name ?: "",
                                vertrekTijd = result.data?.trips?.getOrNull(0)?.legs?.get(0)?.origin?.actualDateTime ?: result.data?.trips?.getOrNull(0)?.legs?.get(0)?.origin?.plannedDateTime ?: "",
                                aankomstTijd = result.data?.trips?.getOrNull(0)?.legs?.get(0)?.destination?.actualDateTime ?: result.data?.trips?.getOrNull(0)?.legs?.get(0)?.destination?.plannedDateTime ?: "",
                                reisTijdInMinuten = result.data?.trips?.getOrNull(0)?.legs?.get(0)?.plannedDurationInMinutes ?: 0,
                                AantalTransfers = result.data?.trips?.getOrNull(0)?.transfers ?: 0
                            )
                        )
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

