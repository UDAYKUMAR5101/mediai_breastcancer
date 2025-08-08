package com.simats.mediai_app.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileData(
    @SerializedName("username")
    val username: String?,
    
    @SerializedName("age")
    val age: Int?,
    
    @SerializedName("gender")
    val gender: String?,
    
    @SerializedName("date_of_birth")
    val date_of_birth: String?,
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("image")
    val image: String?
) : Parcelable
