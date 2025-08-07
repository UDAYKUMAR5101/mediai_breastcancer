package com.simats.mediai_app.responses

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("image_url")
    val imageUrl: String? = null,
    
    @SerializedName("error")
    val error: String? = null
) 