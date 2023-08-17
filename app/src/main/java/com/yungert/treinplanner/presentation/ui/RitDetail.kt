package com.yungert.treinplanner.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.ViewModel.RitDetailViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisAdvies
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateRitDetail
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.model.TreinRitDetail
import com.yungert.treinplanner.presentation.ui.utils.LoadingScreen
import com.yungert.treinplanner.presentation.ui.utils.calculateDelay
import com.yungert.treinplanner.presentation.ui.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.ui.utils.iconSize

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
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getReisadviezen(depatureUicCode = depatureUicCode, arrivalUicCode = arrivalUicCode, reisId = reisId, dateTime = dateTime)
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (val response = viewModel.stops.collectAsState().value) {
        is ViewStateRitDetail.Loading -> LoadingScreen()
        is ViewStateRitDetail.Problem -> {

        }

        is ViewStateRitDetail.Success -> {
            DisplayRitDetail(stops = response.details, navController = navController)
        }
    }
}

@Composable
fun DisplayRitDetail(stops: List<TreinRitDetail>, navController: NavController) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        anchorType = ScalingLazyListAnchorType.ItemStart,
        state = listState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            ListHeader {
                Text(
                    text = stringResource(id = R.string.label_rit) + " " + stops.getOrNull(0)?.ritNummer + " " + stringResource(id = R.string.label_eindbestemming_trein) + " " + stops.getOrNull(0)?.eindbestemmingTrein ,
                    textAlign = TextAlign.Center,
                )
            }
        }
        stops.forEach { stop ->
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
                        Text(
                            text = stop.geplandeAankomstTijd + stop.aankomstVertraging,
                            style = fontsizeLabelCard
                        )
                        if(stop.geplandeAankomstTijd != "" && stop.geplandeVertrektTijd != "") {
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
                                    text = stop.geplandeVertrektTijd + stop.vertrekVertraging,
                                    style = fontsizeLabelCard
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
                }
            }
        }

    }
}
