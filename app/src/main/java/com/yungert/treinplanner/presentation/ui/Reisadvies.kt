package com.yungert.treinplanner.presentation.ui


import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
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
import com.yungert.treinplanner.presentation.ui.ViewModel.ReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisAdvies
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.utils.Foutmelding
import com.yungert.treinplanner.presentation.ui.utils.LoadingScreen
import com.yungert.treinplanner.presentation.ui.utils.WarningType
import com.yungert.treinplanner.presentation.ui.utils.deviderHeight
import com.yungert.treinplanner.presentation.ui.utils.drukteIndicatorComposable
import com.yungert.treinplanner.presentation.ui.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.ui.utils.iconSize
import com.yungert.treinplanner.presentation.ui.utils.minimaleBreedteTouchControls
import com.yungert.treinplanner.presentation.ui.utils.minimaleHoogteTouchControls
import kotlinx.coroutines.launch

@Composable
fun ShowReisAdvies(
    vertrekStation: String,
    eindStation: String,
    viewModel: ReisAdviesViewModel,
    navController: NavController,
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getReisadviezen(startStation = vertrekStation, eindStation = eindStation)
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when (val response = viewModel.reisavies.collectAsState().value) {
        is ViewStateReisAdvies.Loading -> LoadingScreen()
        is ViewStateReisAdvies.Problem -> {
            Foutmelding(onClick = {
                viewModel.getReisadviezen(startStation = vertrekStation, eindStation = eindStation)
            })
        }

        is ViewStateReisAdvies.Success -> {
            DisplayReisAdvies(reisAdvies = response.details, navController = navController)
        }
    }
}

@Composable
fun DisplayReisAdvies(reisAdvies: List<ReisAdvies>, navController: NavController) {
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
                        text = stringResource(id = R.string.label_reis_advies),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Box(
                            modifier = Modifier
                                .weight(0.25f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = stringResource(id = R.string.label_van_reisadvies) + ": ",
                                style = fontsizeLabelCard,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.75f)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = reisAdvies?.getOrNull(0)?.verstrekStation ?: "",
                                style = fontsizeLabelCard,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.25f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = stringResource(id = R.string.label_naar_reisadvies) + ": ",
                                style = fontsizeLabelCard,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.75f)
                                .fillMaxSize()
                        ) {
                            Text(
                                text = reisAdvies?.getOrNull(0)?.aankomstStation ?: "",
                                style = fontsizeLabelCard,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            reisAdvies.forEach { advies ->
                if (advies.cancelled) {
                    return@forEach
                }

                item {
                    Card(
                        onClick = {
                            navController.navigate(Screen.Reisadvies.withArguments(advies.reinadviesId))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .defaultMinSize(
                                minWidth = minimaleBreedteTouchControls,
                                minHeight = minimaleHoogteTouchControls
                            ),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = advies.geplandeVertrekTijd + advies.vertragingInSecondeVertrek,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center
                                    )
                                    Icon(
                                        imageVector = Icons.Default.East,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = advies.geplandeAankomstTijd + advies.vertragingInSecondeAankomst,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = advies.reisTijd,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CompareArrows,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = (advies.aantalTransfers.toString() + "x"),
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                    Text(
                                        text = advies.treinSoortenOpRit,
                                        style = fontsizeLabelCard,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    drukteIndicatorComposable(
                                        aantalIconen = advies.drukte.aantalIconen,
                                        icon = advies.drukte.icon,
                                        color = advies.drukte.color
                                    )
                                }
                            }
                            if (advies.bericht?.type == WarningType.ALTERNATIVE_TRANSPORT.value) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Icon",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(iconSize)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.alternatief_vervoer_bericht),
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
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}