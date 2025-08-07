package com.simats.mediai_app.responses

data class ImagePredictionResponse(
    val id: Int,
    val risk_level: String,
    val prediction_percentage: Double,
    val mode: String,
    val created_at: String,
    val user: Int
)
