package Data.models

data class Note(
    val text: String?,
    val value: String?,
    val accessibilityValue: String?,
    val key: String?,
    val noteType: String?,
    val priority: Int?,
    val routeIdxFrom: Int?,
    val routeIdxTo: Int?,
    val link: Link?,
    val isPresentationRequired: Boolean?,
    val category: String?
)