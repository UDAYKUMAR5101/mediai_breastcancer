package com.simats.mediai_app.responses

data class SignupRequest (
    val username: String,
    val email: String,
    val password: String
)
data class SignupResponse(
    val message : String
)