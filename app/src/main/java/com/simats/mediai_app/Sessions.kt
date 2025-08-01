package com.simats.mediai_app

import android.content.Context
import android.content.SharedPreferences

object Sessions {
    private const val PREF_NAME = "MediaiAppPrefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
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
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
}