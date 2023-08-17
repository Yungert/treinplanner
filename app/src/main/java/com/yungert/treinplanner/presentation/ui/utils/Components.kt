package com.yungert.treinplanner.presentation.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.ui.model.Message
import java.time.Duration
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
private var lastFormattedTime : String? = null
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
        )
        Text(stringResource(id = R.string.label_loading))
    }
}

fun formatTime(time: String?): String {
    if (time == null) {
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

fun calculateTravalTime(duratinInMinutes: Int): String {
    val uur = duratinInMinutes / 60
    if (uur > 0) {
        return uur.toString() + "H:" + (duratinInMinutes % 60).toString() + "M"
    }
    return duratinInMinutes.toString() + "M"
}

fun calculateDelay(delayInSeconds: Long?): String {

    if (delayInSeconds == null) {
        return " (-) "
    }

    if(delayInSeconds == 0.toLong()){
        return ""
    }

    var minuten = delayInSeconds / 60
    var seconden = delayInSeconds % 60

    if(seconden > 30){
        minuten++
    }

    if (minuten > 0) {
        return " +"+ minuten.toString()
    }

    if(seconden > 30 && seconden < 60) {
        return " +1"
    }
    return ""
}

@Composable
fun ShowMessage(msg: List<Message?>) {
    if (msg == null) {
        return
    }

    msg.forEach { bericht ->
        if(bericht == null){
            return
        }
        val color = when (WarningType.fromValue(bericht!!.type)) {
            WarningType.WARNING -> Color.Yellow
            WarningType.ERROR -> Color.Red
            WarningType.MAINTENANCE -> Color.Yellow
            else -> Color.White
        }
        val icon = when (WarningType.fromValue(bericht!!.type)) {
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
                        text = bericht!!.message.text,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun calculateTimeDiff(startTime : String?, endTime: String?): String {
    if(startTime == null || endTime == null){
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
fun drukteIndicatorComposable(aantalIconen: Int, icon: ImageVector, color: Color){
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