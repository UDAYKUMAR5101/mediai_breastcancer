package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.LinearLayout
import android.view.View
import android.content.Intent

class breastcancerguide : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_breastcancerguide)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TODO: Replace with actual IDs from your layout if different
        val typesSection = findViewById<LinearLayout>(R.id.typesSection)
        val cancerStagesSection = findViewById<LinearLayout>(R.id.cancerStagesSection)
        val treatmentSection = findViewById<LinearLayout>(R.id.treatmentSection)
        val preventionSection = findViewById<LinearLayout>(R.id.tipscard) // TODO: Set correct ID if different

        typesSection?.setOnClickListener {
            val intent = Intent(this, stagesofcancer::class.java)
            startActivity(intent)
        }
        cancerStagesSection?.setOnClickListener {
            val intent = Intent(this, cancerstages::class.java)
            startActivity(intent)
        }
        treatmentSection?.setOnClickListener {
            val intent = Intent(this, treatmentframe::class.java)
            startActivity(intent)
        }
        preventionSection?.setOnClickListener {
            val intent = Intent(this, healthlifesytle::class.java)
            startActivity(intent)
        }
    }
}