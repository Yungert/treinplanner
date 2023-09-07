package com.yungert.treinplanner.ui.view.reisAdvies.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize

@Composable
fun TransferNietmogelijkComposable(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Icon",
            tint = Color.Red,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(iconSize)
        )
        Text(
            text = stringResource(id = R.string.label_overstap_niet_mogelijk),
            style = fontsizeLabelCard,
            textAlign = TextAlign.Left,
        )
    }
}