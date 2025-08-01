package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChooseaccountActivity : AppCompatActivity() {
    lateinit var createAccountBtn : Button
    lateinit var LoginBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.chooseaccount)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createAccountBtn= findViewById(R.id.createAccountButton)
        createAccountBtn.setOnClickListener {
            val intent = Intent(this , SignuppageActivity::class.java)
            startActivity(intent)
        }

        LoginBtn= findViewById(R.id.Login)
        LoginBtn.setOnClickListener {
            val intent = Intent(this , LoginPageActivity::class.java)
            startActivity(intent)
        }
    }
}