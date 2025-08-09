package com.simats.mediai_app

import android.content.Context
import android.content.SharedPreferences

object Sessions {
    private const val PREF_NAME = "MediaiAppPrefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USERNAME = "username"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_CHAT_HISTORY = "chat_history"
    private const val KEY_LOCAL_HISTORY_JSON = "local_history_json"
    // Local profile keys
    private const val KEY_PROFILE_USERNAME = "profile_username"
    private const val KEY_PROFILE_AGE = "profile_age"
    private const val KEY_PROFILE_GENDER = "profile_gender"
    private const val KEY_PROFILE_DOB = "profile_dob"
    private const val KEY_PROFILE_NOTES = "profile_notes"
    private const val KEY_PROFILE_IMAGE_PATH = "profile_image_path"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveAuthTokens(context: Context, accessToken: String, refreshToken: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    fun getAccessToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null)
    }
    
    fun getRefreshToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_REFRESH_TOKEN, null)
    }
    
    fun isLoggedIn(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun clearAuthTokens(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USERNAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_LOCAL_HISTORY_JSON)
            .remove(KEY_PROFILE_USERNAME)
            .remove(KEY_PROFILE_AGE)
            .remove(KEY_PROFILE_GENDER)
            .remove(KEY_PROFILE_DOB)
            .remove(KEY_PROFILE_NOTES)
            .remove(KEY_PROFILE_IMAGE_PATH)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
    
    // User data management methods
    fun saveUserData(context: Context, username: String, email: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }
    
    fun getUsername(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USERNAME, null)
    }
    
    fun getUserEmail(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, null)
    }
    
    fun saveUserProfile(context: Context, username: String, email: String, accessToken: String, refreshToken: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_USERNAME, username)
            .putString(KEY_USER_EMAIL, email)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    fun saveChatHistory(context: Context, chatHistory: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putString(KEY_CHAT_HISTORY, chatHistory)
            .apply()
    }
    
    fun getChatHistory(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_CHAT_HISTORY, null)
    }

    // Local history cache (JSON string of server HistoryItem list)
    fun saveLocalHistoryJson(context: Context, json: String) {
        getSharedPreferences(context).edit().putString(KEY_LOCAL_HISTORY_JSON, json).apply()
    }

    fun getLocalHistoryJson(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_LOCAL_HISTORY_JSON, null)
    }

    // Local profile storage (no API): save, get, clear
    fun saveLocalProfile(
        context: Context,
        username: String?,
        age: Int?,
        gender: String?,
        dateOfBirth: String?,
        notes: String?,
        imagePath: String?
    ) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putString(KEY_PROFILE_USERNAME, username)
            .putInt(KEY_PROFILE_AGE, age ?: -1)
            .putString(KEY_PROFILE_GENDER, gender)
            .putString(KEY_PROFILE_DOB, dateOfBirth)
            .putString(KEY_PROFILE_NOTES, notes)
            .putString(KEY_PROFILE_IMAGE_PATH, imagePath)
            .apply()
    }

    fun getLocalProfile(context: Context): com.simats.mediai_app.responses.ProfileData? {
        val prefs = getSharedPreferences(context)
        val username = prefs.getString(KEY_PROFILE_USERNAME, null)
        val storedAge = prefs.getInt(KEY_PROFILE_AGE, -1)
        val age = if (storedAge >= 0) storedAge else null
        val gender = prefs.getString(KEY_PROFILE_GENDER, null)
        val dob = prefs.getString(KEY_PROFILE_DOB, null)
        val notes = prefs.getString(KEY_PROFILE_NOTES, null)
        val imagePath = prefs.getString(KEY_PROFILE_IMAGE_PATH, null)

        val hasAny = listOf(username, age, gender, dob, notes, imagePath).any {
            when (it) {
                is String? -> !it.isNullOrEmpty()
                is Int? -> it != null
                else -> false
            }
        }
        return if (hasAny) com.simats.mediai_app.responses.ProfileData(
            username = username,
            age = age,
            gender = gender,
            date_of_birth = dob,
            notes = notes,
            image = imagePath
        ) else null
    }

    fun clearLocalProfile(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .remove(KEY_PROFILE_USERNAME)
            .remove(KEY_PROFILE_AGE)
            .remove(KEY_PROFILE_GENDER)
            .remove(KEY_PROFILE_DOB)
            .remove(KEY_PROFILE_NOTES)
            .remove(KEY_PROFILE_IMAGE_PATH)
            .apply()
    }
}