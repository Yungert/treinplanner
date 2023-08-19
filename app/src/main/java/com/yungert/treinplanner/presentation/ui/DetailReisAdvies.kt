package com.yungert.treinplanner.presentation.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardTab
import androidx.compose.material.icons.filled.Start
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.ViewModel.DetailReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateDetailReisAdvies
import com.yungert.treinplanner.presentation.ui.model.RitDetail
import com.yungert.treinplanner.presentation.ui.utils.LoadingScreen
import com.yungert.treinplanner.presentation.ui.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.ui.utils.iconSize
import com.yungert.treinplanner.presentation.ui.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.ui.utils.minimaleHoogteTouchControls
import kotlinx.coroutines.launch

@Composable
fun ShowDetailReisAdvies(
    reisADviesId: String,
    viewModel: DetailReisAdviesViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getReisadviesDetail(reisAdviesId = reisADviesId)
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (val response = viewModel.reisavies.collectAsState().value) {
        is ViewStateDetailReisAdvies.Loading -> LoadingScreen()
        is ViewStateDetailReisAdvies.Problem -> {

        }

        is ViewStateDetailReisAdvies.Success -> {
            DisplayDetailReisAdvies(rit = response.details, navController = navController)
        }
    }
}

@Composable
fun DisplayDetailReisAdvies(rit: List<RitDetail>, navController: NavController) {
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
                        text = stringResource(id = R.string.label_jouw_reis_naar) + " " + rit[rit.size - 1].naamAankomstStation,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            rit.forEachIndexed { index, reis ->
                item {
                    if (index > 0 && reis.overstapTijd != "") {
                        Card(
                            onClick = {
                            },
                            modifier = Modifier
                                .padding(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    if (reis.alternatiefVervoer) {
                                        Text(
                                            text = reis.overstapTijd + " " + stringResource(id = R.string.text_tijd_overstap_op_alternatief_vervoer),
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        Text(
                                            text = reis.overstapTijd + " " + stringResource(id = R.string.text_tijd_overstap_op_andere_trein) + " " + reis.vertrekSpoor,
                                            style = fontsizeLabelCard,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Card(
                        onClick = {
                            navController.navigate(
                                Screen.RitDetail.withArguments(
                                    reis.vertrekStationUicCode,
                                    reis.aankomstStationUicCode,
                                    reis.ritId,
                                    reis.datum
                                )
                            )
                        },
                        modifier = if (index == rit.size - 1) Modifier.padding(bottom = 40.dp) else Modifier
                            .padding(
                                bottom = 0.dp
                            )
                            .defaultMinSize(
                                minWidth = minimaleBreedteTouchControls,
                                minHeight = minimaleHoogteTouchControls
                            )

                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = reis.treinOperator + " " + reis.treinOperatorType + " " + reis.ritNummer + " " + stringResource(
                                        id = R.string.label_eindbestemming_trein
                                    ) + ":",
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = reis.eindbestemmingTrein,
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Start,
                                    contentDescription = "Icon",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .size(12.dp)
                                )
                                Text(
                                    text = reis.naamVertrekStation,
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = reis.geplandeVertrektijd,
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = reis.vertragingInSecondeVertrekStation,
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center,
                                    color = Color.Red,
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                )
                                if(reis.vertrekSpoor != null) {
                                    Icon(
                                        imageVector = Icons.Default.Tram,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(12.dp)
                                    )
                                    Text(
                                        text = reis.vertrekSpoor,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardTab,
                                    contentDescription = "Icon",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .size(12.dp)
                                )
                                Text(
                                    text = reis.naamAankomstStation,
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = reis.geplandeAankomsttijd,
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = reis.vertragingInSecondeAankomstStation,
                                    style = fontsizeLabelCard,
                                    textAlign = TextAlign.Center,
                                    color = Color.Red,
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                )
                                if(reis.aankomstSpoor != null) {
                                    Icon(
                                        imageVector = Icons.Default.Tram,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = reis.aankomstSpoor,
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
}



