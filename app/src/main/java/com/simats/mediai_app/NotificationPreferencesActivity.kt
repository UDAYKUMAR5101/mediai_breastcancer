package com.simats.mediai_app

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.Intent

class NotificationPreferencesActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var dailyTipsSwitch: Switch
    private lateinit var submissionRemindersSwitch: Switch
    private lateinit var riskAlertsSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_preferences)

        // Initialize views
        initializeViews()
        setupClickListeners()
        loadSavedPreferences()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        dailyTipsSwitch = findViewById(R.id.dailyTipsSwitch)
        submissionRemindersSwitch = findViewById(R.id.submissionRemindersSwitch)
        riskAlertsSwitch = findViewById(R.id.riskAlertsSwitch)
    }

    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            val intent = Intent(this, settingspage::class.java)
            startActivity(intent)
            finish()
        }

        // Daily Tips switch
        dailyTipsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference("daily_tips", isChecked)
            updateSwitchAppearance(dailyTipsSwitch, isChecked)
        }

        // Submission Reminders switch
        submissionRemindersSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference("submission_reminders", isChecked)
            updateSwitchAppearance(submissionRemindersSwitch, isChecked)
        }

        // Risk Alerts switch
        riskAlertsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationPreference("risk_alerts", isChecked)
            updateSwitchAppearance(riskAlertsSwitch, isChecked)
        }
    }

    private fun updateSwitchAppearance(switchView: Switch, isChecked: Boolean) {
        if (isChecked) {
            // On state - blue track
            switchView.trackTintList = ContextCompat.getColorStateList(this, R.color.toggle_on_track)
        } else {
            // Off state - gray track
            switchView.trackTintList = ContextCompat.getColorStateList(this, R.color.toggle_off_track)
        }
    }

    private fun loadSavedPreferences() {
        // Load saved preferences from SharedPreferences
        val sharedPreferences = getSharedPreferences("notification_preferences", MODE_PRIVATE)
        
        val dailyTipsEnabled = sharedPreferences.getBoolean("daily_tips", false)
        val submissionRemindersEnabled = sharedPreferences.getBoolean("submission_reminders", false)
        val riskAlertsEnabled = sharedPreferences.getBoolean("risk_alerts", false)

        // Set switch states
        dailyTipsSwitch.isChecked = dailyTipsEnabled
        submissionRemindersSwitch.isChecked = submissionRemindersEnabled
        riskAlertsSwitch.isChecked = riskAlertsEnabled

        // Update switch appearances
        updateSwitchAppearance(dailyTipsSwitch, dailyTipsEnabled)
        updateSwitchAppearance(submissionRemindersSwitch, submissionRemindersEnabled)
        updateSwitchAppearance(riskAlertsSwitch, riskAlertsEnabled)
    }

    private fun saveNotificationPreference(key: String, enabled: Boolean) {
        val sharedPreferences = getSharedPreferences("notification_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, enabled)
        editor.apply()

        // TODO: Implement actual notification scheduling/cancellation based on preferences
        // Example:
        // if (enabled) {
        //     scheduleNotification(key)
        // } else {
        //     cancelNotification(key)
        // }
    }

    override fun onPause() {
        super.onPause()
        // Save preferences when leaving the screen
        saveAllPreferences()
    }

    private fun saveAllPreferences() {
        val sharedPreferences = getSharedPreferences("notification_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        
        editor.putBoolean("daily_tips", dailyTipsSwitch.isChecked)
        editor.putBoolean("submission_reminders", submissionRemindersSwitch.isChecked)
        editor.putBoolean("risk_alerts", riskAlertsSwitch.isChecked)
        
        editor.apply()
    }
} 