package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class successpage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_successpage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnViewResult = findViewById<android.widget.Button>(R.id.btn_view_result)
        val btnHistoryPage = findViewById<android.widget.Button>(R.id.btn_history_page)
        btnViewResult.setOnClickListener {
            val intent = android.content.Intent(this, risklevelpage::class.java)
            startActivity(intent)
        }
        btnHistoryPage.setOnClickListener {
            val intent = android.content.Intent(this, historypage::class.java)
            startActivity(intent)
        }
    }
}