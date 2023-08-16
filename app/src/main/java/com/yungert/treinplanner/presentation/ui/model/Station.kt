package com.yungert.treinplanner.presentation.ui.model

data class StationNamen(val displayValue: String, val hiddenValue: String, var favorite : Boolean = false )
var stationNamen = listOf(
    StationNamen("Amsterdam Centraal", "asd"),
    StationNamen("Zaandijk Zaanse Schans", "zzs"),
    StationNamen("Utrecht Centraal", "ut"),
    StationNamen("Zwolle", "zl"),
    StationNamen("Apeldoorn", "apd"),
    StationNamen("Schagen", "sgn"),
    StationNamen("Eindhoven Centraal", "ehv"),
    StationNamen("Arhnhem Centraal", "ah"),
    StationNamen("Almere Centrum", "alm")
)
