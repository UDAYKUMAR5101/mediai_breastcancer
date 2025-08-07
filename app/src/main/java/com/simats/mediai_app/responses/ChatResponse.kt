package com.simats.mediai_app.responses

data class ChatRequest(
    val message: String
)
data class ChatResponse(
    val response: String
)