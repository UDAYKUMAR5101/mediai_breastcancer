package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class logoutpage : AppCompatActivity() {
    private lateinit var cancelButton: Button
    private lateinit var logoutButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logout_confirmation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            val intent = Intent(this , SettingsFragment::class.java)
            startActivity(intent)
        }
        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Clear locally stored session and profile
            Sessions.clearLocalProfile(this)
            Sessions.clearAuthTokens(this)
            val intent = Intent(this , LoginPageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
} 