package com.simats.mediai_app.retrofit

import com.simats.mediai_app.responses.ChatRequest
import com.simats.mediai_app.responses.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatService {
    @POST("api/api/chatbot/") // adjust this to your actual backend path
    fun chatbot(
        @Header("Authorization") token: String,
        @Body chatbotRequest: ChatRequest
    ): Call<ChatResponse>
}
