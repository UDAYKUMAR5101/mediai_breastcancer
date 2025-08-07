package com.simats.mediai_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.responses.ChangeRequest
import com.simats.mediai_app.responses.ChangeResponse
import com.simats.mediai_app.retrofit.retrofit2
import com.simats.mediai_app.Sessions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class passwordchangepage : AppCompatActivity() {

    lateinit var oldPasswordInput: EditText
    lateinit var newPasswordInput: EditText
    lateinit var changePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_passwordchangepage)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        oldPasswordInput = findViewById(R.id.currentPasswordEditText)
        newPasswordInput = findViewById(R.id.newPasswordEditText)
        changePasswordButton = findViewById(R.id.confirmPasswordEditText)

        changePasswordButton.setOnClickListener {
            handleChangePassword()
        }
    }

    private fun handleChangePassword() {
        val oldPassword = oldPasswordInput.text.toString()
        val newPassword = newPasswordInput.text.toString()

        if (oldPassword.isEmpty()) {
            oldPasswordInput.error = "Enter current password"
            return
        }
        if (newPassword.isEmpty()) {
            newPasswordInput.error = "Enter new password"
            return
        }
        if (newPassword.length < 8) {
            newPasswordInput.error = "Password must be at least 8 characters"
            return
        }

        val request = ChangeRequest(oldPassword, newPassword)
        sendChangeRequest(request)
    }

    private fun sendChangeRequest(request: ChangeRequest) {
        val accessToken = Sessions.getAccessToken(this)

        retrofit2.getService(this).changePassword("Bearer $accessToken", request)
            .enqueue(object : Callback<ChangeResponse> {
                override fun onResponse(call: Call<ChangeResponse>, response: Response<ChangeResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@passwordchangepage, response.body()!!.message, Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@passwordchangepage, "Password change failed.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ChangeResponse>, t: Throwable) {
                    Log.e("ChangePassword", "Error: ${t.message}")
                    Toast.makeText(this@passwordchangepage, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
