package com.simats.mediai_app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.responses.ProfileRequest
import com.simats.mediai_app.responses.ProfileResponse
import com.simats.mediai_app.responses.ProfileData
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class editprofile : AppCompatActivity() {
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var medicalNotesEditText: EditText
    private lateinit var fullNameCounter: TextView
    private lateinit var medicalNotesCounter: TextView
    private lateinit var saveChangesButton: Button
    
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var isProfileLoaded = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editprofile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize all views
        initializeViews()
        setupDatePicker()
        setupCharacterCounters()
        setupSaveButton()
        
        // Load existing profile data
        loadProfileData()
    }
    
    private fun initializeViews() {
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        genderEditText = findViewById(R.id.genderEditText)
        medicalNotesEditText = findViewById(R.id.medicalNotesEditText)
        fullNameCounter = findViewById(R.id.fullNameCounter)
        medicalNotesCounter = findViewById(R.id.medicalNotesCounter)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        
        // Back arrow navigation to profile page
        findViewById<View>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, myprofilepage::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun setupDatePicker() {
        // Set maximum date to 80 years ago
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -80)
        
        dateOfBirthEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    dateOfBirthEditText.setText(dateFormatter.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            
            // Set maximum date to 80 years ago
            datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
            
            // Set minimum date to 18 years ago (optional, you can remove this if you want to allow younger ages)
            val minDate = Calendar.getInstance()
            minDate.add(Calendar.YEAR, -18)
            datePickerDialog.datePicker.minDate = minDate.timeInMillis
            
            datePickerDialog.show()
        }
    }
    
    private fun setupCharacterCounters() {
        // Full name character counter
        fullNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                fullNameCounter.text = "$currentLength/50"
            }
        })
        
        // Medical notes character counter
        medicalNotesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                medicalNotesCounter.text = "$currentLength/300"
            }
        })
    }
    
    private fun setupSaveButton() {
        saveChangesButton.setOnClickListener {
            if (validateInputs()) {
                saveProfile()
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        val fullName = fullNameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val dateOfBirth = dateOfBirthEditText.text.toString().trim()
        val medicalNotes = medicalNotesEditText.text.toString().trim()
        
        if (fullName.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_full_name), Toast.LENGTH_SHORT).show()
            fullNameEditText.requestFocus()
            return false
        }
        
        if (age.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_age), Toast.LENGTH_SHORT).show()
            ageEditText.requestFocus()
            return false
        }
        
        val ageInt = age.toIntOrNull()
        if (ageInt == null || ageInt < 18 || ageInt > 100) {
            Toast.makeText(this, getString(R.string.enter_valid_age), Toast.LENGTH_SHORT).show()
            ageEditText.requestFocus()
            return false
        }
        
        if (dateOfBirth.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_date_of_birth), Toast.LENGTH_SHORT).show()
            dateOfBirthEditText.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun loadProfileData() {
        // Show loading state
        saveChangesButton.isEnabled = false
        saveChangesButton.text = getString(R.string.loading)
        
        retrofit2.getService(this).getProfile().enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                saveChangesButton.isEnabled = true
                saveChangesButton.text = getString(R.string.save_changes)
                
                if (response.isSuccessful && response.body() != null) {
                    val profileData = response.body()?.data
                    if (profileData != null) {
                        populateProfileData(profileData)
                        isProfileLoaded = true
                    } else {
                        // No existing profile data, user can create new profile
                        isProfileLoaded = false
                    }
                } else {
                    // Failed to load profile, assume new profile
                    isProfileLoaded = false
                    Toast.makeText(this@editprofile, getString(R.string.failed_to_load_profile), Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                saveChangesButton.isEnabled = true
                saveChangesButton.text = getString(R.string.save_changes)
                // Failed to load profile, assume new profile
                isProfileLoaded = false
                Toast.makeText(this@editprofile, getString(R.string.network_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun populateProfileData(profileData: ProfileData) {
        // Populate the form fields with existing data
        fullNameEditText.setText(profileData.full_name ?: "")
        ageEditText.setText(profileData.age.toString())
        genderEditText.setText(profileData.gender)
        dateOfBirthEditText.setText(profileData.date_of_birth)
        medicalNotesEditText.setText(profileData.notes)
        
        // Update character counters
        fullNameCounter.text = "${fullNameEditText.text.length}/50"
        medicalNotesCounter.text = "${medicalNotesEditText.text.length}/300"
    }
    
    private fun saveProfile() {
        val fullName = fullNameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim().toInt()
        val gender = genderEditText.text.toString()
        val dateOfBirth = dateOfBirthEditText.text.toString().trim()
        val medicalNotes = medicalNotesEditText.text.toString().trim()
        
        // For now, using a placeholder image. In a real app, you would handle image upload
        val image = ""
        
        val profileRequest = ProfileRequest(
            age = age,
            gender = gender,
            date_of_birth = dateOfBirth,
            notes = medicalNotes,
            image = image
        )
        
        // Show loading state
        saveChangesButton.isEnabled = false
        saveChangesButton.text = getString(R.string.saving)
        
        // Use PATCH if profile exists, POST if creating new profile
        val apiCall = if (isProfileLoaded) {
            retrofit2.getService(this).updateProfile(profileRequest)
        } else {
            retrofit2.getService(this).createProfile(profileRequest)
        }
        
        apiCall.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                saveChangesButton.isEnabled = true
                saveChangesButton.text = getString(R.string.save_changes)
                
                if (response.isSuccessful && response.body() != null) {
                    val message = if (isProfileLoaded) {
                        getString(R.string.profile_updated_success)
                    } else {
                        getString(R.string.profile_created_success)
                    }
                    Toast.makeText(this@editprofile, message, Toast.LENGTH_SHORT).show()
                    // Navigate back to profile page
                    val intent = Intent(this@editprofile, myprofilepage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val message = if (isProfileLoaded) {
                        getString(R.string.profile_update_failed)
                    } else {
                        getString(R.string.profile_create_failed)
                    }
                    Toast.makeText(this@editprofile, message, Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                saveChangesButton.isEnabled = true
                saveChangesButton.text = getString(R.string.save_changes)
                Toast.makeText(this@editprofile, getString(R.string.network_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }
}