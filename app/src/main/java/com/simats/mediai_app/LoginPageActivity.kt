package com.simats.mediai_app

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.google.android.material.textfield.TextInputEditText
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
    lateinit var EmailInput: TextInputEditText
    lateinit var PasswordInput: TextInputEditText
    
    companion object {
        private const val TAG = "LoginPageActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.login_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        EmailInput = findViewById(R.id.email_input)
        PasswordInput = findViewById(R.id.password_input)

        forgetPasswordBtn = findViewById(R.id.forgot_password)
        forgetPasswordBtn.setOnClickListener {
            val intent = Intent(this , ForgotpasswordPageActivity::class.java)
            startActivity(intent)
        }
        signupBtn = findViewById(R.id.signup_link)
        signupBtn.setOnClickListener {
            val intent = Intent(this , SignuppageActivity::class.java)
            startActivity(intent)
        }
        LoginBtn = findViewById(R.id.login_button)
        LoginBtn.setOnClickListener {
            val email = EmailInput.text.toString()
            val password = PasswordInput.text.toString()

            Log.d(TAG, "Login attempt - Email: $email")

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginPageActivity , "Please fill in all fields" , Toast.LENGTH_SHORT).show()
            } else if (!isNetworkAvailable()) {
                Toast.makeText(this@LoginPageActivity , "No internet connection. Please check your network." , Toast.LENGTH_LONG).show()
            } else {
                checkLogin(email , password)
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    fun checkLogin (email : String , password : String) {
        try {
            Log.d(TAG, "Making login request to server...")
            
            retrofit2.getService(this).login(LoginRequest(email , password)).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    Log.d(TAG, "Login response received - Code: ${response.code()}")
                    
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        Log.d(TAG, "Login successful - Access token received")
                        
                        // Save authentication tokens
                        Sessions.saveAuthTokens(this@LoginPageActivity, loginResponse.access, loginResponse.refresh)
                        
                        Toast.makeText(this@LoginPageActivity , "Login Successful" , Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginPageActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close login activity so user can't go back
                    } else {
                        // Show more detailed error message
                        val errorMessage = if (response.code() == 400) {
                            "Invalid email or password. Please check your credentials."
                        } else {
                            "Login failed. Server returned code: ${response.code()}"
                        }
                        Log.e(TAG, "Login failed - $errorMessage")
                        Toast.makeText(this@LoginPageActivity , errorMessage , Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(TAG, "Login network failure", t)
                    Toast.makeText(this@LoginPageActivity , "Login Failed: ${t.message}" , Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            Toast.makeText(this@LoginPageActivity , "Login Error: ${e.message}" , Toast.LENGTH_SHORT).show()
        }
    }
}