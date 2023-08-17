package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import Data.api.Resource
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.dataStore
import com.yungert.treinplanner.presentation.ui.get
import com.yungert.treinplanner.presentation.ui.model.StationNamen
import com.yungert.treinplanner.presentation.ui.model.stationNamen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


sealed class ViewStateStationPicker {
    object Loading : ViewStateStationPicker()
    data class Success(val details: List<StationNamen>) : ViewStateStationPicker()
    data class Problem(val exception: ErrorState?) : ViewStateStationPicker()
}


class StationPickerViewModel() : ViewModel() {
    private val _viewState =
        MutableStateFlow<ViewStateStationPicker>(ViewStateStationPicker.Loading)
    val stations = _viewState.asStateFlow()
    private val nsApiRepository: NsApiRepository = NsApiRepository(NSApiClient)


    @SuppressLint("MissingPermission")
    fun getStationsMetGps(vanStation: String?, context: Context) {
        val stations = mutableListOf<StationNamen>()

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                val latCoordinates = location?.latitude ?: 0.0
                val lngCoordinates = location?.longitude ?: 0.0
                if (latCoordinates != 0.0 && lngCoordinates != 0.0) {
                    getClosedStation(latCoordinates, lngCoordinates, context, vanStation)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    fun getClosedStation(
        latCoordinates: Double,
        lngCoordinates: Double,
        context: Context,
        vanStation: String?
    ) {
        var _stationNamen = stationNamen
        val stations = mutableListOf<StationNamen>()
        viewModelScope.launch {
            if (latCoordinates != 0.0 && lngCoordinates != 0.0) {
                nsApiRepository.fetchDichtbijzijndeStation(
                    lat = latCoordinates.toString(),
                    lng = lngCoordinates.toString()
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.payload?.forEach { station ->
                                station.locations?.forEach { locatie ->
                                    val dichtbijStation =
                                        _stationNamen.find { it.hiddenValue == locatie.stationCode?.lowercase() }
                                    dichtbijStation?.distance = locatie?.distance
                                }
                            }

                            _stationNamen.forEach { station ->
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
                                compareByDescending<StationNamen> { it.distance }
                                    .thenByDescending { it.favorite }
                                    .thenBy { it.distance == null }
                            )
                            _viewState.value = ViewStateStationPicker.Success(sortedStations)
                        }

                        is Resource.Loading -> {

                        }

                        is Resource.Error -> {

                        }
                    }

                }
            }
        }


        suspend fun get(context: Context, key: String): String? {
            val dataStoreKey = stringPreferencesKey(key)
            val preference = context.dataStore.data.first()
            return preference[dataStoreKey]
        }
    }
}

