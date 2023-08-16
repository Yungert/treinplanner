package com.yungert.treinplanner.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Global.getString
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.ViewModel.StationPickerViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisAdvies
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateStationPicker
import com.yungert.treinplanner.presentation.ui.model.StationNamen
import com.yungert.treinplanner.presentation.ui.model.stationNamen
import com.yungert.treinplanner.presentation.ui.utils.LoadingScreen
import com.yungert.treinplanner.presentation.ui.utils.fontsizeLabelCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favourites")


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun ComposeStaions(
    vanStation: String?,
    navController: NavController,
    viewModel: StationPickerViewModel,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    val context = LocalContext.current
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getStations(vanStation = vanStation, context = context)
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (val response = viewModel.stations.collectAsState().value) {
        is ViewStateStationPicker.Loading -> LoadingScreen()
        is ViewStateStationPicker.Problem -> {

        }

        is ViewStateStationPicker.Success -> {
            ShowStations(stations = response.details, vanStation = vanStation,  navController = navController)
        }
    }
}
@Composable
fun ShowStations(
    stations: List<StationNamen>,
    vanStation: String?,
    navController: NavController,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        val listState = rememberScalingLazyListState()
        ScalingLazyColumn(
            anchorType = ScalingLazyListAnchorType.ItemStart,
            state = listState,
            modifier = Modifier.fillMaxWidth(),
        ) {

            item {
                ListHeader {
                    Text(
                        text = if (vanStation != null) stringResource(id = R.string.kies_aankomst_station) else stringResource(id = R.string.kies_vertrek_station),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            stations.forEach { station ->
                item {
                    StationCard(
                        item = station,
                        navController = navController,
                        context = context,
                        vanStation = vanStation
                    )
                }
            }
        }
    }
}


@Composable
fun StationCard(
    item: StationNamen,
    navController: NavController,
    context: Context,
    vanStation: String?
) {
    var isFavorite by remember { mutableStateOf(item.favorite) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        onClick = {
            if (vanStation != null) {
                navController.navigate(
                    Screen.Reisadvies.withArguments(
                        vanStation,
                        item.hiddenValue
                    )
                )
            } else {
                navController.navigate(Screen.StationNaarKiezen.withArguments(item.hiddenValue))
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Text(
                text = item.displayValue,
                style = fontsizeLabelCard, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        isFavorite = !isFavorite
                        CoroutineScope(Dispatchers.IO).launch {
                            edit(
                                context = context,
                                key = item.hiddenValue,
                                value = item.displayValue
                            )
                        }
                    }
            )
        }
    }
}

suspend fun edit(context: Context, key: String, value: String) {
    var exist = (get(context, key) != null)


    val dataStoreKey = stringPreferencesKey(key)
    if (!exist) {
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    } else {
        context.dataStore.edit { settings ->
            settings.remove(dataStoreKey)
        }
    }
}

suspend fun get(context: Context, key: String): String? {
    val dataStoreKey = stringPreferencesKey(key)
    val preference = context.dataStore.data.first()
    return preference[dataStoreKey]
}