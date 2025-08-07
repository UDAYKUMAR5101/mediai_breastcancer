package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GetStartedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.getstarted_page)

        // Set up click listener for Get Started button
        findViewById<Button>(R.id.getStartedBtn).setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        // Navigate to login page
        val intent = Intent(this, LoginPageActivity::class.java)
        startActivity(intent)
        finish() // Close this activity so user can't go back
    }
} 