package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.simats.mediai_app.responses.AuthStatusResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Check if user is already logged in
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        // First check local storage
        if (Sessions.isLoggedIn(this)) {
            // User has tokens, verify with server
            verifyAuthWithServer()
        } else {
            // No local tokens, go to login
            navigateToLogin()
        }
    }
    
    private fun verifyAuthWithServer() {
        retrofit2.getService(this).checkAuthStatus().enqueue(object : Callback<AuthStatusResponse> {
            override fun onResponse(call: Call<AuthStatusResponse>, response: Response<AuthStatusResponse>) {
                if (response.isSuccessful && response.body()?.isAuthenticated == true) {
                    // User is authenticated, go to dashboard
                    navigateToDashboard()
                } else {
                    // Token is invalid, clear local storage and go to login
                    Sessions.clearAuthTokens(this@SplashActivity)
                    navigateToLogin()
                }
            }
            
            override fun onFailure(call: Call<AuthStatusResponse>, t: Throwable) {
                // Network error, assume user is logged in if we have local tokens
                // This provides better UX when offline
                if (Sessions.isLoggedIn(this@SplashActivity)) {
                    navigateToDashboard()
                } else {
                    navigateToLogin()
                }
            }
        })
    }
    
    private fun navigateToDashboard() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, dashboardupdateded::class.java)
            startActivity(intent)
            finish()
        }, 1000) // 1 second delay for splash screen
    }
    
    private fun navigateToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000) // 1 second delay for splash screen
    }
} 