package com.yungert.treinplanner.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlusOne
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.yungert.treinplanner.R

enum class ErrorState(val txt: Int, val icon: ImageVector) {
    NO_CONNECTION(R.string.error_geen_internet, Icons.Rounded.WifiOff),
    UNKNOWN( R.string.header_error_screen, Icons.Rounded.Warning),
}