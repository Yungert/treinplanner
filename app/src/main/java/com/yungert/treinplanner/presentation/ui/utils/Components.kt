package com.yungert.treinplanner.presentation.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
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
        Text(text = "Loading, please wait")
    }
}

fun formatTime(time: String?): String {
    if (time == null) {
        return "Foutmelding tijd omzetten!"
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
