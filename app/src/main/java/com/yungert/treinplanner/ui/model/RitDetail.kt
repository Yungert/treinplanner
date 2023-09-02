package com.yungert.treinplanner.presentation.ui.model

data class TreinRitDetail(
    val eindbestemmingTrein: String,
    val ritNummer: String,
    val stationNaam: String,
    val spoor: String?,
    val materieelType: String,
    val aantalZitplaatsen: String,
    val aantalTreinDelen: String,
    val ingekort: Boolean,
    val geplandeAankomstTijd: String,
    val actueleAankomstTijd: String,
    val aankomstVertraging: String,
    val geplandeVertrektTijd: String,
    val actueleVertrekTijd: String,
    val vertrekVertraging: String,
    val drukte: DrukteIndicator,
    val punctualiteit: String,
    val materieelNummers: List<String>
)

