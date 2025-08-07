package com.simats.mediai_app.responses

import android.os.Message
data class ResetRequest(
    val email: String,
    val password: String,
    val confirm_password: String
)
data class ResetResponse(
    val message: String
)