package com.yungert.treinplanner.presentation.ui.model

data class ReisAdvies(
    val verstrekStation: String,
    val aankomstStation: String,
    val geplandeVertrekTijd: String,
    val vertragingInSecondeVertrek: String,
    val geplandeAankomstTijd: String,
    val vertragingInSecondeAankomst: String,
    val reisTijd : String,
    val aantalTransfers : Int,
    val reinadviesId : String,
    val bericht : Message?,
    val drukte : DrukteIndicator,
    val cancelled : Boolean,
)
