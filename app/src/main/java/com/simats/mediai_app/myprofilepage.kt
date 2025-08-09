package com.simats.mediai_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.RetrofitClient
import com.simats.mediai_app.responses.ProfileResponse
import com.simats.mediai_app.responses.ProfileData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class myprofilepage : AppCompatActivity() {
    
    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var completionPercentageTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var apiService: ApiService
    
    // ViewModel
    private lateinit var profileViewModel: ProfileViewModel
    
    // Broadcast receiver for profile updates
    private val profileUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                if (intent?.getBooleanExtra(editprofile.EXTRA_PROFILE_UPDATED, false) == true) {
                    val profileData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(editprofile.EXTRA_PROFILE_DATA, ProfileData::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(editprofile.EXTRA_PROFILE_DATA)
                    }
                    
                    if (profileData != null) {
                        updateProfileDisplay(profileData)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in broadcast receiver", e)
            }
        }
    }
    
    companion object {
        private const val TAG = "MyProfilePage"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_myprofilepage)
        
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            
            // Initialize ViewModel
            profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
            
            // Initialize views
            initializeViews()
            
            // Initialize API service (not used for local-only display)
            apiService = RetrofitClient.getClient().create(ApiService::class.java)
            
            // Set up click listeners
            setupClickListeners()
            
            // Set up pull-to-refresh
            setupSwipeRefresh()
            
            // Register broadcast receiver for profile updates
            registerProfileUpdateReceiver()
            
            // Observe ViewModel changes
            observeViewModel()
            
            // Load local profile data
            loadProfileData()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "Error initializing profile page", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            // Unregister broadcast receiver
            unregisterReceiver(profileUpdateReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver", e)
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            // Refresh profile data when returning to this activity
            loadProfileData()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }
    
    private fun initializeViews() {
        try {
            profileImageView = findViewById(R.id.profileImage)
            usernameTextView = findViewById(R.id.profileName)
            ageTextView = findViewById(R.id.profileAge)
            progressBar = findViewById(R.id.profileCompletionProgress)
            completionPercentageTextView = findViewById(R.id.profileCompletionPercentage)
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            throw e
        }
    }
    
    private fun setupClickListeners() {
        try {
            // Set up edit profile card click listener
            val editProfileCard = findViewById<CardView>(R.id.editProfileCard)
            editProfileCard?.setOnClickListener {
                val intent = Intent(this, editprofile::class.java)
                startActivityForResult(intent, 1001) // Request code for profile edit
            }
            
            // Set up view history card click listener
            val viewHistoryCard = findViewById<CardView>(R.id.viewHistoryCard)
            viewHistoryCard?.setOnClickListener {
                val intent = Intent(this, diagonispage::class.java)
                startActivity(intent)
            }
            
            // Set up saved reports card click listener
            val savedReportsCard = findViewById<CardView>(R.id.savedReportsCard)
            savedReportsCard?.setOnClickListener {
                val intent = Intent(this, uploadedhistory::class.java)
                startActivity(intent)
            }
            
            // Set up health guide card click listener
            val healthGuideCard = findViewById<CardView>(R.id.healthGuideCard)
            healthGuideCard?.setOnClickListener {
                val intent = Intent(this, healthlifesytle::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up click listeners", e)
        }
    }
    
    private fun setupSwipeRefresh() {
        try {
            swipeRefreshLayout.setOnRefreshListener {
                loadProfileData()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up swipe refresh", e)
        }
    }
    
    private fun registerProfileUpdateReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(editprofile.ACTION_PROFILE_UPDATED)
            }
            registerReceiver(profileUpdateReceiver, filter)
        } catch (e: Exception) {
            Log.e(TAG, "Error registering receiver", e)
        }
    }
    
    private fun observeViewModel() {
        try {
            // Observe profile data changes
            profileViewModel.profileData.observe(this) { profileData ->
                profileData?.let {
                    updateProfileDisplay(it)
                }
            }
            
            // Observe profile update status
            profileViewModel.isProfileUpdated.observe(this) { isUpdated ->
                if (isUpdated) {
                    // Profile was updated, show success message
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    profileViewModel.clearProfileUpdate()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error observing ViewModel", e)
        }
    }
    
    private fun loadProfileData() {
        try {
            setLoadingState(true)
            val local = Sessions.getLocalProfile(this)
            if (local != null) {
                updateProfileDisplay(local)
            } else {
                // Fallback to minimal info
                val fallback = ProfileData(
                    username = Sessions.getUsername(this),
                    age = null,
                    gender = null,
                    date_of_birth = null,
                    notes = null,
                    image = null
                )
                updateProfileDisplay(fallback)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading local profile data", e)
        } finally {
            setLoadingState(false)
            swipeRefreshLayout.isRefreshing = false
        }
    }
    
    private fun updateProfileDisplay(profileData: ProfileData?) {
        try {
            if (profileData != null) {
                // Update username
                usernameTextView.text = profileData.username ?: "User"
                
                // Update age
                val age = profileData.age?.toString() ?: "N/A"
                ageTextView.text = "$age years old"
                
                // Update profile image
                if (!profileData.image.isNullOrEmpty()) {
                    loadProfileImage(profileData.image)
                } else {
                    profileImageView.setImageResource(R.drawable.profile_photo)
                }
                
                // Calculate and update completion percentage
                updateCompletionPercentage(profileData)
            } else {
                // Set default values
                usernameTextView.text = "User"
                ageTextView.text = "N/A"
                profileImageView.setImageResource(R.drawable.profile_photo)
                updateCompletionPercentage(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile display", e)
        }
    }
    
    private fun loadProfileImage(imageUrl: String) {
        try {
            val file = File(imageUrl)
            val source: Any = if (file.exists()) file else imageUrl
            Glide.with(this)
                .load(source)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(profileImageView)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading profile image", e)
            profileImageView.setImageResource(R.drawable.profile_photo)
        }
    }
    
    private fun updateCompletionPercentage(profileData: ProfileData?) {
        try {
            var completedFields = 0
            val totalFields = 5 // username, age, gender, date_of_birth, notes
            
            if (profileData != null) {
                if (!profileData.username.isNullOrEmpty()) completedFields++
                if (profileData.age != null) completedFields++
                if (!profileData.gender.isNullOrEmpty()) completedFields++
                if (!profileData.date_of_birth.isNullOrEmpty()) completedFields++
                if (!profileData.notes.isNullOrEmpty()) completedFields++
            }
            
            val percentage = (completedFields * 100) / totalFields
            progressBar.progress = percentage
            completionPercentageTextView.text = "$percentage% Complete"
        } catch (e: Exception) {
            Log.e(TAG, "Error updating completion percentage", e)
        }
    }
    
    private fun setLoadingState(loading: Boolean) {
        try {
            // You can add loading indicators here if needed
            if (loading) {
                // Show loading state
            } else {
                // Hide loading state
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting loading state", e)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        try {
            if (requestCode == 1001 && resultCode == RESULT_OK) {
                // Profile was updated, refresh the data
                loadProfileData()
                
                // Show success message
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onActivityResult", e)
        }
    }
}