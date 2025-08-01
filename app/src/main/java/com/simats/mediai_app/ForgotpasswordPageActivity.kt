package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.otpverification
import android.util.Log
import com.simats.mediai_app.responses.EmailRequest
import com.simats.mediai_app.responses.EmailResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.EditText
import android.widget.Toast

class ForgotpasswordPageActivity : AppCompatActivity() {
    lateinit var changeBtn : TextView
    lateinit var backloginBtn : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.forgotpassword_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeBtn = findViewById(R.id.resetpassword)
        changeBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailInput).text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = EmailRequest(email = email)
            retrofit2.getService(this).requestOtp(request).enqueue(object : Callback<EmailResponse> {
                override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@ForgotpasswordPageActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ForgotpasswordPageActivity, otpverification::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ForgotpasswordPageActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                    Toast.makeText(this@ForgotpasswordPageActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        backloginBtn = findViewById(R.id.backToLogin)
        backloginBtn.setOnClickListener {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
        }
    }
}