package com.simats.mediai_app.responses

data class DeleteAccountRequest (
    var password: String
)
data class DeleteAccountResponse (
    var message: String
)