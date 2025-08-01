package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.content.Intent

class cancerstages : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cancerstages)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cancerStages)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Back arrow navigation to breastcancerguide
        findViewById<View>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, breastcancerguide::class.java)
            startActivity(intent)
            finish()
        }
    }
}