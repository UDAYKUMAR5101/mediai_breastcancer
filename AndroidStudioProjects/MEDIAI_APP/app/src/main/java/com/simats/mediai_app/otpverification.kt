package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.ressetpassword
import com.simats.mediai_app.ForgotpasswordPageActivity
import android.util.Log
import com.simats.mediai_app.responses.OtpverifyRequest
import com.simats.mediai_app.responses.OtpverifyResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast

class otpverification : AppCompatActivity() {
    lateinit var verifyBtn : Button
    lateinit var backBtn : Button
    lateinit var otp1: EditText
    lateinit var otp2: EditText
    lateinit var otp3: EditText
    lateinit var otp4: EditText
    lateinit var otp5: EditText
    lateinit var otp6: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("OTPVerification", "OTPVerification activity created")
        
        try {
            Log.d("OTPVerification", "Setting up OTP verification screen")
            enableEdgeToEdge()
            Log.d("OTPVerification", "Edge to edge enabled")
            
            // Test with a simple layout first
            setContentView(R.layout.activity_otpverification)
            Log.d("OTPVerification", "Layout set successfully")
            
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            Log.d("OTPVerification", "Window insets listener set")
            
            // Initialize OTP input fields
            Log.d("OTPVerification", "Finding OTP input fields...")
            otp1 = findViewById(R.id.otp_1)
            Log.d("OTPVerification", "otp1 found")
            otp2 = findViewById(R.id.otp_2)
            Log.d("OTPVerification", "otp2 found")
            otp3 = findViewById(R.id.otp_3)
            Log.d("OTPVerification", "otp3 found")
            otp4 = findViewById(R.id.otp_4)
            Log.d("OTPVerification", "otp4 found")
            otp5 = findViewById(R.id.otp_5)
            Log.d("OTPVerification", "otp5 found")
            otp6 = findViewById(R.id.otp_6)
            Log.d("OTPVerification", "otp6 found")
            Log.d("OTPVerification", "All OTP fields found successfully")
            
            // Set up OTP input listeners for auto-focus
            setupOtpInputListeners()
            Log.d("OTPVerification", "OTP listeners set up")
            
            verifyBtn = findViewById(R.id.btn_verify)
            Log.d("OTPVerification", "verifyBtn found")
            backBtn = findViewById(R.id.iv_back)
            Log.d("OTPVerification", "backBtn found")
            Log.d("OTPVerification", "All UI elements found successfully")
            
        } catch (e: Exception) {
            Log.e("OTPVerification", "Error in onCreate: ${e.message}")
            Log.e("OTPVerification", "Exception type: ${e.javaClass.simpleName}")
            Log.e("OTPVerification", "Stack trace:")
            e.printStackTrace()
        }
        verifyBtn.setOnClickListener {
            Log.d("OTPVerification", "Verify button clicked")
            val email = intent.getStringExtra("email") ?: ""
            val otp = otp1.text.toString() + otp2.text.toString() + otp3.text.toString() +
                      otp4.text.toString() + otp5.text.toString() + otp6.text.toString()
            if (otp.length != 6) {
                Toast.makeText(this, "Please enter the 6-digit OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Email not found. Please try again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = OtpverifyRequest(email = email, otp = otp)
            retrofit2.getService(this).verifyOtp(request).enqueue(object : Callback<OtpverifyResponse> {
                override fun onResponse(call: Call<OtpverifyResponse>, response: Response<OtpverifyResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@otpverification, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@otpverification, ressetpassword::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@otpverification, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<OtpverifyResponse>, t: Throwable) {
                    Toast.makeText(this@otpverification, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        backBtn = findViewById(R.id.iv_back)
        backBtn.setOnClickListener {
            val intent = Intent(this , ForgotpasswordPageActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupOtpInputListeners() {
        // Auto-focus to next field when a digit is entered
        otp1.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s?.length == 1) {
                    otp2.requestFocus()
                }
            }
        })
        
        otp2.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s?.length == 1) {
                    otp3.requestFocus()
                }
            }
        })
        
        otp3.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s?.length == 1) {
                    otp4.requestFocus()
                }
            }
        })
        
        otp4.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s?.length == 1) {
                    otp5.requestFocus()
                }
            }
        })
        
        otp5.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s?.length == 1) {
                    otp6.requestFocus()
                }
            }
        })
        
        // Focus on first OTP field when activity starts
        otp1.requestFocus()
    }
    
    override fun onResume() {
        super.onResume()
        Log.d("OTPVerification", "OTPVerification activity resumed")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d("OTPVerification", "OTPVerification activity paused")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("OTPVerification", "OTPVerification activity destroyed")
    }
}