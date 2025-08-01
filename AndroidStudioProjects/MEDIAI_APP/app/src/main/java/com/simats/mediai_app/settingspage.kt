package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View

class settingspage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settingspage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Set up profile settings card click listener
        val profileSettingsCard = findViewById<CardView>(R.id.profileSettingsCard)
        profileSettingsCard.setOnClickListener {
            val intent = Intent(this, editprofile::class.java)
            startActivity(intent)
        }
        
        // Set up change password card click listener
        val changePasswordCard = findViewById<CardView>(R.id.changePasswordCard)
        changePasswordCard.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        
        // Set up notifications card click listener
        val notificationsCard = findViewById<CardView>(R.id.notificationsCard)
        notificationsCard.setOnClickListener {
            val intent = Intent(this, notificationpage::class.java)
            startActivity(intent)
        }
        
        // Set up about app card click listener
        val aboutAppCard = findViewById<CardView>(R.id.aboutAppCard)
        aboutAppCard.setOnClickListener {
            val intent = Intent(this, AppinfopageActivity::class.java)
            startActivity(intent)
        }
        
        // Set up privacy and security card click listener
        val privacySecurityCard = findViewById<CardView>(R.id.privacySecurityCard)
        privacySecurityCard.setOnClickListener {
            val intent = Intent(this, PrivacypolicyActivity::class.java)
            startActivity(intent)
        }
        
        // Set up logout card click listener
        val logoutCard = findViewById<CardView>(R.id.logoutCard)
        logoutCard.setOnClickListener {
            val intent = Intent(this, logoutpage::class.java)
            startActivity(intent)
        }
        
        // Set up delete account card click listener
        val deleteAccountCard = findViewById<CardView>(R.id.deleteAccountCard)
        deleteAccountCard.setOnClickListener {
            val intent = Intent(this, deleteaccount::class.java)
            startActivity(intent)
        }

        // Back arrow navigation to dashboard
        findViewById<View>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, dashboardupdateded::class.java)
            startActivity(intent)
            finish()
        }
    }
}