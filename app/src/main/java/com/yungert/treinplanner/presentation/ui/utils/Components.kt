package com.yungert.treinplanner.presentation.ui.utils

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RailwayAlert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.yungert.treinplanner.presentation.ui.model.Message
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private var lastFormattedTime: String? = null

@Composable
fun LoadingScreen(loadingText: String?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text((loadingText ?: stringResource(id = R.string.label_loading)) + "...")
    }
}

fun formatTime(time: String?): String {
    if (time == null || time == "") {
        return ""
    }
    val offsetIndex = time.indexOf('+')

    val modifiedTimestamp = StringBuilder(time).insert(offsetIndex + 3, ':').toString()

    val offsetDateTime =
        OffsetDateTime.parse(modifiedTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)


    val localTime = offsetDateTime.toLocalTime()

    val formattedTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    lastFormattedTime = formattedTime
    return formattedTime
}

fun formatTravelTime(duratinInMinutes: Int): String {
    val uur = duratinInMinutes / 60
    var stringReistijd = ""
    if (uur > 0) {
        stringReistijd = uur.toString() + ":"
    } else {
        stringReistijd = "0:"
    }
    if (duratinInMinutes % 60 < 10) {
        stringReistijd = stringReistijd + "0" + (duratinInMinutes % 60).toString()
    } else {
        stringReistijd += (duratinInMinutes % 60).toString()
    }
    return stringReistijd
}

fun calculateDelay(delayInSeconds: Long?): String {

    if (delayInSeconds == null) {
        return "(-)"
    }

    if (delayInSeconds == 0.toLong()) {
        return ""
    }

    var minuten = delayInSeconds / 60
    var seconden = delayInSeconds % 60

    if (seconden > 30) {
        minuten++
    }

    if (minuten > 0) {
        return "+" + minuten.toString()
    }

    if (seconden > 30 && seconden < 60) {
        return "+1"
    }
    return ""
}

@Composable
fun ShowMessage(msg: List<Message?>) {
    if (msg == null) {
        return
    }

    msg.forEach { bericht ->
        if (bericht == null) {
            return
        }
        val color = when (WarningType.fromValue(bericht.type)) {
            WarningType.WARNING -> Color.Yellow
            WarningType.ERROR -> Color.Red
            WarningType.MAINTENANCE -> Color.Yellow
            else -> Color.White
        }
        val icon = when (WarningType.fromValue(bericht.type)) {
            WarningType.WARNING -> Icons.Default.Warning
            WarningType.ERROR -> Icons.Default.Info
            WarningType.DISRUPTION -> Icons.Default.RailwayAlert
            WarningType.MAINTENANCE -> Icons.Default.Construction
            else -> Icons.Default.Warning
        }
        Card(
            onClick = {},
            modifier = Modifier.padding(2.dp)
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
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon",
                        tint = color,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(iconSize)
                    )
                    Text(
                        text = bericht.message.text,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun calculateTimeDiff(startTime: String?, endTime: String?): String {
    if (startTime == null || endTime == null) {
        return ""
    }

    var start = formatTime(startTime)
    var end = formatTime(endTime)

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startLocalTime = LocalTime.parse(start, formatter)
    val endLocalTime = LocalTime.parse(end, formatter)

    val duration = Duration.between(startLocalTime, endLocalTime)

    return duration.toMinutes().toString()
}

@Composable
fun DrukteIndicatorComposable(aantalIconen: Int, icon: ImageVector, color: Color) {
    repeat(aantalIconen) {
        Icon(
            imageVector = icon,
            contentDescription = "Icon",
            tint = color,
            modifier = Modifier
                .size(iconSize)
        )
    }
}

@Composable
fun Foutmelding(onClick: () -> Unit) {
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
                        text = stringResource(id = R.string.header_error_screen),
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
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(40.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(id = R.string.text_onbekende_fout),
                            style = fontsizeLabelCard,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Card(
                            onClick = onClick,
                            modifier = Modifier.defaultMinSize(
                                minWidth = minimaleBreedteTouchControls,
                                minHeight = minimaleHoogteTouchControls
                            ).padding(bottom = 30.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.label_opnieuw_proberen),
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

fun convertMeterNaarKilometer(afstandInMeters: Double): String{
    if (afstandInMeters > 1000){
        val kilometers = afstandInMeters / 1000
        return "${"%.1f".format(kilometers)} km"
    }
    return "${"%.1f".format(afstandInMeters)} m"
}
