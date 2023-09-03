package com.yungert.treinplanner.presentation.ui.model

import androidx.annotation.Keep
import com.yungert.treinplanner.presentation.Data.models.Message
import com.yungert.treinplanner.presentation.Data.models.PrimaryMessage
@Keep
data class ReisAdvies(
    val primaryMessage: List<PrimaryMessage>?,
    val advies: List<Adviezen>,
    val verstrekStation: String,
    val aankomstStation: String,
)
@Keep
data class Adviezen(
    val verstrekStation: String,
    val aankomstStation: String,
    val geplandeVertrekTijd: String,
    val vertrekVertraging: String,
    val geplandeAankomstTijd: String,
    val aankomstVertraging: String,
    val actueleReistijd: String,
    val geplandeReistijd: String,
    val aantalTransfers: Int,
    val reinadviesId: String,
    val bericht: List<Message>?,
    val drukte: DrukteIndicator,
    val cancelled: Boolean,
    val aandachtsPunten: String?,
    val treinSoortenOpRit: String,
    val alternatiefVervoer: Boolean,
)
