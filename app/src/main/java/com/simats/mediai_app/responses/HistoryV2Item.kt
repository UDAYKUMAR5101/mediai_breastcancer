package com.simats.mediai_app.responses

data class HistoryV2Item(
    val id: Int,
    val symptoms: String?,
    val image_url: String?,
    val risk_level: String,
    val prediction_result: String,
    val created_at: String
)


