package com.simats.mediai_app.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Add a formatted timestamp string for display
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}