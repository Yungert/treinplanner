package com.yungert.treinplanner.presentation.ui.model

data class ReisAdvies(
    val verstrekStation: String,
    val aankomstStation: String,
    val vertrekTijd: String,
    val aankomstTijd: String,
    val reisTijdInMinuten : Int,
    val aantalTransfers : Int,
    val reinadviesId : String,
)
