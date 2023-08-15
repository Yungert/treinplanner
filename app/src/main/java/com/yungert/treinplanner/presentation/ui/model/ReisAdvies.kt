package com.yungert.treinplanner.presentation.ui.model

import Data.models.PrimaryMessage

data class ReisAdvies(
    val verstrekStation: String,
    val aankomstStation: String,
    val geplandeVertrekTijd: String,
    val vertragingInSecondeVertrek: Int,
    val geplandeAankomstTijd: String,
    val vertragingInSecondeAankomst: Int,
    val reisTijdInMinuten : Int,
    val aantalTransfers : Int,
    val reinadviesId : String,
    val bericht : Message?,
)
