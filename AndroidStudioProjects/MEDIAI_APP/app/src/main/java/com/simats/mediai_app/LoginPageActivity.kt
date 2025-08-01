package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.R // Make sure R is imported if it's not automatically resolved
import com.simats.mediai_app.responses.LoginRequest
import com.simats.mediai_app.responses.LoginResponse
import com.simats.mediai_app.Sessions
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPageActivity : AppCompatActivity() {
    lateinit var forgetPasswordBtn: TextView
    lateinit var signupBtn:TextView
    lateinit var LoginBtn:TextView
    lateinit var EmailInput: EditText
    lateinit var PasswordInput: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.login_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        EmailInput = findViewById(R.id.emailInput)
        PasswordInput = findViewById(R.id.passwordInput)

        forgetPasswordBtn = findViewById(R.id.forgotPassword)
        forgetPasswordBtn.setOnClickListener {
            val intent = Intent(this , ForgotpasswordPageActivity::class.java)
            startActivity(intent)
        }
        signupBtn = findViewById(R.id.signUpLink)
        signupBtn.setOnClickListener {
            val intent = Intent(this , SignuppageActivity::class.java)
            startActivity(intent)
        }
        LoginBtn = findViewById(R.id.button)
        LoginBtn.setOnClickListener {
            val email = EmailInput.text.toString()
            val password = PasswordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginPageActivity , "Please fill in all fields" , Toast.LENGTH_SHORT).show()
            } else {
                checkLogin(email , password)
            }

        }
    }

    fun checkLogin (email : String , password : String) {
        retrofit2.getService(this).login(LoginRequest(email , password)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    // Save authentication tokens
                    Sessions.saveAuthTokens(this@LoginPageActivity, loginResponse.access, loginResponse.refresh)
                    
                    Toast.makeText(this@LoginPageActivity , "Login Successful" , Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginPageActivity, dashboardupdateded::class.java)
                    startActivity(intent)
                    finish() // Close login activity so user can't go back
                } else {
                    Toast.makeText(this@LoginPageActivity , "Invalid Login Data" , Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginPageActivity , "Login Failed: ${t.message}" , Toast.LENGTH_SHORT).show()
            }
        })
    }

}