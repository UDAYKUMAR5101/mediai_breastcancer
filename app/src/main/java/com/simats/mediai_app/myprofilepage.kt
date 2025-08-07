package com.simats.mediai_app

import android.content.Intent
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.retrofit2
import com.simats.mediai_app.responses.ProfileResponse
import com.simats.mediai_app.responses.ProfileData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class myprofilepage : AppCompatActivity() {
    
    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var completionPercentageTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var apiService: ApiService
    
    companion object {
        private const val TAG = "MyProfilePage"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_myprofilepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize views
        initializeViews()
        
        // Initialize API service
        apiService = retrofit2.getService(this)
        
        // Set up click listeners
        setupClickListeners()
        
        // Set up pull-to-refresh
        setupSwipeRefresh()
        
        // Load profile data
        loadProfileData()
    }
    
    private fun initializeViews() {
        profileImageView = findViewById(R.id.profileImage)
        usernameTextView = findViewById(R.id.profileName)
        ageTextView = findViewById(R.id.profileAge)
        progressBar = findViewById(R.id.profileCompletionProgress)
        completionPercentageTextView = findViewById(R.id.profileCompletionPercentage)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    }
    
    private fun setupClickListeners() {
        // Set up edit profile card click listener
        val editProfileCard = findViewById<CardView>(R.id.editProfileCard)
        editProfileCard.setOnClickListener {
            val intent = Intent(this, editprofile::class.java)
            startActivity(intent)
        }
        
        // Set up view history card click listener
        val viewHistoryCard = findViewById<CardView>(R.id.viewHistoryCard)
        viewHistoryCard.setOnClickListener {
            val intent = Intent(this, diagonispage::class.java)
            startActivity(intent)
        }
        
        // Set up saved reports card click listener
        val savedReportsCard = findViewById<CardView>(R.id.savedReportsCard)
        savedReportsCard.setOnClickListener {
            val intent = Intent(this, uploadedhistory::class.java)
            startActivity(intent)
        }
        
        // Set up health guide card click listener
        val healthGuideCard = findViewById<CardView>(R.id.healthGuideCard)
        healthGuideCard.setOnClickListener {
            val intent = Intent(this, healthlifesytle::class.java)
            startActivity(intent)
        }

        // Back arrow navigation to dashboard
        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
    
    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            loadProfileData()
        }
    }
    
    private fun loadProfileData() {
        val token = Sessions.getAccessToken(this)
        if (token == null) {
            Log.e(TAG, "No access token found")
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show()
            swipeRefreshLayout.isRefreshing = false
            return
        }

        apiService.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                swipeRefreshLayout.isRefreshing = false
                
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    if (profile.data != null) {
                        displayProfileData(profile.data)
                        Log.d(TAG, "Profile loaded successfully: ${profile.message}")
                    } else {
                        displayDefaultProfileData()
                        Log.d(TAG, "Profile data is null, showing default data")
                    }
                } else {
                    // Profile doesn't exist, show default data
                    displayDefaultProfileData()
                    Log.d(TAG, "Profile doesn't exist, showing default data. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e(TAG, "Error loading profile", t)
                swipeRefreshLayout.isRefreshing = false
                displayDefaultProfileData()
                Toast.makeText(this@myprofilepage, "Error loading profile: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun displayProfileData(profileData: ProfileData) {
        // Display username
        val username = profileData.username ?: "User"
        usernameTextView.text = username
        
        // Display age
        val age = profileData.age?.toString() ?: "Age not set"
        ageTextView.text = "$age years old"
        
        // Load profile image
        if (!profileData.image.isNullOrEmpty()) {
            loadProfileImage(profileData.image)
        } else {
            // Show placeholder
            profileImageView.setImageResource(R.drawable.profile_photo)
        }
        
        // Calculate and display completion percentage
        val completionPercentage = calculateProfileCompletion(profileData)
        updateCompletionUI(completionPercentage)
    }
    
    private fun displayDefaultProfileData() {
        // Display default data from local storage
        val savedUsername = Sessions.getUsername(this) ?: "User"
        usernameTextView.text = savedUsername
        ageTextView.text = "Age not set"
        profileImageView.setImageResource(R.drawable.profile_photo)
        
        // Show 0% completion for new users
        updateCompletionUI(0)
    }
    
    private fun loadProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.profile_photo)
            .error(R.drawable.profile_photo)
            .into(profileImageView)
    }
    
    private fun calculateProfileCompletion(profileData: ProfileData): Int {
        var filledFields = 0
        val totalFields = 6
        
        // Check each field
        if (!profileData.username.isNullOrEmpty()) filledFields++
        if (profileData.age != null) filledFields++
        if (!profileData.gender.isNullOrEmpty()) filledFields++
        if (!profileData.date_of_birth.isNullOrEmpty()) filledFields++
        if (!profileData.notes.isNullOrEmpty()) filledFields++
        if (!profileData.image.isNullOrEmpty()) filledFields++
        
        return (filledFields * 100) / totalFields
    }
    
    private fun updateCompletionUI(percentage: Int) {
        // Update progress bar with animation
        progressBar.progress = 0
        progressBar.post {
            progressBar.progress = percentage
        }
        
        // Update percentage text
        completionPercentageTextView.text = "$percentage%"
        
        // Update completion instruction text
        val instructionTextView = findViewById<TextView>(R.id.profileCompletionInstruction)
        when {
            percentage == 100 -> instructionTextView.text = getString(R.string.profile_completion_complete)
            percentage >= 80 -> instructionTextView.text = getString(R.string.profile_completion_almost_there)
            percentage >= 50 -> instructionTextView.text = getString(R.string.profile_completion_good_progress)
            else -> instructionTextView.text = getString(R.string.profile_completion_start)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh profile data when returning from edit profile
        loadProfileData()
    }
}