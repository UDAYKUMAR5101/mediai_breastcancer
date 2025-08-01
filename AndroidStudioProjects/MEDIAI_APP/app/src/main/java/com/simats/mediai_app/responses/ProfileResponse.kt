package com.simats.mediai_app.responses

data class ProfileRequest(
    val age: Int,
    val gender: String,
    val date_of_birth: String,
    val notes: String,
    val image: String
)

data class ProfileData(
    val age: Int,
    val gender: String,
    val date_of_birth: String,
    val notes: String,
    val image: String,
    val full_name: String? = null
)

data class ProfileResponse(
    val details: String,
    val data: ProfileData? = null
)