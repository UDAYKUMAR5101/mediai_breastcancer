package com.simats.mediai_app.responses

import android.os.Message

data class ChangepasswordRequest(
        var old_password: String,
        var new_password: String, )
data class ChangepasswordResponse(
    var message: String
)