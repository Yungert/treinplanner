package com.yungert.treinplanner.presentation.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
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
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.Navigation.Screen
import com.yungert.treinplanner.presentation.ui.ViewModel.ReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisAdvies
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.utils.CrowdForecast
import com.yungert.treinplanner.presentation.ui.utils.LoadingScreen
import com.yungert.treinplanner.presentation.ui.utils.ShowMessage
import com.yungert.treinplanner.presentation.ui.utils.WarningType
import com.yungert.treinplanner.presentation.ui.utils.calculateDelay
import com.yungert.treinplanner.presentation.ui.utils.calculateTravalTime
import com.yungert.treinplanner.presentation.ui.utils.deviderHeight
import com.yungert.treinplanner.presentation.ui.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.ui.utils.formatTime
import com.yungert.treinplanner.presentation.ui.utils.iconSize

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

        }

        is ViewStateReisAdvies.Success -> {
            DisplayReisAdvies(reisAdvies = response.details, navController = navController)
        }
    }
}

@Composable
fun DisplayReisAdvies(reisAdvies: List<ReisAdvies>, navController: NavController) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        anchorType = ScalingLazyListAnchorType.ItemStart,
        state = listState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            ListHeader {
                Text(
                    text = stringResource(id = R.string.label_reis_advies),
                    textAlign = TextAlign.Center,
                )
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_van_reisadvies) + ":",
                            style = fontsizeLabelCard,
                            textAlign = TextAlign.Right
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = reisAdvies?.getOrNull(0)?.verstrekStation ?: "",
                            style = fontsizeLabelCard,
                            textAlign = TextAlign.Left
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_naar_reisadvies) + ":",
                            style = fontsizeLabelCard,
                            textAlign = TextAlign.Right
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = reisAdvies?.getOrNull(0)?.aankomstStation ?: "",
                            style = fontsizeLabelCard,
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }
        }

        reisAdvies.forEach { advies ->
            item {
                Card(
                    onClick = {
                              navController.navigate(Screen.Reisadvies.withArguments(advies.reinadviesId))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .defaultMinSize(minHeight = 24.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = formatTime(advies.geplandeVertrekTijd) + calculateDelay(
                                    advies.vertragingInSecondeVertrek.toLong()),
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
                                text = formatTime(advies.geplandeAankomstTijd) + calculateDelay(
                                    advies.vertragingInSecondeAankomst.toLong()),
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
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = "Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(iconSize)
                            )
                            Text(
                                text = (advies.aantalTransfers.toString() + "x "),
                                style = fontsizeLabelCard,
                                textAlign = TextAlign.Center
                            )
                            Divider(
                                modifier = Modifier
                                    .fillMaxHeight(deviderHeight)
                                    .width(1.dp),
                                color = Color.White,
                            )
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(iconSize)
                            )
                            Text(
                                text = calculateTravalTime(advies.reisTijdInMinuten) + " ",
                                style = fontsizeLabelCard,
                                textAlign = TextAlign.Center
                            )
                            Divider(
                                modifier = Modifier
                                    .fillMaxHeight(deviderHeight)
                                    .width(1.dp),
                                color = Color.White,
                            )

                            repeat(advies.drukte.aantalIconen) {
                                Icon(
                                    imageVector = advies.drukte.icon,
                                    contentDescription = "Icon",
                                    tint = advies.drukte.color,
                                    modifier = Modifier
                                        .size(iconSize)
                                )
                            }

                        }
                        if(advies.bericht?.type == WarningType.ALTERNATIVE_TRANSPORT.value) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
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