package com.simats.mediai_app.responses

import android.os.Message

data class VerifyotpRequest(
    val email: String,
    val otp: String
)
data class VerifyotpResponse(
    val message: String
)