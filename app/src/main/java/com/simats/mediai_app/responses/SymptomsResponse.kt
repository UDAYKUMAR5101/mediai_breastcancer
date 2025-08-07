package com.simats.mediai_app.responses

data class SymptomsResponse(
    val risk_level: String,
    val prediction_percentage: Double,
    val mode: String
)
