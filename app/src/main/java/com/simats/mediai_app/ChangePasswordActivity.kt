package com.simats.mediai_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.Intent
import android.widget.Toast

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var currentPasswordEyeIcon: ImageView
    private lateinit var newPasswordEyeIcon: ImageView
    private lateinit var confirmPasswordEyeIcon: ImageView
    private lateinit var passwordStrengthText: TextView
    private lateinit var passwordStrengthBar: View
    private lateinit var requirement8CharsIcon: ImageView
    private lateinit var requirementUppercaseIcon: ImageView
    private lateinit var requirementNumberIcon: ImageView
    private lateinit var requirementSpecialIcon: ImageView
    private lateinit var saveChangesButton: Button
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passwordchangepage)

        // Initialize views
        initializeViews()
        setupPasswordVisibilityToggles()
        setupPasswordStrengthValidation()
        setupClickListeners()
    }

    private fun initializeViews() {
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        currentPasswordEyeIcon = findViewById(R.id.currentPasswordEyeIcon)
        newPasswordEyeIcon = findViewById(R.id.newPasswordEyeIcon)
        confirmPasswordEyeIcon = findViewById(R.id.confirmPasswordEyeIcon)
        passwordStrengthText = findViewById(R.id.passwordStrengthText)
        passwordStrengthBar = findViewById(R.id.passwordStrengthBar)
        requirement8CharsIcon = findViewById(R.id.requirement8CharsIcon)
        requirementUppercaseIcon = findViewById(R.id.requirementUppercaseIcon)
        requirementNumberIcon = findViewById(R.id.requirementNumberIcon)
        requirementSpecialIcon = findViewById(R.id.requirementSpecialIcon)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        backButton = findViewById(R.id.backButton)
    }

    private fun setupPasswordVisibilityToggles() {
        // Current password visibility toggle
        currentPasswordEyeIcon.setOnClickListener {
            togglePasswordVisibility(currentPasswordEditText, currentPasswordEyeIcon)
        }

        // New password visibility toggle
        newPasswordEyeIcon.setOnClickListener {
            togglePasswordVisibility(newPasswordEditText, newPasswordEyeIcon)
        }

        // Confirm password visibility toggle
        confirmPasswordEyeIcon.setOnClickListener {
            togglePasswordVisibility(confirmPasswordEditText, confirmPasswordEyeIcon)
        }
    }

    private fun togglePasswordVisibility(editText: EditText, eyeIcon: ImageView) {
        if (editText.inputType == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT) {
            // Show password
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
            eyeIcon.setImageResource(R.drawable.eye_password)
        } else {
            // Hide password
            editText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD or android.text.InputType.TYPE_CLASS_TEXT
            eyeIcon.setImageResource(R.drawable.eye_password)
        }
    }

    private fun setupPasswordStrengthValidation() {
        newPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                updatePasswordStrength(password)
                updatePasswordRequirements(password)
            }
        })
    }

    private fun updatePasswordStrength(password: String) {
        val strength = calculatePasswordStrength(password)
        
        when (strength) {
            "weak" -> {
                passwordStrengthText.text = getString(R.string.password_strength_weak)
                passwordStrengthText.setTextColor(ContextCompat.getColor(this, R.color.change_password_weak))
                passwordStrengthBar.setBackgroundColor(ContextCompat.getColor(this, R.color.change_password_weak))
            }
            "medium" -> {
                passwordStrengthText.text = getString(R.string.password_strength_medium)
                passwordStrengthText.setTextColor(ContextCompat.getColor(this, R.color.change_password_medium))
                passwordStrengthBar.setBackgroundColor(ContextCompat.getColor(this, R.color.change_password_medium))
            }
            "strong" -> {
                passwordStrengthText.text = getString(R.string.password_strength_strong)
                passwordStrengthText.setTextColor(ContextCompat.getColor(this, R.color.change_password_strong))
                passwordStrengthBar.setBackgroundColor(ContextCompat.getColor(this, R.color.change_password_strong))
            }
        }
    }

    private fun calculatePasswordStrength(password: String): String {
        var score = 0
        
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        
        return when {
            score <= 1 -> "weak"
            score <= 3 -> "medium"
            else -> "strong"
        }
    }

    private fun updatePasswordRequirements(password: String) {
        // Check each requirement and update icon colors
        val has8Chars = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        requirement8CharsIcon.setColorFilter(
            if (has8Chars) ContextCompat.getColor(this, R.color.change_password_strong)
            else ContextCompat.getColor(this, R.color.change_password_weak)
        )

        requirementUppercaseIcon.setColorFilter(
            if (hasUppercase) ContextCompat.getColor(this, R.color.change_password_strong)
            else ContextCompat.getColor(this, R.color.change_password_weak)
        )

        requirementNumberIcon.setColorFilter(
            if (hasNumber) ContextCompat.getColor(this, R.color.change_password_strong)
            else ContextCompat.getColor(this, R.color.change_password_weak)
        )

        requirementSpecialIcon.setColorFilter(
            if (hasSpecial) ContextCompat.getColor(this, R.color.change_password_strong)
            else ContextCompat.getColor(this, R.color.change_password_weak)
        )
    }

    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Save changes button
        saveChangesButton.setOnClickListener {
            savePasswordChanges()
        }
    }

    private fun savePasswordChanges() {
        val currentPassword = currentPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validate inputs
        if (currentPassword.isEmpty()) {
            currentPasswordEditText.error = "Please enter your current password"
            return
        }

        if (newPassword.isEmpty()) {
            newPasswordEditText.error = "Please enter a new password"
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Please confirm your new password"
            return
        }

        // Check if passwords match
        if (newPassword != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return
        }

        // Check password strength
        val strength = calculatePasswordStrength(newPassword)
        if (strength == "weak") {
            newPasswordEditText.error = "Password is too weak"
            return
        }

        // Show success message
        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
} 