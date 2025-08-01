package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class dashboardupdateded : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboardupdateded)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Set up profile button click listener
        val profileButton = findViewById<ImageButton>(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, myprofilepage::class.java)
            startActivity(intent)
        }
        
        // Set up settings button click listener
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, settingspage::class.java)
            startActivity(intent)
        }
        
        // Set up upload image card click listener
        val uploadImageCard = findViewById<CardView>(R.id.uploadImageCard)
        uploadImageCard.setOnClickListener {
            val intent = Intent(this, UploadpageActivity::class.java)
            startActivity(intent)
        }
        
        // Set up daily check card click listener
        val dailyCheckCard = findViewById<CardView>(R.id.dailyCheckCard)
        dailyCheckCard.setOnClickListener {
            val intent = Intent(this, todolist::class.java)
            startActivity(intent)
        }
        
        // Set up symptom photo card click listener
        val symptomPhotoCard = findViewById<CardView>(R.id.symptomPhotoCard)
        symptomPhotoCard.setOnClickListener {
            val intent = Intent(this, uploadedhistory::class.java)
            startActivity(intent)
        }
        
        // Set up symptoms updated card click listener
        val symptomsUpdatedCard = findViewById<CardView>(R.id.symptomsUpdatedCard)
        symptomsUpdatedCard.setOnClickListener {
            val intent = Intent(this, documentupload::class.java)
            startActivity(intent)
        }
        
        // Set up health tips card click listener
        val healthTipsCard = findViewById<CardView>(R.id.healthTipsCard)
        healthTipsCard.setOnClickListener {
            val intent = Intent(this, healthguide::class.java)
            startActivity(intent)
        }

        // Set up symptoms card click listener
        val symptomsCard = findViewById<CardView>(R.id.symptomsCard)
        symptomsCard.setOnClickListener {
            val intent = Intent(this, symstomspage::class.java)
            startActivity(intent)
        }
        
        // Set up history card click listener
        val historyCard = findViewById<CardView>(R.id.historyCard)
        historyCard.setOnClickListener {
            val intent = Intent(this, historypage::class.java)
            startActivity(intent)
        }

        // Set up share button click listener
        val shareButton = findViewById<Button>(R.id.sharebutton)
        shareButton.setOnClickListener {
            val intent = Intent(this, sharepage::class.java)
            startActivity(intent)
        }
    }
}