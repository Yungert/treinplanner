package com.yungert.treinplanner.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlusOne
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector

enum class ErrorState(val txt: Int, val icon: ImageVector) {
    EMPTY(1, Icons.Rounded.PlusOne),
    NO_CONNECTION(1, Icons.Rounded.WifiOff),
    COULD_NOT_LOAD(1, Icons.Rounded.Sync),
    API_LIMIT(1, Icons.Rounded.Update),
    UNKNOWN(1, Icons.Rounded.QuestionMark),
    SSL(1, Icons.Rounded.Schedule)
}