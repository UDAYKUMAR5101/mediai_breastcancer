package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.content.Intent
import com.simats.mediai_app.responses.DeleteRequest
import com.simats.mediai_app.responses.DeleteResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.EditText
import android.widget.Toast
import com.simats.mediai_app.responses.EmailRequest
import com.simats.mediai_app.responses.EmailResponse

class deleteaccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deleteaccount)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.delete)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Back arrow navigation to settings page
        findViewById<View>(R.id.iv_back).setOnClickListener {
            val intent = Intent(this, settingspage::class.java)
            startActivity(intent)
            finish()
        }
        // Cancel button navigation to settings page
        findViewById<View>(R.id.btn_cancel).setOnClickListener {
            val intent = Intent(this, settingspage::class.java)
            startActivity(intent)
            finish()
        }
        // Delete account button integration
        findViewById<View>(R.id.btn_delete_account).setOnClickListener {
            val password = findViewById<EditText>(R.id.passwordInput).text.toString().trim()
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = DeleteRequest(password = password)
            retrofit2.getService(this).deleteAccount(request).enqueue(object : Callback<DeleteResponse> {
                override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@deleteaccount, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        // Optionally, clear user session or navigate to login/signup
                        val intent = Intent(this@deleteaccount, SignuppageActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@deleteaccount, "Failed to delete account", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Toast.makeText(this@deleteaccount, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}