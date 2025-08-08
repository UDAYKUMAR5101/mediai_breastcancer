package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.simats.mediai_app.retrofit.RetrofitClient
import com.simats.mediai_app.responses.SymptomsRequest
import com.simats.mediai_app.responses.SymptomsResponse
import com.simats.mediai_app.responses.SaveHistoryRequest
import com.simats.mediai_app.responses.SaveHistoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log


class symstomspage : AppCompatActivity() {
    
    companion object {
        private const val TAG = "symstomspage"
    }
    
    // API Service
    private lateinit var apiService: com.simats.mediai_app.retrofit.ApiService
    
    // UI Elements
    private lateinit var backButton: ImageButton
    private lateinit var ageEditText: EditText
    private lateinit var preMenopausalButton: Button
    private lateinit var postMenopausalButton: Button
    private lateinit var familyHistoryYesButton: Button
    private lateinit var familyHistoryNoButton: Button
    private lateinit var bmiEditText: EditText
    private lateinit var menarcheEditText: EditText
    private lateinit var breastfedYesButton: Button
    private lateinit var breastfedNoButton: Button
    private lateinit var alcoholYesButton: Button
    private lateinit var alcoholNoButton: Button
    private lateinit var hormonalTreatmentYesButton: Button
    private lateinit var hormonalTreatmentNoButton: Button
    private lateinit var activityLowButton: Button
    private lateinit var activityModerateButton: Button
    private lateinit var activityHighButton: Button
    private lateinit var breastPainYesButton: Button
    private lateinit var breastPainNoButton: Button
    private lateinit var breastCancerYesButton: Button
    private lateinit var breastCancerNoButton: Button
    private lateinit var additionalNotesEditText: EditText
    private lateinit var characterCount: TextView
    private lateinit var submitButton: Button

    // Data variables
    private var menopausalStatus: String = "pre_menopausal"
    private var familyHistory: String = "no"
    private var breastfed: String = "no"
    private var alcoholConsumption: String = "no"
    private var hormonalTreatmentHistory: String = "no"
    private var activityLevel: String = "low"
    private var breastPain: String = "no"
    private var breastCancerHistory: String = "no"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symstomspage)
        
        // Initialize API Service
        apiService = RetrofitClient.getClient().create(com.simats.mediai_app.retrofit.ApiService::class.java)
        
        initializeViews()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun initializeViews() {
        try {
            backButton = findViewById(R.id.backButton)
            ageEditText = findViewById(R.id.ageEditText)
            preMenopausalButton = findViewById(R.id.preMenopausalButton)
            postMenopausalButton = findViewById(R.id.postMenopausalButton)
            familyHistoryYesButton = findViewById(R.id.familyHistoryYesButton)
            familyHistoryNoButton = findViewById(R.id.familyHistoryNoButton)
            bmiEditText = findViewById(R.id.bmiEditText)
            menarcheEditText = findViewById(R.id.menarcheEditText)
            breastfedYesButton = findViewById(R.id.breastfedYesButton)
            breastfedNoButton = findViewById(R.id.breastfedNoButton)
            alcoholYesButton = findViewById(R.id.alcoholYesButton)
            alcoholNoButton = findViewById(R.id.alcoholNoButton)
            hormonalTreatmentYesButton = findViewById(R.id.hormonalTreatmentYesButton)
            hormonalTreatmentNoButton = findViewById(R.id.hormonalTreatmentNoButton)
            activityLowButton = findViewById(R.id.activityLowButton)
            activityModerateButton = findViewById(R.id.activityModerateButton)
            activityHighButton = findViewById(R.id.activityHighButton)
            breastPainYesButton = findViewById(R.id.breastPainYesButton)
            breastPainNoButton = findViewById(R.id.breastPainNoButton)
            breastCancerYesButton = findViewById(R.id.breastCancerYesButton)
            breastCancerNoButton = findViewById(R.id.breastCancerNoButton)
            additionalNotesEditText = findViewById(R.id.additionalNotesEditText)
            characterCount = findViewById(R.id.characterCount)
            submitButton = findViewById(R.id.submitButton)
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing views: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Menopausal status buttons
        preMenopausalButton.setOnClickListener {
            setMenopausalStatus("pre_menopausal")
        }
        postMenopausalButton.setOnClickListener {
            setMenopausalStatus("post_menopausal")
        }

        // Family history buttons
        familyHistoryYesButton.setOnClickListener {
            setFamilyHistory("yes")
        }
        familyHistoryNoButton.setOnClickListener {
            setFamilyHistory("no")
        }

        // Breastfed buttons
        breastfedYesButton.setOnClickListener {
            setBreastfed("yes")
        }
        breastfedNoButton.setOnClickListener {
            setBreastfed("no")
        }

        // Alcohol consumption buttons
        alcoholYesButton.setOnClickListener {
            setAlcoholConsumption("yes")
        }
        alcoholNoButton.setOnClickListener {
            setAlcoholConsumption("no")
        }

        // Hormonal treatment history buttons
        hormonalTreatmentYesButton.setOnClickListener {
            setHormonalTreatmentHistory("yes")
        }
        hormonalTreatmentNoButton.setOnClickListener {
            setHormonalTreatmentHistory("no")
        }

        // Activity level buttons
        activityLowButton.setOnClickListener {
            setActivityLevel("low")
        }
        activityModerateButton.setOnClickListener {
            setActivityLevel("moderate")
        }
        activityHighButton.setOnClickListener {
            setActivityLevel("high")
        }

        // Breast pain buttons
        breastPainYesButton.setOnClickListener {
            setBreastPain("yes")
        }
        breastPainNoButton.setOnClickListener {
            setBreastPain("no")
        }

        // Breast cancer history buttons
        breastCancerYesButton.setOnClickListener {
            setBreastCancerHistory("yes")
        }
        breastCancerNoButton.setOnClickListener {
            setBreastCancerHistory("no")
        }

        // Submit button
        submitButton.setOnClickListener {
            if (validateForm()) {
                submitSymptomsData()
            }
        }
    }

    private fun setupTextWatchers() {
        // Character count for additional notes
        additionalNotesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                characterCount.text = "$currentLength/300"
            }
        })
    }

    private fun setMenopausalStatus(status: String) {
        menopausalStatus = status
        if (status == "pre_menopausal") {
            preMenopausalButton.setBackgroundResource(R.drawable.toggle_button_active)
            preMenopausalButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            postMenopausalButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            postMenopausalButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        } else {
            postMenopausalButton.setBackgroundResource(R.drawable.toggle_button_active)
            postMenopausalButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            preMenopausalButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            preMenopausalButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        }
    }

    private fun setFamilyHistory(history: String) {
        familyHistory = history
        if (history == "yes") {
            familyHistoryYesButton.setBackgroundResource(R.drawable.toggle_button_active)
            familyHistoryYesButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            familyHistoryNoButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            familyHistoryNoButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        } else {
            familyHistoryNoButton.setBackgroundResource(R.drawable.toggle_button_active)
            familyHistoryNoButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            familyHistoryYesButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            familyHistoryYesButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        }
    }

    private fun setBreastfed(breastfedStatus: String) {
        breastfed = breastfedStatus
        if (breastfedStatus == "yes") {
            breastfedYesButton.setBackgroundResource(R.drawable.toggle_button_active)
            breastfedYesButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            breastfedNoButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            breastfedNoButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        } else {
            breastfedNoButton.setBackgroundResource(R.drawable.toggle_button_active)
            breastfedNoButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            breastfedYesButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            breastfedYesButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        }
    }

    private fun setAlcoholConsumption(consumption: String) {
        alcoholConsumption = consumption
        if (consumption == "yes") {
            alcoholYesButton.setBackgroundResource(R.drawable.toggle_button_active)
            alcoholYesButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            alcoholNoButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            alcoholNoButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        } else {
            alcoholNoButton.setBackgroundResource(R.drawable.toggle_button_active)
            alcoholNoButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            alcoholYesButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            alcoholYesButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        }
    }

    private fun setHormonalTreatmentHistory(history: String) {
        hormonalTreatmentHistory = history
        if (history == "yes") {
            hormonalTreatmentYesButton.setBackgroundResource(R.drawable.toggle_button_active)
            hormonalTreatmentYesButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            hormonalTreatmentNoButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            hormonalTreatmentNoButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        } else {
            hormonalTreatmentNoButton.setBackgroundResource(R.drawable.toggle_button_active)
            hormonalTreatmentNoButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            hormonalTreatmentYesButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            hormonalTreatmentYesButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        }
    }

    private fun setActivityLevel(level: String) {
        activityLevel = level
        // Reset all buttons to inactive
        activityLowButton.setBackgroundResource(R.drawable.toggle_button_inactive)
        activityLowButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        activityModerateButton.setBackgroundResource(R.drawable.toggle_button_inactive)
        activityModerateButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        activityHighButton.setBackgroundResource(R.drawable.toggle_button_inactive)
        activityHighButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))

        // Set the selected button to active
        when (level) {
            "low" -> {
                activityLowButton.setBackgroundResource(R.drawable.toggle_button_active)
                activityLowButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            "moderate" -> {
                activityModerateButton.setBackgroundResource(R.drawable.toggle_button_active)
                activityModerateButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            "high" -> {
                activityHighButton.setBackgroundResource(R.drawable.toggle_button_active)
                activityHighButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
        }
    }

    private fun setBreastPain(pain: String) {
        breastPain = pain
        if (pain == "yes") {
            breastPainYesButton.setBackgroundResource(R.drawable.toggle_button_active)
            breastPainYesButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            breastPainNoButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            breastPainNoButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        } else {
            breastPainNoButton.setBackgroundResource(R.drawable.toggle_button_active)
            breastPainNoButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            breastPainYesButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            breastPainYesButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        }
    }

    private fun setBreastCancerHistory(history: String) {
        breastCancerHistory = history
        if (history == "yes") {
            breastCancerYesButton.setBackgroundResource(R.drawable.toggle_button_active)
            breastCancerYesButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            breastCancerNoButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            breastCancerNoButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        } else {
            breastCancerNoButton.setBackgroundResource(R.drawable.toggle_button_active)
            breastCancerNoButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            breastCancerYesButton.setBackgroundResource(R.drawable.toggle_button_inactive)
            breastCancerYesButton.setTextColor(ContextCompat.getColor(this, R.color.settings_secondary_text))
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        try {
            // Validate age
            val ageText = ageEditText.text?.toString() ?: ""
            if (ageText.isEmpty()) {
                ageEditText.error = "Age is required"
                isValid = false
            } else {
                val age = ageText.toIntOrNull()
                if (age == null || age < 10 || age > 100) {
                    ageEditText.error = "Age must be between 10-100 years"
                    isValid = false
                }
            }

            // Validate BMI (required)
            val bmiText = bmiEditText.text?.toString() ?: ""
            if (bmiText.isEmpty()) {
                bmiEditText.error = "BMI is required"
                isValid = false
            } else {
                val bmi = bmiText.toFloatOrNull()
                if (bmi == null || bmi < 10 || bmi > 60) {
                    bmiEditText.error = "BMI must be between 10-60"
                    isValid = false
                }
            }

            // Validate menarche age (required)
            val menarcheText = menarcheEditText.text?.toString() ?: ""
            if (menarcheText.isEmpty()) {
                menarcheEditText.error = "Age at first menstruation is required"
                isValid = false
            } else {
                val menarcheAge = menarcheText.toIntOrNull()
                if (menarcheAge == null || menarcheAge < 8 || menarcheAge > 20) {
                    menarcheEditText.error = "Age at first menstruation must be between 8-20 years"
                    isValid = false
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Validation error: ${e.message}", Toast.LENGTH_LONG).show()
            isValid = false
        }

        return isValid
    }

    private fun submitSymptomsData() {
        // Check authentication
        val token = Sessions.getAccessToken(this)
        if (token == null) {
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show()
            navigateToLogin()
            return
        }
        
        // Show loading state
        submitButton.isEnabled = false
        submitButton.text = "Submitting..."
        
        // Collect form data
        val age = ageEditText.text.toString().toIntOrNull() ?: 0
        val bmiText = bmiEditText.text.toString().trim()
        val bmi: String? = if (bmiText.isEmpty()) null else bmiText
        val menarcheAge = menarcheEditText.text.toString().toIntOrNull()
        val backendMenopausalStatus = if (menopausalStatus.contains("post", ignoreCase = true)) "post" else "pre"
        
        // Create request object
        val symptomsRequest = SymptomsRequest(
            age = age,
            menopausal_status = backendMenopausalStatus,
            family_history = familyHistory,
            bmi = bmi,
            menarche_age = menarcheAge,
            breastfeeding_history = breastfed,
            alcohol_consumption = alcoholConsumption,
            hormonal_treatment_history = hormonalTreatmentHistory,
            physical_activity = activityLevel,
            breast_pain = breastPain,
            breast_cancer = breastCancerHistory
        )
        
        // Make API call with authentication
        val call = apiService.predictSymptoms("Bearer $token", symptomsRequest)
        
        call.enqueue(object : Callback<SymptomsResponse> {
            override fun onResponse(call: Call<SymptomsResponse>, response: Response<SymptomsResponse>) {
                submitButton.isEnabled = true
                submitButton.text = "Submit & Analyze Risk"
                
                if (response.isSuccessful) {
                    val symptomsResponse = response.body()
                    if (symptomsResponse != null) {
                        // Save to history
                        saveToHistory(symptomsResponse)
                        
                        // Navigate to risk level page with results
                        navigateToRiskLevelPage(symptomsResponse)
                    } else {
                        Toast.makeText(this@symstomspage, "Empty response from server", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Handle error response
                    val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { null }
                    handleSymptomsError(response.code(), errorBody)
                }
            }
            
            override fun onFailure(call: Call<SymptomsResponse>, t: Throwable) {
                submitButton.isEnabled = true
                submitButton.text = "Submit & Analyze Risk"
                
                handleSymptomsFailure(t)
            }
        })
    }
    
    private fun navigateToRiskLevelPage(symptomsResponse: SymptomsResponse) {
        try {
            // Navigate to Risk fragment host using MainActivity + pass args via intent
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "risk")
                putExtra("risk_level", symptomsResponse.risk_level)
                putExtra("prediction_percentage", symptomsResponse.prediction_percentage)
                putExtra("mode", symptomsResponse.mode)
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to risk level page", e)
            Toast.makeText(this, "Error displaying results: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun saveToHistory(response: SymptomsResponse) {
        val token = Sessions.getAccessToken(this)
        if (token == null) {
            Log.w(TAG, "No access token available for saving history")
            return
        }

        val historyRequest = SaveHistoryRequest(
            riskLevel = response.risk_level,
            predictionPercentage = response.prediction_percentage.toFloat(),
            mode = response.mode,
            createdAt = response.created_at
        )

        apiService.saveHistory("Bearer $token", historyRequest).enqueue(object : Callback<SaveHistoryResponse> {
            override fun onResponse(call: Call<SaveHistoryResponse>, response: Response<SaveHistoryResponse>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "History saved successfully")
                } else {
                    Log.w(TAG, "Failed to save history: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SaveHistoryResponse>, t: Throwable) {
                Log.e(TAG, "Error saving history", t)
            }
        })
    }
    
    private fun handleSymptomsError(errorCode: Int, serverMessage: String?) {
        val errorMessage = when (errorCode) {
            400 -> "Invalid request data. Please check your inputs."
            401 -> "Authentication failed. Please login again."
            403 -> "Access denied. Please check your permissions."
            500 -> "Server error. Please try again later."
            else -> "Network error: $errorCode"
        }
        
        val combinedMessage = if (!serverMessage.isNullOrBlank()) "$errorMessage\n$serverMessage" else errorMessage
        Toast.makeText(this, combinedMessage, Toast.LENGTH_LONG).show()
        if (!serverMessage.isNullOrBlank()) {
            Log.w(TAG, "Server error body: $serverMessage")
        }
        
        // Handle authentication errors
        if (errorCode == 401) {
            navigateToLogin()
        }
    }
    
    private fun handleSymptomsFailure(t: Throwable) {
        val errorMessage = when {
            t.message?.contains("timeout", ignoreCase = true) == true -> "Request timeout. Please try again."
            t.message?.contains("network", ignoreCase = true) == true -> "Network error. Please check your connection."
            t.message?.contains("authentication", ignoreCase = true) == true -> "Session expired. Please login again."
            else -> "Error: ${t.message}"
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        
        // Handle authentication failures
        if (t.message?.contains("authentication", ignoreCase = true) == true) {
            navigateToLogin()
        }
    }
    
    private fun navigateToLogin() {
        Sessions.clearAuthTokens(this)
        val intent = Intent(this, LoginPageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}