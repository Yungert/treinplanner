package com.yungert.treinplanner.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.yungert.treinplanner.R

enum class ErrorState(val txt: Int, val icon: ImageVector) {
    NO_CONNECTION(R.string.error_geen_internet, Icons.Rounded.WifiOff),
    UNKNOWN( R.string.header_error_screen, Icons.Rounded.Warning),
}