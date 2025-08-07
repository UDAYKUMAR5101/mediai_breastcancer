package com.simats.mediai_app.responses

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: ProfileData? = null,
    
    @SerializedName("success")
    val success: Boolean? = null
)
