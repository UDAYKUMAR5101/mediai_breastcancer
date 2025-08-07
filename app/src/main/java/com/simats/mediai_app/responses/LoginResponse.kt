package com.simats.mediai_app.responses

data class LoginRequest (
    val email : String,
    val password : String
)

data class LoginResponse (
    val refresh : String,
    val access : String
)