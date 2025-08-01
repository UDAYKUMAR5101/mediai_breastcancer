package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
        signBtn = findViewById(R.id.signUpPrompt)
        signBtn.setOnClickListener {
            val fullname = fullnameinput.text.toString()
            val email = emailinput.text.toString()
            val password = passwordinput.text.toString()
            val confirmpassword = confirmpasswordinput.text.toString()
            if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@SignuppageActivity , "Please fill in all fields" , Toast.LENGTH_SHORT).show()
            } else if (password != confirmpassword) {
                Toast.makeText(this@SignuppageActivity , "Passwords do not match" , Toast.LENGTH_SHORT).show()
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
    fun checksign (fullname : String ,email : String , password : String) {
        retrofit2.getService(this).register(SignupRequest(fullname,email,password)).enqueue(object :
            Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@SignuppageActivity , "Signup Successful" , Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignuppageActivity, dashboardupdateded::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignuppageActivity , "Invalid Data" , Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                Toast.makeText(this@SignuppageActivity , "Signup Failed: ${t.message}" , Toast.LENGTH_SHORT).show()
            }
        })
    }
}