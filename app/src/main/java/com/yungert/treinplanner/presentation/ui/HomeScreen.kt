package com.yungert.treinplanner.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.yungert.treinplanner.presentation.ui.utils.iconSize

@Composable
fun HomeScreen(navController: NavController) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        anchorType = ScalingLazyListAnchorType.ItemStart,
        state = listState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            ListHeader {
                Text(
                    text = stringResource(id = R.string.label_header_homescreen),
                    textAlign = TextAlign.Center,
                )
            }
        }

        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            onClick = {
                                navController.navigate(Screen.StationVanKiezen.route)
                            },
                            modifier = Modifier.size(50.dp),

                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "Icon",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(horizontal = 1.dp)
                                        .size(iconSize),
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            onClick = {
                                navController.navigate(Screen.GpsPermission.route)
                            },
                            modifier = Modifier.size(50.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Icon",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(horizontal = 1.dp)
                                        .size(iconSize)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}