package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import com.google.android.material.button.MaterialButton
import android.content.Intent

class risklevelpage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_risklevelpage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSaveResult = findViewById<MaterialButton>(R.id.btnSaveResult)
        val btnBreastCancerGuide = findViewById<MaterialButton>(R.id.btnBreastCancerGuide)
        btnSaveResult.setOnClickListener {
            val intent = Intent(this, documentupload::class.java)
            startActivity(intent)
        }
        btnBreastCancerGuide.setOnClickListener {
            val intent = Intent(this, breastcancerguide::class.java)
            startActivity(intent)
        }
    }
}