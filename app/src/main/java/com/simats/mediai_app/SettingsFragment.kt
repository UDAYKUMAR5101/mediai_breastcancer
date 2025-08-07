package com.simats.mediai_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.Toast
import android.content.Intent

class SettingsFragment : Fragment() {

    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var emailSwitch: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initializeViews()
        
        // Set up click listeners
        setupClickListeners()
        
        // Load saved settings
        loadSettings()
    }

    private fun initializeViews() {
        notificationSwitch = view?.findViewById(R.id.notificationSwitch)!!
        emailSwitch = view?.findViewById(R.id.emailSwitch)!!
    }

    private fun setupClickListeners() {
        // Set up switch listeners
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationSetting(isChecked)
        }

        emailSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveEmailSetting(isChecked)
        }

        // Set up other click listeners for settings items
        setupSettingsItemClickListeners()
    }

    private fun setupSettingsItemClickListeners() {
        // Edit Profile
        view?.findViewById<View>(R.id.editProfileItem)?.setOnClickListener {
            navigateToEditProfile()
        }

        // Change Password
        view?.findViewById<View>(R.id.changePasswordItem)?.setOnClickListener {
            navigateToChangePassword()
        }

        // Privacy Policy
        view?.findViewById<View>(R.id.privacyPolicyItem)?.setOnClickListener {
            navigateToPrivacyPolicy()
        }

        // Terms of Service
        view?.findViewById<View>(R.id.termsOfServiceItem)?.setOnClickListener {
            navigateToTermsOfService()
        }

        // About App
        view?.findViewById<View>(R.id.aboutAppItem)?.setOnClickListener {
            navigateToAboutApp()
        }

        // Share App
        view?.findViewById<View>(R.id.shareAppItem)?.setOnClickListener {
            shareApp()
        }

        // Logout
        view?.findViewById<View>(R.id.logoutItem)?.setOnClickListener {
            logout()
        }

        // Delete Account
        view?.findViewById<View>(R.id.deleteAccountItem)?.setOnClickListener {
            deleteAccount()
        }
    }

    private fun loadSettings() {
        // Load saved settings from SharedPreferences
        // For now, we'll use default values
        notificationSwitch.isChecked = true
        emailSwitch.isChecked = false
    }

    private fun saveNotificationSetting(enabled: Boolean) {
        // Save notification setting to SharedPreferences
        Toast.makeText(context, "Notification setting saved", Toast.LENGTH_SHORT).show()
    }

    private fun saveEmailSetting(enabled: Boolean) {
        // Save email setting to SharedPreferences
        Toast.makeText(context, "Email setting saved", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToEditProfile() {
        // Navigate to existing edit profile activity
        val intent = Intent(requireContext(), editprofile::class.java)
        startActivity(intent)
    }

    private fun navigateToChangePassword() {
        // Navigate to existing change password activity
        val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPrivacyPolicy() {
        // Navigate to existing privacy policy activity
        val intent = Intent(requireContext(), PrivacypolicyActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToTermsOfService() {
        // Navigate to existing terms of service activity
        val intent = Intent(requireContext(), TermsconditionsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAboutApp() {
        // Navigate to existing about app activity
        val intent = Intent(requireContext(), AppinfopageActivity::class.java)
        startActivity(intent)
    }

    private fun shareApp() {
        // Share app functionality
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing health app!")
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Health App Recommendation")
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun logout() {
        // Show logout confirmation
        Toast.makeText(context, "Logging out...", Toast.LENGTH_SHORT).show()
        
        // Navigate to logout activity
        val intent = Intent(requireContext(), logoutpage::class.java)
        startActivity(intent)
    }

    private fun deleteAccount() {
        // Show delete account confirmation
        Toast.makeText(context, "Delete account functionality", Toast.LENGTH_SHORT).show()
        
        // Navigate to delete account activity
        val intent = Intent(requireContext(), deleteaccount::class.java)
        startActivity(intent)
    }
} 