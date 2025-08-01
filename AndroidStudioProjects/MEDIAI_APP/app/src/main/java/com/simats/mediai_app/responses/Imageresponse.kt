package com.simats.mediai_app.responses

data class ImageRequest(
    val image: String
)

data class ImageResponse(
    val id: Int,
    val image: String,
    val uploaded_at: String
)