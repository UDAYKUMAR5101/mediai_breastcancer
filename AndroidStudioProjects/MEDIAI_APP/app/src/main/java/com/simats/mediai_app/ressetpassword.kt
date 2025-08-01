package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.LoginPageActivity
import android.util.Log
import com.simats.mediai_app.responses.ResetpasswordRequest
import com.simats.mediai_app.responses.ResetpasswordResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast

class ressetpassword : AppCompatActivity() {
    lateinit var newPasswordEditText: EditText
    lateinit var confirmPasswordEditText: EditText
    lateinit var resetPasswordButton: Button
    lateinit var backToLoginText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ResetPassword", "ResetPassword activity created")
        try {
            Log.d("ResetPassword", "Setting up ResetPassword screen")
            enableEdgeToEdge()
            setContentView(R.layout.activity_ressetpassword)
            Log.d("ResetPassword", "Layout set successfully")
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            Log.d("ResetPassword", "ResetPassword activity setup completed")
            
            // Initialize UI elements
            setupUIElements()
            
        } catch (e: Exception) {
            Log.e("ResetPassword", "Error in onCreate: ${e.message}")
            Log.e("ResetPassword", "Exception type: ${e.javaClass.simpleName}")
            Log.e("ResetPassword", "Stack trace:")
            e.printStackTrace()
        }
    }
    
    private fun setupUIElements() {
        try {
            Log.d("ResetPassword", "Setting up UI elements")
            
            // Find UI elements
            newPasswordEditText = findViewById(R.id.et_new_password)
            confirmPasswordEditText = findViewById(R.id.et_confirm_password)
            resetPasswordButton = findViewById(R.id.btn_reset_password)
            backToLoginText = findViewById(R.id.tv_back_to_login)
            
            Log.d("ResetPassword", "UI elements found successfully")
            
            // Set up button click listeners
            setupClickListeners()
            
        } catch (e: Exception) {
            Log.e("ResetPassword", "Error setting up UI elements: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun setupClickListeners() {
        // Reset Password button click listener
        resetPasswordButton.setOnClickListener {
            Log.d("ResetPassword", "Reset Password button clicked")
            try {
                val newPassword = newPasswordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()
                val email = intent.getStringExtra("email") ?: ""
                // Basic validation
                if (newPassword.isEmpty()) {
                    Log.d("ResetPassword", "New password is empty")
                    newPasswordEditText.error = "Please enter a new password"
                    return@setOnClickListener
                }
                if (confirmPassword.isEmpty()) {
                    Log.d("ResetPassword", "Confirm password is empty")
                    confirmPasswordEditText.error = "Please confirm your password"
                    return@setOnClickListener
                }
                if (newPassword != confirmPassword) {
                    Log.d("ResetPassword", "Passwords don't match")
                    confirmPasswordEditText.error = "Passwords don't match"
                    return@setOnClickListener
                }
                if (newPassword.length < 8) {
                    Log.d("ResetPassword", "Password too short")
                    newPasswordEditText.error = "Password must be at least 8 characters"
                    return@setOnClickListener
                }
                if (email.isEmpty()) {
                    Toast.makeText(this, "Email not found. Please try again.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Call the reset password API
                val request = ResetpasswordRequest(
                    email = email,
                    password = newPassword,
                    confirm_password = confirmPassword
                )
                retrofit2.getService(this).resetPassword(request).enqueue(object : Callback<ResetpasswordResponse> {
                    override fun onResponse(call: Call<ResetpasswordResponse>, response: Response<ResetpasswordResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            Toast.makeText(this@ressetpassword, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            navigateToLogin()
                        } else {
                            Toast.makeText(this@ressetpassword, "Failed to reset password", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<ResetpasswordResponse>, t: Throwable) {
                        Toast.makeText(this@ressetpassword, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: Exception) {
                Log.e("ResetPassword", "Error in reset password: ${e.message}")
                e.printStackTrace()
            }
        }
        // Back to Login text click listener
        backToLoginText.setOnClickListener {
            Log.d("ResetPassword", "Back to Login clicked")
            navigateToLogin()
        }
    }
    
    private fun navigateToLogin() {
        try {
            Log.d("ResetPassword", "Navigating to LoginPageActivity")
            val intent = Intent(this, LoginPageActivity::class.java)
            
            // Clear the activity stack so user can't go back to password reset screens
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            startActivity(intent)
            Log.d("ResetPassword", "Successfully navigated to login page")
            
        } catch (e: Exception) {
            Log.e("ResetPassword", "Error navigating to login: ${e.message}")
            e.printStackTrace()
        }
    }
}