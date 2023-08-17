package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.dataStore
import com.yungert.treinplanner.presentation.ui.model.StationNamen
import com.yungert.treinplanner.presentation.ui.model.stationNamen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


sealed class ViewStateStationPicker {
    object Loading : ViewStateStationPicker()
    data class Success(val details: List<StationNamen>) : ViewStateStationPicker()
    data class Problem(val exception: ErrorState?) : ViewStateStationPicker()
}

sealed class Location {
    object Loading : Location()
    data class Success(val details: List<StationNamen>) : Location()
    data class Problem(val exception: ErrorState?) : Location()
}

private lateinit var fusedLocationClient: FusedLocationProviderClient

class StationPickerViewModel() : ViewModel() {
    private val _viewState =
        MutableStateFlow<ViewStateStationPicker>(ViewStateStationPicker.Loading)
    val stations = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)


    @SuppressLint("MissingPermission")
    fun getStations(vanStation: String?, context: Context) {
        val stations = mutableListOf<StationNamen>()
//        var longitude = 0.0
//        var latitude = 0.0
//        var dichtbijZijndeStations = mutableListOf<StationNamen>()
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//        viewModelScope.launch {
//            fusedLocationClient.lastLocation
//                .addOnCompleteListener { task: Task<android.location.Location> ->
//                    if (task.isSuccessful && task.result != null) {
//                        val location = task.result
//                        latitude = location.latitude
//                        longitude = location.longitude
//                    }
//                }
//
//
//
//
//            nsApiRepository.fetchDichtbijzijndeStation(
//                lat = latitude.toString(),
//                lng = longitude.toString()
//            ).collect { result ->
//                result.data?.payload?.forEach { data ->
//                    data.locations?.forEach { locatie ->
//                        dichtbijZijndeStations.add(StationNamen(
//                            displayValue = locatie.name ?: "",
//                            hiddenValue = locatie.stationCode?.lowercase() ?: "",
//                            favorite = false,
//                            distance = locatie.distance ?: -1.0
//                        ))
//                    }
//                }
//            }
//        }

        viewModelScope.launch {
            stationNamen.forEach { station ->
                if (get(context = context, key = station.hiddenValue) != null) {
                    station.favorite = true
                }
                if (vanStation == null) {
                    stations.add(station)
                } else if (station.hiddenValue != vanStation) {
                    stations.add(station)
                }
            }
            val sortedStations = stations.sortedWith(
                compareByDescending<StationNamen> { it.favorite }
                    .thenBy { it.displayValue }
            )
            _viewState.value = ViewStateStationPicker.Success(sortedStations)
        }
    }


    suspend fun get(context: Context, key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preference = context.dataStore.data.first()
        return preference[dataStoreKey]
    }

}

