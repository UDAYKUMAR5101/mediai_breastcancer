package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.content.Intent
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class todolist : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_todolist)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Back arrow navigation to dashboard
        findViewById<View>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, DashboardFragment::class.java)
            startActivity(intent)
            finish()
        }

        // Set today's date in the header
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        dateTextView?.text = dateFormat.format(Date())
    }
}