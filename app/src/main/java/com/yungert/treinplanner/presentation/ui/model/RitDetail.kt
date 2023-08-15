package com.yungert.treinplanner.presentation.ui.model

import Data.models.NesProperties
import Data.models.TransferMessage

data class RitDetail(
    val treinOperator: String,
    val treinOperatorType: String,
    val ritNummer: String,
    val eindbestemmingTrein: String,

    val naamVertrekStation: String,
    val geplandeVertrektijd: String,
    val vertrekSpoor: String,
    val vertragingInSecondeVertrekStation: Int,

    val naamAankomstStation: String,
    val geplandeAankomsttijd: String,
    val aankomstSpoor: String,
    val vertragingInSecondeAankomstStation: Int,

    val berichten: List<Message>?,
    val transferBericht : List<TransferMessage>?,

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