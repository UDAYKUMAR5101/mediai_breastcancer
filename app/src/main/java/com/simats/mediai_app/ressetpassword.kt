package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.responses.ResetRequest
import com.simats.mediai_app.responses.ResetResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ressetpassword : AppCompatActivity() {
    lateinit var newPasswordEditText: EditText
    lateinit var confirmPasswordEditText: EditText
    lateinit var resetPasswordButton: Button
    lateinit var backToLoginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ressetpassword)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUIElements()
    }

    private fun setupUIElements() {
        newPasswordEditText = findViewById(R.id.et_new_password)
        confirmPasswordEditText = findViewById(R.id.et_confirm_password)
        resetPasswordButton = findViewById(R.id.btn_reset_password)
        backToLoginText = findViewById(R.id.tv_back_to_login)

        resetPasswordButton.setOnClickListener {
            handleResetPassword()
        }

        backToLoginText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun handleResetPassword() {
        val newPassword = newPasswordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val email = intent.getStringExtra("email") ?: ""

        if (newPassword.isEmpty()) {
            newPasswordEditText.error = "Please enter a new password"
            return
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Please confirm your password"
            return
        }
        if (newPassword != confirmPassword) {
            confirmPasswordEditText.error = "Passwords don't match"
            return
        }
        if (newPassword.length < 8) {
            newPasswordEditText.error = "Password must be at least 8 characters"
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Email not found. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val resetRequest = ResetRequest(email, newPassword, confirmPassword)
        sendResetRequest(resetRequest)
    }

    private fun sendResetRequest(request: ResetRequest) {
        retrofit2.getService(this).resetPassword(request).enqueue(object : Callback<ResetResponse> {
            override fun onResponse(call: Call<ResetResponse>, response: Response<ResetResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@ressetpassword, response.body()!!.message, Toast.LENGTH_LONG).show()
                    navigateToLogin()
                } else {
                    Toast.makeText(this@ressetpassword, "Reset failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResetResponse>, t: Throwable) {
                Toast.makeText(this@ressetpassword, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
