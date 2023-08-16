package com.yungert.treinplanner.presentation.ui.ViewModel

import Data.Repository.NsApiRepository
import Data.api.NSApiClient
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yungert.treinplanner.presentation.ui.ErrorState
import com.yungert.treinplanner.presentation.ui.dataStore
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.model.StationNamen
import com.yungert.treinplanner.presentation.ui.model.stationNamen
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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


    fun getStations(vanStation: String?, context: Context) {
        val stations = mutableListOf<StationNamen>()
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

