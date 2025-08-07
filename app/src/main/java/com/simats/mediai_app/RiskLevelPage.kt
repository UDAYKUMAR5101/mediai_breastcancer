package com.simats.mediai_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.Intent
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.RetrofitClient
import com.simats.mediai_app.responses.SaveHistoryRequest
import com.simats.mediai_app.responses.SaveHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RiskLevelPage : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var riskLevelTextView: TextView
    private lateinit var percentageTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var modeTextView: TextView
    private lateinit var infoCardTextView: TextView
    private lateinit var saveResultButton: Button
    private lateinit var breastCancerGuideButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_level_page)
        
        initializeViews()
        setupClickListeners()
        displayRiskResults()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        riskLevelTextView = findViewById(R.id.riskLevelTextView)
        percentageTextView = findViewById(R.id.percentageTextView)
        progressBar = findViewById(R.id.progressBar)
        modeTextView = findViewById(R.id.modeTextView)
        infoCardTextView = findViewById(R.id.infoCardTextView)
        saveResultButton = findViewById(R.id.saveResultButton)
        breastCancerGuideButton = findViewById(R.id.breastCancerGuideButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        saveResultButton.setOnClickListener {
            saveRiskResult()
        }

        breastCancerGuideButton.setOnClickListener {
            navigateToBreastCancerGuide()
        }
    }

    private fun displayRiskResults() {
        val riskLevel = intent.getStringExtra("risk_level") ?: "Low"
        val predictionPercentage = intent.getDoubleExtra("prediction_percentage", 15.0)
        val mode = intent.getStringExtra("mode") ?: "symptoms"

        // Update UI with received data
        riskLevelTextView.text = riskLevel
        percentageTextView.text = "${predictionPercentage.toInt()}%"
        progressBar.progress = predictionPercentage.toInt()
        modeTextView.text = "Mode: $mode"

        // Set color based on risk level
        val colorRes = when (riskLevel.lowercase()) {
            "high" -> R.color.risk_high
            "moderate" -> R.color.risk_moderate
            else -> R.color.risk_low
        }
        
        val color = ContextCompat.getColor(this, colorRes)
        riskLevelTextView.setTextColor(color)
        percentageTextView.setTextColor(color)

        // Update info card text based on risk level
        val infoText = when (riskLevel.lowercase()) {
            "high" -> "Based on your assessment, you have a high risk of developing breast cancer. Please consult with a healthcare provider immediately for further evaluation and screening."
            "moderate" -> "Based on your assessment, you have a moderate risk of developing breast cancer. Regular screening and consultation with a healthcare provider is recommended."
            else -> "Based on your assessment, you have a low risk of developing breast cancer. Regular screening is still recommended."
        }
        infoCardTextView.text = infoText
    }

    private fun saveRiskResult() {
        // Check if user is logged in
        if (!Sessions.isLoggedIn(this)) {
            Toast.makeText(this, "Please log in to save your results", Toast.LENGTH_LONG).show()
            return
        }

        // Get current data
        val riskLevel = intent.getStringExtra("risk_level") ?: "Low"
        val predictionPercentage = intent.getDoubleExtra("prediction_percentage", 15.0)
        val mode = intent.getStringExtra("mode") ?: "symptoms"

        // Create timestamp
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            .format(Date())

        // Create request
        val saveRequest = SaveHistoryRequest(
            riskLevel = riskLevel,
            predictionPercentage = predictionPercentage.toFloat(),
            mode = mode,
            createdAt = timestamp
        )

        // Get API service
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        val token = Sessions.getAccessToken(this)

        if (token == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_LONG).show()
            return
        }

        // Show loading
        saveResultButton.isEnabled = false
        saveResultButton.text = "Saving..."

        // Make API call
        apiService.saveHistory("Bearer $token", saveRequest).enqueue(object : Callback<SaveHistoryResponse> {
            override fun onResponse(call: Call<SaveHistoryResponse>, response: Response<SaveHistoryResponse>) {
                saveResultButton.isEnabled = true
                saveResultButton.text = "Save Result"

                if (response.isSuccessful && response.body() != null) {
                    val saveResponse = response.body()!!
                    if (saveResponse.success) {
                        Toast.makeText(this@RiskLevelPage, "Risk assessment saved successfully!", Toast.LENGTH_SHORT).show()
                        // Optionally navigate to history page
                        // val intent = Intent(this@RiskLevelPage, historypage::class.java)
                        // startActivity(intent)
                    } else {
                        Toast.makeText(this@RiskLevelPage, saveResponse.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@RiskLevelPage, "Failed to save result. Please try again.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SaveHistoryResponse>, t: Throwable) {
                saveResultButton.isEnabled = true
                saveResultButton.text = "Save Result"
                Toast.makeText(this@RiskLevelPage, "Network error. Please check your connection.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToBreastCancerGuide() {
        // Navigate to existing breast cancer guide activity
        val intent = Intent(this, breastcancerguide::class.java)
        startActivity(intent)
    }
}
