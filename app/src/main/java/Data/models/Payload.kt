package Data.models


import com.yungert.treinplanner.presentation.ui.model.MessageData

data class Payload(
    val notes: List<Note>?,
    val productNumbers: List<String>,
    val stops: List<Stop>,
    val allowCrowdReporting: Boolean,
    val source: String?,
    var transferMessage: List<TransferMessage>?,
    var alternativeTransport : Boolean,
    var message : List<MessageData>,
)