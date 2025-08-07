package com.simats.mediai_app.responses

import android.os.Message
data class ChangeRequest(
    val old_password: String,
    val new_password: String
)
data class ChangeResponse(
    val message: String
)