package com.simats.mediai_app.responses

import com.google.gson.annotations.SerializedName

data class SaveHistoryResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("id")
    val id: Int? = null
)
