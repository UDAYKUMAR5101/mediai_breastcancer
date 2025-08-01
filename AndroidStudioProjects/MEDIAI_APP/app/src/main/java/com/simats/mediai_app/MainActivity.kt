package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.R // Make sure R is imported if it's not automatically resolved

class MainActivity : AppCompatActivity() {

    lateinit var getStartedBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Correct way to reference a layout file in Kotlin/Java
        // Layout files are referenced using R.layout.your_layout_name (without .xml extension)
        setContentView(R.layout.getstarted_page)

        // Ensure your root layout in getstarted_page.xml has an ID, e.g., android:id="@+id/main_layout"
        // Then reference it here:
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getStartedBtn = findViewById(R.id.getStartedBtn)
        getStartedBtn.setOnClickListener {
            val intent = Intent(this , getstart::class.java)
            startActivity(intent)
        }
    }
}