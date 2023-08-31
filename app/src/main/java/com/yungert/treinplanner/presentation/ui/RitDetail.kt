package com.yungert.treinplanner.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Tram
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.ViewModel.RitDetailViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisAdvies
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateRitDetail
import com.yungert.treinplanner.presentation.ui.model.TreinRitDetail
import com.yungert.treinplanner.presentation.ui.utils.LoadingScreen
import com.yungert.treinplanner.presentation.ui.utils.DrukteIndicatorComposable
import com.yungert.treinplanner.presentation.ui.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.ui.utils.iconSize
import com.yungert.treinplanner.presentation.ui.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.ui.utils.minimaleHoogteTouchControls
import kotlinx.coroutines.launch

@Composable
fun ShowRitDetail(
    depatureUicCode: String,
    arrivalUicCode: String,
    reisId: String,
    dateTime: String,
    viewModel: RitDetailViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val ritDetailViewModel = viewModel
    when (val result = ritDetailViewModel.stops.collectAsState().value) {
        is ViewStateRitDetail.Success -> {
            DisplayRitDetail(
                stops = result.details,
                navController = navController,
            )
        }
        else -> {
            val context = LocalContext.current
            DisposableEffect(lifeCycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.getReisadviezen(
                            depatureUicCode = depatureUicCode,
                            arrivalUicCode = arrivalUicCode,
                            reisId = reisId,
                            dateTime = dateTime,
                            context = context
                        )
                    }
                }
                lifeCycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(observer)
                }
            }

            when (val response = viewModel.stops.collectAsState().value) {
                is ViewStateRitDetail.Loading -> LoadingScreen(loadingText = stringResource(id = R.string.laadt_text_rit_gegevens))
                is ViewStateRitDetail.Problem -> {

                }

                is ViewStateRitDetail.Success -> {
                    DisplayRitDetail(stops = response.details, navController = navController)
                }
            }
        }
    }
}

@Composable
fun DisplayRitDetail(stops: List<TreinRitDetail>, navController: NavController) {
    val focusRequester = remember { FocusRequester() }
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            anchorType = ScalingLazyListAnchorType.ItemStart,
            modifier = Modifier
                .fillMaxWidth()
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
            state = listState)
        {
            item {
                ListHeader {
                    Text(
                        text = stringResource(id = R.string.label_rit) + " " + stops.getOrNull(0)?.ritNummer + " " + stringResource(
                            id = R.string.label_eindbestemming_trein
                        ) + " " + stops.getOrNull(0)?.eindbestemmingTrein,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            //showExtraInfo.value = !showExtraInfo.value
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if(stops.getOrNull(0)?.materieelType != "" && stops.getOrNull(0)?.aantalTreinDelen != "") {
                            Text(
                                text = stops.getOrNull(0)?.materieelType + "-" + stops.getOrNull(0)?.aantalTreinDelen + " ",
                                style = fontsizeLabelCard
                            )
                        }
                        if(stops.getOrNull(0)?.aantalZitplaatsen != "") {
                            Icon(
                                imageVector = Icons.Default.AirlineSeatReclineNormal,
                                contentDescription = "Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(iconSize)
                                    .padding(vertical = 2.dp)
                            )
                            Text(
                                text = stops.getOrNull(0)?.aantalZitplaatsen
                                    ?: stringResource(id = R.string.label_onbekend),
                                style = fontsizeLabelCard
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if(stops.get(0).materieelNummers.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Train,
                                contentDescription = "Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(iconSize)
                                    .padding(vertical = 2.dp)
                            )

                            stops.getOrNull(0)?.materieelNummers?.forEachIndexed { index, materieel ->
                                Text(
                                    text = materieel,
                                    style = fontsizeLabelCard
                                )
                                if (index < (stops.getOrNull(0)?.materieelNummers?.size?.minus(1)
                                        ?: 0)
                                ) {
                                    Text(
                                        text = ", ",
                                        style = fontsizeLabelCard
                                    )
                                }
                            }
                        }
                    }
                }
            }
            stops.forEachIndexed { index, stop ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .defaultMinSize(
                                minWidth = minimaleBreedteTouchControls,
                                minHeight = minimaleHoogteTouchControls
                            ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Divider(
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Text(
                                text = if (stop.geplandeAankomstTijd != "") stop.geplandeAankomstTijd  else stop.geplandeVertrektTijd,
                                style = fontsizeLabelCard
                            )
                            Text(
                                text =  if (stop.geplandeAankomstTijd != "") stop.aankomstVertraging  else stop.vertrekVertraging,
                                style = fontsizeLabelCard,
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 1.dp)
                            )
                            if (stop.geplandeAankomstTijd != "" && stop.geplandeVertrektTijd != "") {
                                if (stop.geplandeAankomstTijd != stop.geplandeVertrektTijd) {
                                    Icon(
                                        imageVector = Icons.Default.East,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(21.dp)
                                            .padding(vertical = 2.dp)
                                    )
                                    Text(
                                        text = stop.geplandeVertrektTijd,
                                        style = fontsizeLabelCard
                                    )
                                    Text(
                                        text = stop.vertrekVertraging,
                                        style = fontsizeLabelCard,
                                        color = Color.Red,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 1.dp)
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stop.stationNaam,
                                style = fontsizeLabelCard
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (stop.spoor != null) {
                            Icon(
                                imageVector = Icons.Default.Tram,
                                contentDescription = "Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(iconSize)
                                    .padding(vertical = 2.dp)
                            )
                                Text(
                                    text = stop.spoor + " ",
                                    style = fontsizeLabelCard
                                )
                            }
                            DrukteIndicatorComposable(
                                aantalIconen = stop.drukte.aantalIconen,
                                icon = stop.drukte.icon,
                                color = stop.drukte.color
                            )
                        }


                        if (index == stops.size - 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 60.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.text_eindpunt_van_jouw_reis),
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}
