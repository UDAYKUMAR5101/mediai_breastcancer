package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LogoutConfirmationActivity : AppCompatActivity() {

    private lateinit var cancelButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout_confirmation)

        // Initialize views
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        cancelButton = findViewById(R.id.cancelButton)
        logoutButton = findViewById(R.id.logoutButton)
    }

    private fun setupClickListeners() {
        // Cancel button - go to settings page
        cancelButton.setOnClickListener {
            val intent = Intent(this, settingspage::class.java)
            startActivity(intent)
            finish()
        }
        // Logout button - perform logout and navigate to login
        logoutButton.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        // TODO: Implement actual logout logic
        // Clear user session, preferences, etc.
        // Example logout implementation:
        // clearUserSession()
        // clearUserPreferences()
        // clearStoredData()
        // Navigate to login screen
        val intent = Intent(this, LoginPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 