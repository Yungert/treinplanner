package com.yungert.treinplanner.presentation.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.yungert.treinplanner.presentation.ui.ViewModel.ReisAdviesViewModel
import com.yungert.treinplanner.presentation.ui.ViewModel.ViewStateReisAdvies
import com.yungert.treinplanner.presentation.ui.model.ReisAdvies
import com.yungert.treinplanner.presentation.ui.utils.LoadingScreen
import com.yungert.treinplanner.presentation.ui.utils.calculateTravalTime
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
            DisplayReisAdvies(reisAdvies = response.details)
        }
    }
}

@Composable
fun DisplayReisAdvies(reisAdvies: List<ReisAdvies>) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        anchorType = ScalingLazyListAnchorType.ItemStart,
        state = listState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            ListHeader {
                Text(text = "Reisopties")
            }
        }
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Van : " + reisAdvies?.getOrNull(0)?.verstrekStation,
                        style = fontsizeLabelCard,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Naar : " + reisAdvies?.getOrNull(0)?.aankomstStation,
                        style = fontsizeLabelCard,
                    )
                }
            }
        }

        reisAdvies.forEach { advies ->
            item {
                Card(
                    onClick = {
                        // TODO navcontroller
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
                                text = formatTime(advies.vertrekTijd),
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
                                text = formatTime(advies.aankomstTijd),
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
                                text = (advies.aantalTransfers.toString() + "x | "),
                                style = fontsizeLabelCard,
                                textAlign = TextAlign.Center
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
                                text = (calculateTravalTime(advies.reisTijdInMinuten)),
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