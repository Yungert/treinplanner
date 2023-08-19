package com.yungert.treinplanner.presentation.ui.model

import Data.models.NesProperties
import Data.models.TransferMessage

data class RitDetail(
    val treinOperator: String,
    val treinOperatorType: String,
    val ritNummer: String,
    val eindbestemmingTrein: String,
    val datum: String,

    val naamVertrekStation: String,
    val geplandeVertrektijd: String,
    val actueleVertrektijd: String,
    val vertrekSpoor: String?,
    val vertrekVertraging: String,
    val vertrekStationUicCode : String,

    val naamAankomstStation: String,
    val geplandeAankomsttijd: String,
    val aankomstSpoor: String?,
    val aankomstVertraging: String,
    val actueleAankomstTijd: String,
    val aankomstStationUicCode : String,

    val berichten: List<Message>?,
    val transferBericht : List<TransferMessage>?,
    val alternatiefVervoer : Boolean,
    val ritId : String,

    val overstapTijd : String?,
    )

data class Message(
    val title: String,

    val nesProperties: NesProperties,
    val message: MessageData,
    val type: String
)

data class MessageData(
    val id: String,
    val externalId: String,
    val head: String,
    val text: String,
    val lead: String,
    val routeIdxFrom: Int,
    val routeIdxTo: Int,
    val type: String,
    val nesProperties: NesProperties,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String
)