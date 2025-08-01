package com.simats.mediai_app.responses
data class OtpverifyRequest(
    val email: String,
    val otp: String
)
data class OtpverifyResponse(
    val message: String
)