package com.simats.mediai_app.responses

import com.google.gson.annotations.SerializedName

data class SaveHistoryRequest(
    @SerializedName("risk_level")
    val riskLevel: String,
    
    @SerializedName("prediction_percentage")
    val predictionPercentage: Float,
    
    @SerializedName("mode")
    val mode: String,
    
    @SerializedName("created_at")
    val createdAt: String
)
