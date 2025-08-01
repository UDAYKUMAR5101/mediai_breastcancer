package com.simats.mediai_app.responses
data class ResetpasswordRequest(
    val email: String,
    val password: String,
    val confirm_password: String
)
data class ResetpasswordResponse(
    val message: String
)