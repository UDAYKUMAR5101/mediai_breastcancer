package com.simats.mediai_app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.responses.DeleteAccountRequest
import com.simats.mediai_app.responses.DeleteAccountResponse
import com.simats.mediai_app.retrofit.retrofit2
import com.simats.mediai_app.Sessions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class deleteaccount : AppCompatActivity() {

    lateinit var passwordInput: EditText
    lateinit var deleteButton: Button
    lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deleteaccount)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.delete)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        passwordInput = findViewById(R.id.passwordInput)
        deleteButton = findViewById(R.id.btn_delete_account)
        cancelButton = findViewById(R.id.btn_cancel)

        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }

        deleteButton.setOnClickListener {
            val password = passwordInput.text.toString()
            if (password.isEmpty()) {
                passwordInput.error = "Please enter your password"
                return@setOnClickListener
            }
            sendDeleteAccountRequest(password)
        }
    }

    private fun sendDeleteAccountRequest(password: String) {
        val token = Sessions.getAccessToken(this)
        val request = DeleteAccountRequest(password)

        retrofit2.getService(this).deleteAccount("Bearer $token", request)
            .enqueue(object : Callback<DeleteAccountResponse> {
                override fun onResponse(call: Call<DeleteAccountResponse>, response: Response<DeleteAccountResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@deleteaccount, response.body()!!.message, Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@deleteaccount, "Incorrect password. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DeleteAccountResponse>, t: Throwable) {
                    Log.e("DeleteAccount", "Network error: ${t.message}")
                    Toast.makeText(this@deleteaccount, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
