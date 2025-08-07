package com.simats.mediai_app

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.R.id.main
import com.simats.mediai_app.responses.LoginRequest
import com.simats.mediai_app.responses.LoginResponse
import com.simats.mediai_app.responses.SignupRequest
import com.simats.mediai_app.responses.SignupResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignuppageActivity : AppCompatActivity() {
    lateinit var signBtn:TextView
    lateinit var termsBtn:TextView
    lateinit var fullnameinput : EditText
    lateinit var emailinput : EditText
    lateinit var passwordinput : EditText
    lateinit var confirmpasswordinput : EditText
    lateinit var checkboxAgree: CheckBox
    
    companion object {
        private const val TAG = "SignuppageActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signuppage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fullnameinput = findViewById(R.id.fullnameinput)
        emailinput = findViewById(R.id.emailInput)
        passwordinput = findViewById(R.id.passwordInput)
        confirmpasswordinput = findViewById(R.id.confirm_button)
        checkboxAgree = findViewById(R.id.checkboxAgree)
        signBtn = findViewById(R.id.signUpPrompt)
        signBtn.setOnClickListener {
            val fullname = fullnameinput.text.toString()
            val email = emailinput.text.toString()
            val password = passwordinput.text.toString()
            val confirmpassword = confirmpasswordinput.text.toString()
            
            Log.d(TAG, "Signup attempt - Email: $email, Username: $fullname")
            
            if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@SignuppageActivity , "Please fill in all fields" , Toast.LENGTH_SHORT).show()
            } else if (password != confirmpassword) {
                Toast.makeText(this@SignuppageActivity , "Passwords do not match" , Toast.LENGTH_SHORT).show()
            } else if (!checkboxAgree.isChecked) {
                Toast.makeText(this@SignuppageActivity , "Please agree to Terms of Service and Privacy Policy" , Toast.LENGTH_SHORT).show()
            } else if (!isNetworkAvailable()) {
                Toast.makeText(this@SignuppageActivity , "No internet connection. Please check your network." , Toast.LENGTH_LONG).show()
            } else {
                checksign(fullname ,email , password)
            }
        }
        termsBtn = findViewById(R.id.termstext)
        termsBtn.setOnClickListener {
            val intent = Intent(this , TermsconditionsActivity::class.java)
            startActivity(intent)
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

    fun checksign (fullname : String ,email : String , password : String) {
        try {
            Log.d(TAG, "Making signup request to server...")
            
            // Create SignupRequest with username field (API expects username, not fullname)
            val signupRequest = SignupRequest(username = fullname, email = email, password = password)
            
            retrofit2.getService(this).register(signupRequest).enqueue(object :
                Callback<SignupResponse> {
                override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                    Log.d(TAG, "Signup response received - Code: ${response.code()}")
                    
                    if (response.isSuccessful && response.body() != null) {
                        Log.d(TAG, "Signup successful")
                        
                        // Save user data locally for future use
                        Sessions.saveUserData(this@SignuppageActivity, fullname, email)
                        Log.d(TAG, "User data saved locally - Username: $fullname, Email: $email")
                        
                        Toast.makeText(this@SignuppageActivity , "Signup Successful" , Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignuppageActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close signup activity so user can't go back
                    } else {
                        // Show more detailed error message
                        val errorMessage = if (response.code() == 400) {
                            "Invalid data. Please check your information."
                        } else {
                            "Signup failed. Server returned code: ${response.code()}"
                        }
                        Log.e(TAG, "Signup failed - $errorMessage")
                        Toast.makeText(this@SignuppageActivity , errorMessage , Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    Log.e(TAG, "Signup network failure", t)
                    Toast.makeText(this@SignuppageActivity , "Signup Failed: ${t.message}" , Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Signup exception", e)
            Toast.makeText(this@SignuppageActivity , "Signup Error: ${e.message}" , Toast.LENGTH_SHORT).show()
        }
    }
}