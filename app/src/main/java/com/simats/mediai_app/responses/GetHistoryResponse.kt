package com.simats.mediai_app.responses

import com.google.gson.annotations.SerializedName

data class GetHistoryResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<HistoryItem>? = null
)
