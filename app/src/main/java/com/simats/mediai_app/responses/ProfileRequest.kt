package com.simats.mediai_app.responses

data class ProfileRequest(
    val username: String,
    val age: Int,
    val gender: String,
    val date_of_birth: String,
    val notes: String
)
