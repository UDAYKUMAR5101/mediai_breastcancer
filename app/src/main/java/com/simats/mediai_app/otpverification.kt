package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.responses.VerifyotpRequest
import com.simats.mediai_app.responses.VerifyotpResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class otpverification : AppCompatActivity() {
    lateinit var verifyBtn: Button
    lateinit var backBtn: Button
    lateinit var otp1: EditText
    lateinit var otp2: EditText
    lateinit var otp3: EditText
    lateinit var otp4: EditText
    lateinit var otp5: EditText
    lateinit var otp6: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otpverification)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otp1 = findViewById(R.id.otp_1)
        otp2 = findViewById(R.id.otp_2)
        otp3 = findViewById(R.id.otp_3)
        otp4 = findViewById(R.id.otp_4)
        otp5 = findViewById(R.id.otp_5)
        otp6 = findViewById(R.id.otp_6)

        verifyBtn = findViewById(R.id.btn_verify)
        backBtn = findViewById(R.id.iv_back)

        setupOtpInputListeners()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        verifyBtn.setOnClickListener {
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

            Log.d("OTPVerification", "Sending OTP verification request...")

            retrofit2.getService(this).verifyotp(VerifyotpRequest(email, otp)).enqueue(object : Callback<VerifyotpResponse> {
                override fun onResponse(call: Call<VerifyotpResponse>, response: Response<VerifyotpResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val message = response.body()!!.message
                        Toast.makeText(this@otpverification, message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@otpverification, ressetpassword::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@otpverification, "OTP verification failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VerifyotpResponse>, t: Throwable) {
                    Log.e("OTPVerification", "OTP verification failed", t)
                    Toast.makeText(this@otpverification, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupOtpInputListeners() {
        otp1.addTextChangedListener(simpleWatcher { otp2.requestFocus() })
        otp2.addTextChangedListener(simpleWatcher { otp3.requestFocus() })
        otp3.addTextChangedListener(simpleWatcher { otp4.requestFocus() })
        otp4.addTextChangedListener(simpleWatcher { otp5.requestFocus() })
        otp5.addTextChangedListener(simpleWatcher { otp6.requestFocus() })
        otp1.requestFocus()
    }

    private fun simpleWatcher(onFilled: () -> Unit): android.text.TextWatcher {
        return object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s?.length == 1) onFilled()
            }
        }
    }
}
