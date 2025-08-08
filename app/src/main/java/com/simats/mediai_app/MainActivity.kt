package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        // Set up Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Set up bottom navigation colors
        bottomNav.setItemIconTintList(null)
        bottomNav.setItemTextColor(null)

        // Optional: handle navigation intent for risk results
        handleNavigationIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let { handleNavigationIntent(it) }
    }

    private fun handleNavigationIntent(intent: Intent) {
        if (intent.getStringExtra("navigate_to") == "risk") {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            // Post to ensure NavHost is ready
            Handler(Looper.getMainLooper()).post {
                navController.navigate(R.id.nav_risk, Bundle().apply {
                    putString("risk_level", intent.getStringExtra("risk_level"))
                    putDouble("prediction_percentage", intent.getDoubleExtra("prediction_percentage", 0.0))
                    putString("mode", intent.getStringExtra("mode") ?: "image")
                })
            }
        }
    }
}