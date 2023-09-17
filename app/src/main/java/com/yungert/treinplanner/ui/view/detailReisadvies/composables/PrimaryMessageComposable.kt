package com.yungert.treinplanner.ui.view.detailReisadvies.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.yungert.treinplanner.R
import com.yungert.treinplanner.presentation.utils.fontsizeLabelCard
import com.yungert.treinplanner.presentation.utils.iconSize
import kotlin.math.max

@Composable
fun PrimaryMessageComposable(
    hoofdBericht: String,
    eindTijdVerstoring: String?,
    minimumExtraReistijd: String?,
    maximumExtraReistijd: String?,
) {
    Card(
        onClick = {},
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Icon",
                    tint = Color.Yellow,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(iconSize)
                )
                Text(
                    text = hoofdBericht,
                    style = fontsizeLabelCard,
                    textAlign = TextAlign.Left,
                )
            }
            if (eindTijdVerstoring != "") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Icon",
                        tint = Color.Yellow,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(iconSize)
                    )
                    Text(
                        text = stringResource(id = R.string.label_verwachte_eindtijd) + ": " + eindTijdVerstoring,
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Left,
                    )
                }
            }
            if (maximumExtraReistijd != null) {
                var extraReistijd = ""
                if (minimumExtraReistijd != null && minimumExtraReistijd != maximumExtraReistijd){
                    extraReistijd = minimumExtraReistijd + "-"
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Icon",
                        tint = Color.Yellow,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(iconSize)
                    )
                    Text(
                        text = stringResource(id = R.string.label_extra_reistijd) + ": " + extraReistijd + maximumExtraReistijd + " " + stringResource(id = R.string.label_minuten),
                        style = fontsizeLabelCard,
                        textAlign = TextAlign.Left,
                    )
                }
            }
        }
    }
}