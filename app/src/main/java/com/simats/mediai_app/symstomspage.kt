package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class symstomspage : AppCompatActivity() {
    
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
    private lateinit var activityLowButton: Button
    private lateinit var activityModerateButton: Button
    private lateinit var activityHighButton: Button
    private lateinit var breastPainToggle: ToggleButton
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
    private var activityLevel: String = "low"
    private var breastPain: Boolean = false
    private var breastCancerHistory: String = "no"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symstomspage)
        
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
            activityLowButton = findViewById(R.id.activityLowButton)
            activityModerateButton = findViewById(R.id.activityModerateButton)
            activityHighButton = findViewById(R.id.activityHighButton)
            breastPainToggle = findViewById(R.id.breastPainToggle)
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

        // Breast pain toggle
        breastPainToggle.setOnCheckedChangeListener { _, isChecked ->
            breastPain = isChecked
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

            // Validate BMI
            val bmiText = bmiEditText.text?.toString() ?: ""
            if (bmiText.isNotEmpty()) {
                val bmi = bmiText.toFloatOrNull()
                if (bmi == null || bmi < 10 || bmi > 60) {
                    bmiEditText.error = "BMI must be between 10-60"
                    isValid = false
                }
            }

            // Validate menarche age
            val menarcheText = menarcheEditText.text?.toString() ?: ""
            if (menarcheText.isNotEmpty()) {
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
        // TODO: Add API integration here
        Toast.makeText(this, "API integration pending - will be added step by step", Toast.LENGTH_LONG).show()
        
        // For now, just show a success message
        submitButton.isEnabled = false
        submitButton.text = "Submitting..."
        
        // Simulate API call delay
        submitButton.postDelayed({
            submitButton.isEnabled = true
            submitButton.text = "Submit & Analyze Risk"
            Toast.makeText(this, "Symptoms data collected successfully!", Toast.LENGTH_SHORT).show()
        }, 2000)
    }
}