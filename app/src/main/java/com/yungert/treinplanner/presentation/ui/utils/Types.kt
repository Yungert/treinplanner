package com.yungert.treinplanner.presentation.ui.utils

enum class TransferType(val value: String) {
    CROSS_PLATFORM("CROSS_PLATFORM"),
    TRANSFER_TIME("TRANSFER_TIME"),
    IMPOSSIBLE_TRANSFER("IMPOSSIBLE_TRANSFER");
    companion object {
        fun fromValue(value: String): TransferType? {
            return values().find { it.value == value }
        }
    }
}
enum class WarningType(val value: String) {
    WARNING("warning"),
    DISRUPTION("DISRUPTION"),
    MAINTENANCE("MAINTENANCE"),
    ERROR("error"),
    ALTERNATIVE_TRANSPORT("ALTERNATIVE_TRANSPORT");
    companion object {
        fun fromValue(value: String): WarningType? {
            return values().find { it.value == value }
        }
    }
}

enum class CrowdForecast(val value: String) {
    rustig("LOW"),
    gemiddeld("MEDIUM"),
    druk("HIGH"),
    onbekend("onbekend");
    companion object {
        fun fromValue(value: String): CrowdForecast? {
            return values().find { it.value == value }
        }
    }
}