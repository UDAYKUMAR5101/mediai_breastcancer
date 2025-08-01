package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View

class myprofilepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_myprofilepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Set up edit profile card click listener
        val editProfileCard = findViewById<CardView>(R.id.editProfileCard)
        editProfileCard.setOnClickListener {
            val intent = Intent(this, editprofile::class.java)
            startActivity(intent)
        }
        
        // Set up view history card click listener
        val viewHistoryCard = findViewById<CardView>(R.id.viewHistoryCard)
        viewHistoryCard.setOnClickListener {
            val intent = Intent(this, diagonispage::class.java)
            startActivity(intent)
        }
        
        // Set up saved reports card click listener
        val savedReportsCard = findViewById<CardView>(R.id.savedReportsCard)
        savedReportsCard.setOnClickListener {
            val intent = Intent(this, uploadedhistory::class.java)
            startActivity(intent)
        }
        
        // Set up health guide card click listener
        val healthGuideCard = findViewById<CardView>(R.id.healthGuideCard)
        healthGuideCard.setOnClickListener {
            val intent = Intent(this, healthlifesytle::class.java)
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