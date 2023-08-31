package com.yungert.treinplanner.presentation.ui.model

import Data.models.Message
import Data.models.PrimaryMessage

data class ReisAdvies(
    val primaryMessage: PrimaryMessage?,
    val advies: List<Adviezen>,
    val verstrekStation: String,
    val aankomstStation: String,
)

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
    val treinSoortenOpRit: String,
    val alternatiefVervoer: Boolean,
)
